package com.lw.novelreader;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.htmlparser.util.ParserException;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.db.DBUtil;
import com.lw.novel.common.FileUtil;
import com.lw.ttzw.DataQueryManager;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask implements Runnable {

	private static final String TAG = "DownloadTask";

	public int startChapter;
	public int length;
	
	public boolean notify;

	public Novel novel;

	public boolean isStartDownload = false;

	public boolean isCancel = false;

	public boolean isFinish = false;
	
	private SparseArray<List<Integer>> mDownloading = new SparseArray<List<Integer>>();
	
	OkHttpClient client = new OkHttpClient();

	public DownloadTask(Novel novel, int start, int len) {
		this.startChapter = start;
		this.length = len;
		this.novel = novel;
	}
	
	public DownloadTask(Novel novel, int start, int len,boolean notify) {
		this.startChapter = start;
		this.length = len;
		this.novel = novel;
		this.notify = notify;
	}

	@Override
	public void run() {
		DownloadMessage startMsg = new DownloadMessage(novel.getId(), DownloadMessage.STATUS_START,this);
		if(notify)
			EventBus.getDefault().postSticky(startMsg);
		int bookid = novel.getId();
		System.out.println("DownloadTask bookid=" + bookid);
		if (bookid <= 0) {
			return;
		}
		isStartDownload = true;
		List<Chapter> chapters = DBUtil.queryNovelChapterList(bookid);
		if(startChapter < 0) {
			startChapter = 0;
		}
		if(length < 0) {
			length = chapters.size();
		}
		if (startChapter + length > chapters.size()) {
			length = chapters.size() - startChapter;
		}
		int limit = startChapter + length;
		for (int i = startChapter; i < limit; i++) {
			if (isCancel) {
				if(notify)
					EventBus.getDefault().post(new DownloadMessage(novel.getId(), DownloadMessage.STATUS_CANCEL,this));
				isStartDownload = false;
				return;
			}
			final Chapter chapter = chapters.get(i);
			if(notify)
				EventBus.getDefault().post(new DownloadProgress(novel.getId(), i + 1, chapters.size()));
			if (FileUtil.isChapterExist(novel,chapter))
				continue;
			List<Integer> ids = mDownloading.get(bookid);
			if(ids != null) {
				if(ids.contains(i)) {
					continue;
				} else {
					ids.add(i);
				}
			} else {
				ids = new ArrayList<Integer>();
				ids.add(i);
				mDownloading.put(bookid, ids);
			}
			boolean s = download(chapter);
			if (!s) {
				final int a = i;
				final List<Integer> cids = ids;
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						OkHttpClient client = new OkHttpClient();
						for (int i = 0; i < 3; i++) {
							Log.v(TAG, "USE async download again! chapter = " + chapter.getTitle() + ",i=" + i);
							Request request = new Request.Builder().url(chapter.getUrl()).build();
							try {
								Response response = client.newCall(request).execute();
								String content = DataQueryManager.instance().getChapterContent(response.body().string());
								FileUtil.saveChapter(novel.getName(), novel.getAuthor(), chapter.getTitle(), content);
								cids.remove(new Integer(a));
								return null;
							} catch (SocketTimeoutException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (ParserException e) {
								e.printStackTrace();
							}
						}
						cids.remove(new Integer(a));
						return null;
					}
				}.execute();
			} else {
				ids.remove(new Integer(i));
			}
		}
		isFinish = true;
		isStartDownload = false;
		if(notify)
			EventBus.getDefault().removeStickyEvent(startMsg);
//		if(notify)
			EventBus.getDefault().post(new DownloadMessage(novel.getId(), DownloadMessage.STATUS_COMPLETED,this));
	}

	private boolean download(Chapter chapter) {
		System.out.println("Download " + chapter.getTitle() + ",url = " + chapter.getUrl());
		Request request = new Request.Builder().url(chapter.getUrl()).build();
		try {
			Response response = client.newCall(request).execute();
			String content = DataQueryManager.instance().getChapterContent(response.body().string());
			FileUtil.saveChapter(novel.getName(), novel.getAuthor(), chapter.getTitle(), content);
			return true;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
