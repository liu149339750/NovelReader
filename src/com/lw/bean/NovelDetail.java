package com.lw.bean;

import java.util.List;

public class NovelDetail {

	private Novel novel;
	private List<Chapter> chapters;
	public Novel getNovel() {
		return novel;
	}
	public void setNovel(Novel novel) {
		this.novel = novel;
	}
	public List<Chapter> getChapters() {
		return chapters;
	}
	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}
	
	
}
