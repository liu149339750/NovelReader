package com.lw.bean;

public class Chapter {

	public String title;
	private String url;
	private String contentPath;
	
	public boolean isDownload;
	
	public Chapter() {
	}
	
	public Chapter(String title,String url) {
		this.title = title;
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getContentPath() {
		return contentPath;
	}

	public void setContentPath(String contentPath) {
		this.contentPath = contentPath;
	}

	@Override
	public String toString() {
		return title+">"+url;
	}
	
}
