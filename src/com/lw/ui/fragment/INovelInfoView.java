package com.lw.ui.fragment;

import com.lw.bean.Novel;

public interface INovelInfoView {

	
	void showLoading();
	void hideLoading();
	
	void showProgress();
	void hideProgress();
	
	int getDownloadState();
	boolean isInbookShelf(int bookid);
	void onLoadFail();
	
	void startRead();
	void removeBookShelt(int id);
	void addBookShelft(int id);
	
	void showDownloadComplete();
	void showDownloadBook();
	void showDownloadPause();
	void showDownloadProgress(String p);
	
	void showChapters();
	
	void showNovelInfo(Novel novel);
}
