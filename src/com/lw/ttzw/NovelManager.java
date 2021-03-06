package com.lw.ttzw;

import java.util.List;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.novelreader.BookShelftManager;

import android.content.Context;

public class NovelManager {

	
	private static NovelManager manager;
	
	private Novel mNovel;
	private List<Chapter> mChaper;
	private int chapterId = 0;
	
	public static NovelManager getInstance() {
		if(manager == null) {
			System.out.println("new NovelManager");
			manager = new NovelManager();
		}
		return manager;
	}
	
	public void init(Context context) {
		BookShelftManager.instance().loadBookShelft(context);
	}
	
	public boolean isInbookShelft(int bookid) {
		return BookShelftManager.instance().isInbookShelft(bookid);
	}

	public void setCurrentNovel(Novel novel){
		if(mNovel != null && mNovel.getId() == novel.getId()) {
			System.out.println("setCurrentNovel return");
			return;
		}
		System.out.println(novel.toString());
		mNovel = novel;
		mChaper = null;
		chapterId = 0;
	}
	
	public Novel getCurrentNovel() {
		return mNovel;
	}

	public List<Chapter> getChapers() {
		return mChaper;
	}

	public void setChapers(List<Chapter> mChaper) {
		this.mChaper = mChaper;
	}
	
	public int getChapterSize() {
		if(mChaper == null) {
			return 0;
		}
		return mChaper.size();
	}

	public int getChapterPosition() {
		return chapterId;
	}

	public void setChapterPosition(int chapterId) {
		System.out.println("setChapterId id =" + chapterId);
		this.chapterId = chapterId;
	}

	public Chapter getChapter() {
		return mChaper.get(chapterId);
	}
	
	public Chapter getChapter(int chapter) {
		return mChaper.get(chapter);
	}
	
}
