package com.lw.model;

import org.htmlparser.util.ParserException;

import com.lw.bean.NovelDetail;
import com.lw.db.DBUtil;
import com.lw.ttzw.NovelManager;
import com.lw.ttzw.TTZWManager;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.LruCache;

public class NovelBiz implements INovelBiz {

	private static LruCache<String, NovelDetail> caches = new LruCache<String, NovelDetail>(10);
	
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
				
				try {
					System.out.println("begin query");
					NovelDetail detail = DBUtil.queryNovelDetail(NovelManager.getInstance().getCurrentNovel());
					if(detail != null) {
						System.out.println("use DB");
						caches.put(params[0], detail);
						return detail;
					}
					System.out.println("begin read from net");
					if(isCancel) {
						return null;
					}
					NovelDetail nd = TTZWManager.getNovelDetailByMeta(params[0]);
					System.out.println("read over");
					Uri uri = DBUtil.saveBookInfo(nd.getNovel());
					int id = Integer.parseInt(uri.getLastPathSegment());
					nd.getNovel().setId(id);
					int c = DBUtil.saveChaptersToDb(id, nd.getChapters());
					System.out.println("c="+c);
					caches.put(params[0], nd);
					return nd;
				} catch (ParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
			

			@Override
			protected void onProgressUpdate(Void... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
			}



			@Override
			protected void onPostExecute(NovelDetail result) {
				if(result != null && !isCancel) {
					listener.onSucess(result);
				} else {
					listener.onFail();
				}
			}
			
		}.execute(url);
		
	}
	@Override
	public void cancel() {
		
	}

}
