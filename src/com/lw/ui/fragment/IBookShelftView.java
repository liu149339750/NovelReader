package com.lw.ui.fragment;

import java.util.List;

import com.lw.bean.Novel;

public interface IBookShelftView {

	void showLoading();
	void hideLoading();
	
	
	List<Novel> getNeedUpdateNovels();
	
}
