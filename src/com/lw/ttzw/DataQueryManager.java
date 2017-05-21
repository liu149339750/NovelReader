package com.lw.ttzw;

import java.util.List;

import org.htmlparser.util.ParserException;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.Novels;

import android.util.Pair;

public class DataQueryManager implements DataInterface{
	
	private static DataQueryManager dataQueryManager = new DataQueryManager();
	
	private DataInterface mDataInterface;
	
	public static DataQueryManager instance() {
		return dataQueryManager;
	}
	
	public DataQueryManager() {
		mDataInterface = new TTZWImpl();
	}

	@Override
	public Novels search(String keyword) throws ParserException {
		return mDataInterface.search(keyword);
	}

	@Override
	public Novels loadSearchNovel(String source) throws ParserException {
		return mDataInterface.loadSearchNovel(source);
	}

	@Override
	public String getChapterContent(String source) throws ParserException {
		return mDataInterface.getChapterContent(source);
	}

	@Override
	public List<Chapter> getNovelChapers(String source) throws ParserException {
		return mDataInterface.getNovelChapers(source);
	}

	@Override
	public NovelDetail getNovelDetail(String url) throws ParserException {
		return mDataInterface.getNovelDetail(url);
	}

	@Override
	public List<Novel> getLastUpdates(String url) throws ParserException {
		return mDataInterface.getLastUpdates(url);
	}

	@Override
	public Novels getSortKindNovels(String url) throws ParserException {
		return mDataInterface.getSortKindNovels(url);
	}

	@Override
	public List<Pair<String, String>> getSortKindUrlPairs() {
		// TODO Auto-generated method stub
		return mDataInterface.getSortKindUrlPairs();
	}

	@Override
	public List<Pair<String, String>> getLastUpdateUrlPairs() {
		// TODO Auto-generated method stub
		return mDataInterface.getLastUpdateUrlPairs();
	}

	
	
}
