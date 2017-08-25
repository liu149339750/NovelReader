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
import com.lw.novel.utils.SettingUtil;
import com.lw.ui.fragment.IBookShelftView;

public class BookShelftPresenter {

	private INovelBiz mINovelBiz;
	private IBookShelftView mView;
	
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
								.subscribeOn(Schedulers.io())
								.map(new Func1<Novel, String>() {

									@Override
									public String call(Novel arg0) {
										return arg0.getUrl();
									}
								}).map(new Func1<String, NovelDetail>() {

									@Override
									public NovelDetail call(String arg0) {
									    System.out.println("call");
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
						System.out.println("onCompleted");
						mView.hideLoading();
					}

					@Override
					public void onError(Throwable arg0) {
						arg0.printStackTrace();
						mView.hideLoading();
					}

					@Override
					public void onNext(NovelDetail arg0) {
						// TODO Auto-generated method stub
						System.out.println("onNext > " + (arg0 ==null ? "null":arg0.getNovel().getName()));
					}
				});
	}
	

}
