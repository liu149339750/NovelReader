package com.lw.ui.fragment;

public interface IChapterContentView {

	
	void showLoading(String msg);
	void hideLoading();
	
	void showChapterContent(int chapter);
	void onLoadFail(int chapter);
	
}
