package com.lw.ttzw;

import java.util.List;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.ShelftBook;
import com.lw.novelreader.BookShelftManager;

import android.content.Context;

public class NovelManager {

	
	private static NovelManager manager;
	
	private Novel mNovel;
	private List<Chapter> mChaper;
	private int chapterId = 1;
	
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
			return;
		}
		mNovel = novel;
		mChaper = null;
		chapterId = 1;
	}
	
	public Novel getCurrentNovel() {
		return mNovel;
	}

	public List<Chapter> getChaper() {
		return mChaper;
	}

	public void setChaper(List<Chapter> mChaper) {
		this.mChaper = mChaper;
	}
	
	public int getChapterSize() {
		if(mChaper == null) {
			return 0;
		}
		return mChaper.size();
	}

	public int getChapterId() {
		return chapterId;
	}

	public void setChapterId(int chapterId) {
		System.out.println("setChapterId id =" + chapterId);
		this.chapterId = chapterId;
	}

	public Chapter getChapter() {
		return mChaper.get(chapterId - 1);
	}
	
	public Chapter getChapter(int chapter) {
		return mChaper.get(chapter - 1);
	}
	
}
