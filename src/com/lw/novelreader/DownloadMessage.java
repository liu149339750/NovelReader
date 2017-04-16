package com.lw.novelreader;

public class DownloadMessage {

	public static int STATUS_START = 1;
	public static int STATUS_COMPLETED = 2;
	public static int STATUS_CANCEL = 3;
	
	public int status;
	public int bookId;
	public DownloadTask task;
	
	public DownloadMessage(int bookId,int status,DownloadTask task) {
		this.status = status;
		this.bookId = bookId;
		this.task = task;
	}
}
