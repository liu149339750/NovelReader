package com.lw.ttzw;

import java.util.List;

import com.lw.bean.Novel;

public class SearchResult {

	private List<Novel> novels;
	
	/**the url for more result*/
	private String nextUrl;

	public List<Novel> getNovels() {
		return novels;
	}

	public void setNovels(List<Novel> novels) {
		this.novels = novels;
	}

	public String getNextUrl() {
		return nextUrl;
	}

	public void setNextUrl(String nextUrl) {
		this.nextUrl = nextUrl;
	} 
	
	
	
}
