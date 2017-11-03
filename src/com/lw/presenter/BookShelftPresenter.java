package com.lw.presenter;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.model.INovelBiz;
import com.lw.model.NovelBiz;
import com.lw.novel.utils.AppUtils;
import com.lw.novel.utils.LogUtils;
import com.lw.novel.utils.SettingUtil;
import com.lw.novelreader.SearchSourceService;
import com.lw.ui.fragment.IBookShelftView;

public class BookShelftPresenter {

	private INovelBiz mINovelBiz;
	private IBookShelftView mView;
	private static final String TAG = "BookShelftPresenter";
	
	public BookShelftPresenter(IBookShelftView iview) {
		mView = iview;
		mINovelBiz = new NovelBiz();
	}
	
	
	public void updateBookShelft() {
		List<Novel> updateNovels = mView.getNeedUpdateNovels();
		Observable
				.from(updateNovels)
				.take(SettingUtil.getPullUpdateNovelNum())
				.flatMap(new Func1<Novel, Observable<? extends NovelDetail>>() {

					@Override
					public Observable<? extends NovelDetail> call(Novel arg0) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return Observable.just(arg0)
								.subscribeOn(Schedulers.from(AppUtils.getExecutor()))
								.map(new Func1<Novel, String>() {

									@Override
									public String call(Novel arg0) {
									    System.out.println(arg0.getName());
										return arg0.getUrl();
									}
								}).map(new Func1<String, NovelDetail>() {

									@Override
									public NovelDetail call(String arg0) {
										return mINovelBiz.getNovelInfo(arg0);
									}
								});
					}
				})
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<NovelDetail>() {

					@Override
					public void onCompleted() {
						// TODO Auto-generated method stub
						LogUtils.v(TAG, "updateBookShelft onCompleted");
						mView.hideLoading();
					}

					@Override
					public void onError(Throwable arg0) {
						LogUtils.e(TAG, "updateBookShelft>onError", arg0);
						mView.hideLoading();
					}

					@Override
					public void onNext(NovelDetail arg0) {
					    System.out.println("onNext > " + (arg0 ==null ? "null":arg0.getNovel().getName()));
						LogUtils.v(TAG,"onNext > " + (arg0 ==null ? "null":arg0.getNovel().getName()));
						//太耗性能与流量，先不开启，改成在打开小说的时候更新。
//						if(arg0 != null)
//						    SearchSourceService.postBackgroundSearch(arg0.getNovel());
					}
				});
	}
	

}
