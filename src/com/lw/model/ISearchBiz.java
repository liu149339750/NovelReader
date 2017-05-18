package com.lw.model;

import java.util.List;

import com.lw.bean.Novel;
import com.lw.bean.Novels;

public interface ISearchBiz {

	
	public Novels search(String keyword);
	
	public Novels loadSearchNovel(String source);
}
