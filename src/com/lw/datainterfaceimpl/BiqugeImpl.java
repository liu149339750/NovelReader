package com.lw.datainterfaceimpl;

import java.util.List;

import org.htmlparser.util.ParserException;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.Novels;
import com.lw.ttzw.BiqugeManager;
import com.lw.ttzw.BiqugeSearch;
import com.lw.ttzw.DataInterface;

import android.util.Pair;

public class BiqugeImpl implements DataInterface{
	
	private static final String TAG = "biquge";

	@Override
	public Novels search(String keyword) throws ParserException {
		return BiqugeSearch.searchBiquge(keyword);
	}

	@Override
	public Novels loadSearchNovel(String source) throws ParserException {
		return null;
	}

	@Override
	public String getChapterContent(String url) throws ParserException {
		return BiqugeManager.getChapterContent(url);
	}

	@Override
	public List<Chapter> getNovelChapers(String url) throws ParserException {
		return BiqugeManager.getChapters(url);
	}

	@Override
	public NovelDetail getNovelDetail(String url) throws ParserException {
		return BiqugeManager.getNovelDetailByMeta(url);
	}

	@Override
	public List<Novel> getLastUpdates(String url) throws ParserException {
		return null;
	}

	@Override
	public Novels getSortKindNovels(String url) throws ParserException {
		return null;
	}

	@Override
	public Novels getRankNovels(String url) throws ParserException {
		return null;
	}

	@Override
	public List<Pair<String, String>> getSortKindUrlPairs() {
		return null;
	}

	@Override
	public List<Pair<String, String>> getLastUpdateUrlPairs() {
		return null;
	}

	@Override
	public List<Pair<String, String>> getWeekRankUrlPairs() {
		return null;
	}

	@Override
	public List<Pair<String, String>> getMonthRankUrlPairs() {
		return null;
	}

	@Override
	public List<Pair<String, String>> getAllRankUrlPairs() {
		return null;
	}

	@Override
	public String getChapterUrl(String url) {
		return url;
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public DataInterface select(String url) {
		if(url.contains(TAG))
			return this;
		return null;
	}

}
