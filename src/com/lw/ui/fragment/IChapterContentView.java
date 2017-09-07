package com.lw.ui.fragment;

import java.util.List;

import com.lw.bean.Chapter;

public interface IChapterContentView {

	
	void showLoading(String msg);
	void hideLoading();
	
	void showChapterContent(int chapter);
	void onLoadFail(int chapter);
	void onChapterChange(List<Chapter> chapters);
	
}
