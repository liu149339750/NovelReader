package com.lw.model;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.util.ParserException;

import com.lw.bean.Chapter;
import com.lw.bean.Chapter.Chapters;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.ShelftBook;
import com.lw.db.DBUtil;
import com.lw.novel.utils.FileUtil;
import com.lw.novel.utils.LogUtils;
import com.lw.ttzw.DataQueryManager;
import com.lw.ttzw.NovelManager;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;

public class NovelBiz implements INovelBiz {

	private static LruCache<String, NovelDetail> caches = new LruCache<String, NovelDetail>(10);
	private static final String TAG = "NovelBiz";
	private boolean isCancel;
	@Override
	public void getNovelInfo(String url, final OnDataListener listener) {
		NovelDetail n = caches.get(url);
		if(n != null) {
			System.out.println("use cache");
			listener.onSucess(n);
			return;
		}
		new AsyncTask<String, Void, NovelDetail>() {

			@Override
			protected NovelDetail doInBackground(String... params) {
				String url = params[0];
				try {
					
//					System.out.println("begin query");
//					NovelDetail detail = DBUtil.queryNovelDetail(NovelManager.getInstance().getCurrentNovel());
//					if(detail != null) {
//						System.out.println("use DB");
//						caches.put(params[0], detail);
//						return detail;
//					}
					System.out.println("begin read from net,url="+url);
					LogUtils.v(TAG, "begin read from net,url="+url);
					if(isCancel) {
					    LogUtils.v(TAG, "cancel,url="+url);
						return null;
					}
					NovelDetail nd = DataQueryManager.instance().getNovelDetail(url);
					System.out.println("read over");
					LogUtils.v(TAG, "read over,Novel = " + nd.getNovel());
					Novel novel = nd.getNovel();
					if(novel.getName() == null) {
						return null;
					}
//					DBUtil.deleteNovelByNameAndAuthor(novel.getName(), novel.getAuthor());
					Novel old = DBUtil.queryNovelByUrl(url);
					int id = 0;
					int c = 0;
					Chapters chs = new Chapters();
					chs.source = DataQueryManager.instance().queryTag(url);
					chs.chapters = nd.getChapters();
					if(old == null) {
						Uri uri = DBUtil.saveBookInfo(nd.getNovel());
						id = Integer.parseInt(uri.getLastPathSegment());
//						c = DBUtil.saveChaptersToDb(id, nd.getChapters());
						DBUtil.saveBookSource(id, DataQueryManager.instance().queryTag(url), url, nd.getChapterUrl(),null);
					} else {
						id = old.id;
						DBUtil.updateNovelById(old.id, novel);
						if (NovelManager.getInstance().isInbookShelft(id)) {
							List<Chapter> chapters = DBUtil
									.queryNovelChapterList(id);
							List<Chapter> newChapters = nd.getChapters();

							if (chapters.size() < newChapters.size()) {
								List<Chapter> newInsert = new ArrayList<Chapter>();
								for (int i = chapters.size(); i < newChapters
										.size(); i++) {
									newInsert.add(newChapters.get(i));
								}
								c = DBUtil.saveChaptersToDb(id, newInsert);
								if (c > 0) {
									DBUtil.updateChapterCount(id,
											newChapters.size());
								}
							}
						}
					}
					chs.bookid = id;
					String path = FileUtil.saveChapterList(chs);
					DBUtil.updateChaptersFileUrl(id, DataQueryManager.instance().queryTag(url), path);
					novel.setId(id);
//					int c = DBUtil.saveChaptersToDb(id, nd.getChapters());
					System.out.println("c="+c);
					caches.put(url, nd);
					return nd;
				} catch (ParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LogUtils.e(TAG, "getNovelInfo",e);
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(NovelDetail result) {
				if(isCancel) {
					listener.onCancel();
				} else if(result != null) {
					listener.onSucess(result);
				} else {
					listener.onFail();
				}
			}
			
		}.execute(url);
		
	}
	
	/**this methon is used when pull to update bookshelft*/
	public NovelDetail getNovelInfo(String url) {
//		NovelDetail n = caches.get(url);
//		if(n != null) {
//			System.out.println("use cache");
//			return n;
//		}
		try {
			System.out.println("begin read from net url = " + url);
			if(isCancel) {
//				listener.onCancel();
				return null;
			}
			NovelDetail nd = DataQueryManager.instance().getNovelDetail(url);
			System.out.println("NovelBiz read over");
			Novel novel = nd.getNovel();
			if(novel == null || novel.getName() == null || nd.getChapters() == null) {
				LogUtils.v(TAG, "get novel info fail,url="+url);
				return null;
			}
//			DBUtil.deleteNovelByNameAndAuthor(novel.getName(), novel.getAuthor());
			Novel old = DBUtil.queryNovelByUrl(url);
			int id = 0;
			int c = 0;
			//save chapters to file
            Chapters chs = new Chapters();
            chs.source = DataQueryManager.instance().queryTag(url);
            chs.chapters = nd.getChapters();
            
			if(old == null) {
				Uri uri = DBUtil.saveBookInfo(nd.getNovel());
				id = Integer.parseInt(uri.getLastPathSegment());
				c = DBUtil.saveChaptersToDb(id, nd.getChapters());
				DBUtil.saveBookSource(id, DataQueryManager.instance().queryTag(url), url, nd.getChapterUrl(),null);
			} else {
				id = old.id;
				DBUtil.updateNovelById(old.id, novel);
				List<Chapter> chapters = DBUtil.queryNovelChapterList(id);
				List<Chapter> newChapters = nd.getChapters();
				if(chapters.size() < newChapters.size()) {
					System.out.println("insert new > " + (newChapters.size() - chapters.size()));
					List<Chapter> newInsert = new ArrayList<Chapter>();
					for(int i=chapters.size();i<newChapters.size();i++) {
						newInsert.add(newChapters.get(i));
					}
					c = DBUtil.saveChaptersToDb(id, newInsert);
					if(c > 0) {
						DBUtil.updateChapterCount(id, newChapters.size());
					}
				}
			}
			
			chs.bookid = id;
            String path = FileUtil.saveChapterList(chs);
            DBUtil.updateChaptersFileUrl(id, DataQueryManager.instance().queryTag(url), path);
            
			novel.setId(id);
			System.out.println("c="+c);
			caches.put(url, nd);
			return nd;
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void cancel() {
		isCancel = true;
	}

}
