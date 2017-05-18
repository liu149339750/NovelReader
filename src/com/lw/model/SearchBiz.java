package com.lw.model;

import org.htmlparser.util.ParserException;

import com.lw.bean.Novels;
import com.lw.db.DBUtil;
import com.lw.ttzw.TTZWManager;

public class SearchBiz implements ISearchBiz{

	@Override
	public Novels search(String keyword) {
		Novels sr = null;
		try {
			sr = TTZWManager.searchNovel(keyword);
			DBUtil.addSearchKeyword(keyword);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sr;
	}

	@Override
	public Novels loadSearchNovel(String source) {
		Novels sr = null;
		try {
			sr = TTZWManager.loadSearchNovel(source);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sr;
	}
	
	

}
