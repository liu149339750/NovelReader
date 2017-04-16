package com.lw.model;

public interface INovelBiz {

	
	public void getNovelInfo(String url,OnDataListener listener);
	
	public void cancel();
	
}
