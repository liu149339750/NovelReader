package com.lw.model;

import com.lw.bean.NovelDetail;

public interface INovelBiz {

	
	public void getNovelInfo(String url,OnDataListener listener);
	
	public NovelDetail getNovelInfo(String url);
	
	public void cancel();
	
}
