package com.lw.novelreader;

public class DownloadProgress {

	public int current;
	
	public int total;
	
	public int bookid;
	
	public DownloadProgress(int bid,int current,int total) {
		this.bookid = bid;
		this.current = current;
		this.total = total;
	}
}
