package com.lw.widget;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.lw.bean.Novel;
import com.lw.novelreader.DownloadMessage;
import com.lw.novelreader.DownloadProgress;
import com.lw.novelreader.DownloadService;
import com.lw.novelreader.DownloadTask;
import com.lw.novelreader.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class DownloadText extends TextView implements android.view.View.OnClickListener{
	
	private Novel mNovel;
	private int bookId;
	private int status;
	public DownloadText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	

	private void init() {
//		setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
//			
//			@Override
//			public void onSystemUiVisibilityChange(int visibility) {
//				System.out.println("visibility = " + visibility);
//				if(visibility != View.VISIBLE) {
//					EventBus.getDefault().unregister(this);
//				} else {
//					EventBus.getDefault().register(this);
//				}
//			}
//		});
		setOnClickListener(this);
	}
	
	
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if(visibility != View.VISIBLE) {
			if(EventBus.getDefault().isRegistered(this))
				EventBus.getDefault().unregister(this);
		} else {
			if(!EventBus.getDefault().isRegistered(this))
				EventBus.getDefault().register(this);
		}
	}
	
	public void bindText(Novel novel) {
		mNovel = novel;
		bookId = mNovel.getId();
		status = DownloadService.isDownloading(bookId) ? DownloadMessage.STATUS_START : 0;
		if(status == DownloadMessage.STATUS_START) {
			setText(R.string.download_prepare);
		} else if(status == DownloadMessage.STATUS_COMPLETED) {
			setText(R.string.download_complete);
		} else if(status == DownloadMessage.STATUS_CANCEL) {
			setText(R.string.download_continue);
		} else {
			setText(R.string.download);
		}
	}

	@Subscribe(threadMode=ThreadMode.MAIN)
	public void onMsg(DownloadMessage msg) {
		if(bookId != msg.bookId)
			return;
		this.status = msg.status;
		if(status == DownloadMessage.STATUS_START) {
			setText(R.string.download_prepare);
		} else if(status == DownloadMessage.STATUS_COMPLETED) {
			setText(R.string.download_complete);
		} else if(status == DownloadMessage.STATUS_CANCEL) {
			setText(R.string.download_continue);
		}
	}
	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void onProgress(DownloadProgress progress) {
		if(bookId != progress.bookid)
			return;
//		status = DownloadMessage.STATUS_START;
		setText(progress.current + "/" + progress.total);
	}


	@Override
	public void onClick(View v) {
		System.out.println("onclick status = " + status);
		if(status == DownloadMessage.STATUS_START) {
			DownloadService.cancelDownload(bookId);
		} else {
			DownloadService.addToDownload(new DownloadTask(mNovel, 0, -1, true));
		}
	}
	
	

}
