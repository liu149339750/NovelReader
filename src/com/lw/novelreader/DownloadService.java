package com.lw.novelreader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service {

	ExecutorService mExecutor;

	private static final String TAG = "DownloadService";

	private static List<DownloadTask> mDownloadQueues = new ArrayList<DownloadTask>();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mExecutor = Executors.newFixedThreadPool(5);
		System.out.println("DownloadService onCreate");
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public synchronized void download(DownloadTask task) {
		System.out.println("download > " + task.startChapter);
		for (DownloadTask dt : mDownloadQueues) {
			if (dt.novel.getId() == task.novel.getId()) {
				Log.e(TAG, "the book has in downloading,id=" + task.novel.getId());
				if(dt.startChapter == task.startChapter && dt.length == task.length) {
					Log.e(TAG, "repeat request,prevent!,id=" + task.novel.getId());
					return;
				}
			}
		}
		mDownloadQueues.add(task);
		mExecutor.execute(task);
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public synchronized void msgDownload(DownloadMessage msg) {
		if (msg.status == DownloadMessage.STATUS_COMPLETED) {
			Log.e(TAG, "finish downloading,id=" + msg.bookId);
			mDownloadQueues.remove(msg.task);
//			for (DownloadTask dt : mDownloadQueues) {
//				if (dt.novel.getId() == msg.bookId) {
//					Log.e(TAG, "finish downloading,id=" + msg.bookId);
//					mDownloadQueues.remove(dt);
//					return;
//				}
//			}
		}
	}

	public synchronized static void addToDownload(DownloadTask task) {
		System.out.println("addToDownload");
		EventBus.getDefault().post(task);
	}

	public synchronized static void cancelDownload(int bookid) {
		for (int i=0;i<mDownloadQueues.size();i++) {
			DownloadTask dt = mDownloadQueues.get(i);
			if (dt.novel.getId() == bookid) {
				Log.e(TAG, "cancel downloading,id=" + bookid);
				dt.isCancel = true;
				mDownloadQueues.remove(dt);
				i --;
			}
		}
	}
	
	public static boolean isDownloading(int bookid) {
		for (int i=0;i<mDownloadQueues.size();i++) {
			DownloadTask dt = mDownloadQueues.get(i);
			if (dt.novel.getId() == bookid) {
				if(dt.isStartDownload) {
					return true;
				}
			}
		}
		return false;
	}

}
