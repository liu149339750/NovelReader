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
	
	private static final String TAG = "DataQueryManager";
	
	private DataInterface mDataInterface;
	
	public static DataQueryManager instance() {
		return dataQueryManager;
	}
	
	public DataQueryManager() {
		mDataInterface = new TTZWImpl();
		SourceSelector.setDefaultSource(mDataInterface);
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
	public String getChapterContent(String url) throws ParserException {
		DataInterface df = SourceSelector.selectDataInterface(url);
		if(df != null) {
			return df.getChapterContent(url);
		}
		return mDataInterface.getChapterContent(url);
	}

	@Override
	public List<Chapter> getNovelChapers(String url) throws ParserException {
		DataInterface df = SourceSelector.selectDataInterface(url);
		if(df != null) {
			return df.getNovelChapers(url);
		}
		return mDataInterface.getNovelChapers(url);
	}

	@Override
	public NovelDetail getNovelDetail(String url) throws ParserException {
		DataInterface df = SourceSelector.selectDataInterface(url);
		if(df != null) {
			return df.getNovelDetail(url);
		}
		System.out.println("use default :>" + url);
		return mDataInterface.getNovelDetail(url);
	}

	@Override
	public List<Novel> getLastUpdates(String url) throws ParserException {
		DataInterface df = SourceSelector.selectDataInterface(url);
		if(df != null) {
			return df.getLastUpdates(url);
		}
		return mDataInterface.getLastUpdates(url);
	}

	@Override
	public Novels getSortKindNovels(String url) throws ParserException {
		DataInterface df = SourceSelector.selectDataInterface(url);
		if(df != null) {
			return df.getSortKindNovels(url);
		}
		return mDataInterface.getSortKindNovels(url);
	}

	@Override
	public List<Pair<String, String>> getSortKindUrlPairs() {
		DataInterface df = SourceSelector.getDefaultSource();
		if(df != null) {
			return df.getSortKindUrlPairs();
		}
		return mDataInterface.getSortKindUrlPairs();
	}

	@Override
	public List<Pair<String, String>> getLastUpdateUrlPairs() {
		DataInterface df = SourceSelector.getDefaultSource();
		if(df != null) {
			return df.getLastUpdateUrlPairs();
		}
		return mDataInterface.getLastUpdateUrlPairs();
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public DataInterface select(String url) {
		return this;
	}

	@Override
	public String getChapterUrl(String url) {
		DataInterface df = SourceSelector.getDefaultSource();
		if(df != null) {
			return df.getChapterUrl(url);
		}
		return mDataInterface.getChapterUrl(url);
	}

	
	
}
