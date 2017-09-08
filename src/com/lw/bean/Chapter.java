package com.lw.bean;

import java.util.List;

public class Chapter extends BaseBean{

	public String title;
	private String url;
	private String contentPath;
	private int bookId;
	private String source;
	
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
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	@Override
	public String toString() {
		return title+">"+url;
	}
	
	public static class Chapters {
	    public int bookid;
	    public String source;
	    public List<Chapter> chapters;
	}
	
}
