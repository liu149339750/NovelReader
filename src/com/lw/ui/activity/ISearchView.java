package com.lw.ui.activity;

import java.util.List;

import com.lw.bean.Novels;

public interface ISearchView {

	
	public void showSearchResult(Novels sr);
	
	public void showSearchHistory(List<String> history);
	
	public void showProgressDialog();
	
	public void hideProgressDialog();
}
