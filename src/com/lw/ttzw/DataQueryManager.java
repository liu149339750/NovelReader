package com.lw.ttzw;

import java.util.List;

import org.htmlparser.util.ParserException;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.Novels;
import com.lw.datainterfaceimpl.TTZWImpl;

import android.util.Pair;

public class DataQueryManager implements DataInterface{
	
	private static DataQueryManager dataQueryManager = new DataQueryManager();
	
	private static final String TAG = "DataQueryManager";
	
	private DataInterface mDataInterface;
	
	public static DataQueryManager instance() {
		return dataQueryManager;
	}
	
	public DataQueryManager() {
	}

	@Override
	public Novels search(String keyword) throws ParserException {
		return SourceSelector.getDefaultSource().search(keyword);
	}

	@Override
	public Novels loadSearchNovel(String url) throws ParserException {
		DataInterface df = SourceSelector.selectDataInterface(url);
		if(df != null) {
			return df.loadSearchNovel(url);
		}
		return SourceSelector.getDefaultSource().loadSearchNovel(url);
	}

	@Override
	public String getChapterContent(String url) throws ParserException {
		DataInterface df = SourceSelector.selectDataInterface(url);
		if(df != null) {
			return df.getChapterContent(url);
		}
		return SourceSelector.getDefaultSource().getChapterContent(url);
	}

	@Override
	public List<Chapter> getNovelChapers(String url) throws ParserException {
		DataInterface df = SourceSelector.selectDataInterface(url);
		if(df != null) {
			return df.getNovelChapers(url);
		}
		return SourceSelector.getDefaultSource().getNovelChapers(url);
	}

	@Override
	public NovelDetail getNovelDetail(String url) throws ParserException {
		DataInterface df = SourceSelector.selectDataInterface(url);
		if(df != null) {
			return df.getNovelDetail(url);
		}
		System.out.println("use default :>" + url);
		return SourceSelector.getDefaultSource().getNovelDetail(url);
	}

	@Override
	public List<Novel> getLastUpdates(String url) throws ParserException {
		DataInterface df = SourceSelector.selectDataInterface(url);
		if(df != null) {
			return df.getLastUpdates(url);
		}
		return SourceSelector.getDefaultSource().getLastUpdates(url);
	}

	@Override
	public Novels getSortKindNovels(String url) throws ParserException {
		DataInterface df = SourceSelector.selectDataInterface(url);
		if(df != null) {
			return df.getSortKindNovels(url);
		}
		return SourceSelector.getDefaultSource().getSortKindNovels(url);
	}

	@Override
	public List<Pair<String, String>> getSortKindUrlPairs() {
		DataInterface df = SourceSelector.getDefaultSource();
		if(df != null) {
			return df.getSortKindUrlPairs();
		}
		return SourceSelector.getDefaultSource().getSortKindUrlPairs();
	}

	@Override
	public List<Pair<String, String>> getLastUpdateUrlPairs() {
		DataInterface df = SourceSelector.getDefaultSource();
		if(df != null) {
			return df.getLastUpdateUrlPairs();
		}
		return SourceSelector.getDefaultSource().getLastUpdateUrlPairs();
	}

	@Override
	public String getTag() {
		return SourceSelector.getDefaultSource().getTag();
	}

	@Override
	public DataInterface select(String url) {
		return SourceSelector.selectDataInterface(url);
	}

	@Override
	public String getChapterUrl(String url) {
		DataInterface df = SourceSelector.getDefaultSource();
		if(df != null) {
			return df.getChapterUrl(url);
		}
		return SourceSelector.getDefaultSource().getChapterUrl(url);
	}

	@Override
	public Novels getRankNovels(String url) throws ParserException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Pair<String, String>> getWeekRankUrlPairs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Pair<String, String>> getMonthRankUrlPairs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Pair<String, String>> getAllRankUrlPairs() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
