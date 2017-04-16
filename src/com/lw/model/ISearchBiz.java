package com.lw.model;

import java.util.List;

import com.lw.bean.Novel;
import com.lw.ttzw.SearchResult;

public interface ISearchBiz {

	
	public SearchResult search(String keyword);
}
