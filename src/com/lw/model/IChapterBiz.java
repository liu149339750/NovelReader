package com.lw.model;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;

public interface IChapterBiz {

	
	public void getChapterContent(Novel novel,Chapter chapter,OnChapterContentListener listener);
}
