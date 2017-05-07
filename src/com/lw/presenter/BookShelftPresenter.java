package com.lw.presenter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.model.INovelBiz;
import com.lw.model.NovelBiz;
import com.lw.novel.common.SettingUtil;
import com.lw.ui.fragment.IBookShelftView;

public class BookShelftPresenter {

	private INovelBiz mINovelBiz;
	private IBookShelftView mView;
	
	public BookShelftPresenter(IBookShelftView iview) {
		mView = iview;
		mINovelBiz = new NovelBiz();
	}
	
	
	public void updateBookShelft() {
		System.out.println(Thread.currentThread().getName());
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
								.subscribeOn(Schedulers.io())
								.map(new Func1<Novel, String>() {

									@Override
									public String call(Novel arg0) {
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
				.observeOn(Schedulers.immediate())
				.subscribe(new Observer<NovelDetail>() {

					@Override
					public void onCompleted() {
						// TODO Auto-generated method stub
						System.out.println("onCompleted");
						mView.hideLoading();
					}

					@Override
					public void onError(Throwable arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onNext(NovelDetail arg0) {
						// TODO Auto-generated method stub
						System.out.println("onNext > " + arg0.getNovel().getName());
					}
				});
	}
	

}
