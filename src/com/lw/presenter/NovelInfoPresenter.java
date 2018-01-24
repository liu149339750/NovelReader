package com.lw.presenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.db.DBUtil;
import com.lw.model.INovelBiz;
import com.lw.model.NovelBiz;
import com.lw.model.OnDataListener;
import com.lw.novel.utils.AsyncUtil;
import com.lw.novel.utils.LogUtils;
import com.lw.novelreader.BookShelftManager;
import com.lw.novelreader.DownloadMessage;
import com.lw.novelreader.DownloadProgress;
import com.lw.novelreader.DownloadService;
import com.lw.novelreader.DownloadStatus;
import com.lw.novelreader.DownloadTask;
import com.lw.novelreader.SearchSourceService;
import com.lw.ttzw.NovelManager;
import com.lw.ui.fragment.INovelInfoView;

public class NovelInfoPresenter {

    private static final String TAG = "NovelInfoPresenter";
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
		LogUtils.v(TAG, "getNovelInfo novel = " + novel);
		mBiz.getNovelInfo(novel.getUrl(), new OnDataListener() {
			
			@Override
			public void onSucess(NovelDetail nd) {
				mNovel = nd.getNovel();
				novel.setId(nd.getNovel().getId());
				mIView.hideLoading();
				mIView.showNovelInfo(nd.getNovel());
				NovelManager.getInstance().setChapers(nd.getChapters());
//				NovelManager.getInstance().setCurrentNovel(novel.getNovel());
				System.out.println("sucess");
				AsyncUtil.run(new Runnable() {
					
					@Override
					public void run() {
						DBUtil.updateReadtime(mNovel.getId());
					}
				});;
			}
			
			@Override
			public void onFail() {
			    LogUtils.v(TAG, "onFail");
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
			mIView.showProgress();
			Observable.just(mNovel.id)
			.map(new Func1<Integer, Integer>() {

				@Override
				public Integer call(Integer arg0) {
					BookShelftManager.instance().rmBookFromShelft(arg0);
					return arg0;
				}
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.newThread())
			.subscribe(new Observer<Integer>() {

				@Override
				public void onCompleted() {
					mIView.hideProgress();
				}

				@Override
				public void onError(Throwable arg0) {
					mIView.hideProgress();
					LogUtils.e(TAG, arg0);
				}

				@Override
				public void onNext(Integer arg0) {
				}
			});
			mIView.removeBookShelt(mNovel.id);
			
		} else {
			mIView.showProgress();
			Observable.just(mNovel.id)
			.map(new Func1<Integer, Integer>() {

				@Override
				public Integer call(Integer arg0) {
					BookShelftManager.instance().addBookToShelft(arg0);
					return arg0;
				}
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.newThread())
			.subscribe(new Observer<Integer>() {

				@Override
				public void onCompleted() {
					LogUtils.v(TAG, "addBookToShelft onCompleted");
					mIView.hideProgress();
					SearchSourceService.postBackgroundSearch(mNovel);
				}

				@Override
				public void onError(Throwable arg0) {
					mIView.hideProgress();
					LogUtils.e(TAG, arg0);
				}

				@Override
				public void onNext(Integer arg0) {
				}
			});
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
