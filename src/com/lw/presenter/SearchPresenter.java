package com.lw.presenter;

import com.lw.bean.Novels;
import com.lw.model.ISearchBiz;
import com.lw.model.SearchBiz;
import com.lw.ui.activity.ISearchView;

import android.os.AsyncTask;
import android.text.TextUtils;

public class SearchPresenter {

	private ISearchBiz mISearchBiz;
	private ISearchView mIview;
	
	public SearchPresenter(ISearchView view) {
		mISearchBiz = new SearchBiz();
		mIview = view;
	}
	
	
	public void search(String keyword) {
		if(TextUtils.isEmpty(keyword))
			return;
		new AsyncTask<String, Novels, Novels>() {

			@Override
			protected Novels doInBackground(String... params) {
				return mISearchBiz.search(params[0]);
			}

			@Override
			protected void onPostExecute(Novels result) {
				mIview.showSearchResult(result);
			}

		}.execute(keyword);
	}
	
	public void loadSearch(String url) {
		if(TextUtils.isEmpty(url))
			return;
		new AsyncTask<String, Novels, Novels>() {

			@Override
			protected Novels doInBackground(String... params) {
				return mISearchBiz.loadSearchNovel(params[0]);
			}

			@Override
			protected void onPostExecute(Novels result) {
				mIview.showSearchResult(result);
			}

		}.execute(url);
	}
}
