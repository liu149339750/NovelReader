package com.lw.presenter;

import com.lw.model.ISearchBiz;
import com.lw.model.SearchBiz;
import com.lw.ttzw.SearchResult;
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
		new AsyncTask<String, SearchResult, SearchResult>() {

			@Override
			protected SearchResult doInBackground(String... params) {
				return mISearchBiz.search(params[0]);
			}

			@Override
			protected void onPostExecute(SearchResult result) {
				mIview.showSearchResult(result);
			}

		}.execute(keyword);
	}
}
