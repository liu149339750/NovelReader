package com.lw.model;

import java.util.List;

import org.htmlparser.util.ParserException;

import com.lw.db.DBUtil;
import com.lw.ttzw.SearchResult;
import com.lw.ttzw.TTZWManager;

public class SearchBiz implements ISearchBiz{

	@Override
	public SearchResult search(String keyword) {
		SearchResult sr = null;
		try {
			sr = TTZWManager.searchNovel(keyword);
			DBUtil.addSearchKeyword(keyword);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sr;
	}

}
