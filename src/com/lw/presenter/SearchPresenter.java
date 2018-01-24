package com.lw.presenter;

import java.util.List;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.observers.Observers;
import rx.schedulers.Schedulers;

import com.lw.bean.Novels;
import com.lw.db.DBUtil;
import com.lw.model.ISearchBiz;
import com.lw.model.SearchBiz;
import com.lw.ui.activity.ISearchView;

import android.os.AsyncTask;
import android.text.TextUtils;

public class SearchPresenter {

	private ISearchBiz mISearchBiz;
	private ISearchView mIview;
	private boolean isCancel;
	
	public SearchPresenter(ISearchView view) {
		mISearchBiz = new SearchBiz();
		mIview = view;
	}
	
	
	public void search(String keyword) {
		if(TextUtils.isEmpty(keyword) || mIview == null)
			return;
		isCancel = false;
		mIview.showProgressDialog();
		new AsyncTask<String, Novels, Novels>() {

			@Override
			protected Novels doInBackground(String... params) {
				return mISearchBiz.search(params[0]);
			}

			@Override
			protected void onPostExecute(Novels result) {
				if(isCancel)
					return;
				mIview.hideProgressDialog();
				mIview.showSearchResult(result);
				
			}

		}.execute(keyword);
	}
	
	public void loadSearch(String url) {
		if(TextUtils.isEmpty(url) || mIview == null)
			return;
		new AsyncTask<String, Novels, Novels>() {

			@Override
			protected Novels doInBackground(String... params) {
				return mISearchBiz.loadSearchNovel(params[0]);
			}

			@Override
			protected void onPostExecute(Novels result) {
				if(isCancel)
					return;
				mIview.showSearchResult(result);
			}

		}.execute(url);
	}
	
	public void queryKeywords() {
		Observable.create(new OnSubscribe<List<String>>() {

			@Override
			public void call(Subscriber<? super List<String>> arg0) {
				arg0.onStart();
				List<String> words = DBUtil.querySearchHistory();
				arg0.onNext(words);
				arg0.onCompleted();
			}
		})
		.subscribeOn(Schedulers.newThread())
		.observeOn(AndroidSchedulers.mainThread())
		.subscribe(new Action1<List<String>>() {


			@Override
			public void call(List<String> arg0) {
				
			}
		});
	}
	
	public void cancel() {
		isCancel = true;
		mIview.hideProgressDialog();
	}
}
