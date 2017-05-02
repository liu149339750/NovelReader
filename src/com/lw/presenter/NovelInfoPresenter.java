package com.lw.presenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.model.INovelBiz;
import com.lw.model.NovelBiz;
import com.lw.model.OnDataListener;
import com.lw.novelreader.DownloadMessage;
import com.lw.novelreader.DownloadProgress;
import com.lw.novelreader.DownloadService;
import com.lw.novelreader.DownloadStatus;
import com.lw.novelreader.DownloadTask;
import com.lw.ttzw.NovelManager;
import com.lw.ui.fragment.INovelInfoView;

public class NovelInfoPresenter {

	private INovelInfoView mIView;
	private INovelBiz mBiz;
	
	private Novel mNovel;
	
	public NovelInfoPresenter(INovelInfoView view) {
		mIView = view;
		mBiz = new NovelBiz();
	}
	
	public void getNovelInfo() {
		mIView.showLoading();
		final Novel novel = NovelManager.getInstance().getCurrentNovel();
		mBiz.getNovelInfo(novel.getUrl(), new OnDataListener() {
			
			@Override
			public void onSucess(NovelDetail nd) {
				mNovel = nd.getNovel();
				novel.setId(nd.getNovel().getId());
				mIView.hideLoading();
				mIView.showNovelInfo(nd.getNovel());
				NovelManager.getInstance().setChapers(nd.getChapters());
//				NovelManager.getInstance().setCurrentNovel(novel.getNovel());
			}
			
			@Override
			public void onFail() {
				mIView.hideLoading();
				mIView.onLoadFail();
			}

			@Override
			public void onCancel() {
				
			}
		});
	}
	
	public void readNow() {
		mIView.startRead();
	}
	
	
	public void addOrRemoveShelft() {
		if(mIView.isInbookShelf(mNovel.id)) {
			mIView.removeBookShelt(mNovel.id);
		} else {
			mIView.addBookShelft(mNovel.id);
		}
	}
	
	public void downloadBook() {
		int status = mIView.getDownloadState();
		
		if(status == DownloadStatus.DOWNLOAD_START) {
			DownloadService.cancelDownload(mNovel.id);
			mIView.showDownloadPause();
		} else if(status == DownloadStatus.DOWNLOAD_PAUSE || status == DownloadStatus.DOWNLOAD_IDLE) {
			if(!mIView.isInbookShelf(mNovel.getId()))
				mIView.addBookShelft(mNovel.id);
			DownloadService.addToDownload(new DownloadTask(mNovel, 0, -1,true));
			mIView.showDownloadBook();
		}
	}
	
	public void showChapterList() {
		mIView.showChapters();
	}
	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void showDownloadSatus(DownloadMessage message) {
		if(mNovel == null)
			return;
		if(message.status == DownloadMessage.STATUS_COMPLETED && message.bookId == mNovel.getId()) {
			if(message.task.notify)
				mIView.showDownloadComplete();
		}
	}
	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void onProgress(DownloadProgress progress) {
		if(mNovel == null)
			return;
		int bookid = mNovel.getId();
		if(bookid == progress.bookid) {
			String p = progress.current + "/" + progress.total;
			mIView.showDownloadProgress(p);
		}
	}
}
