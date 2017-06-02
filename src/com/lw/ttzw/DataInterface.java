package com.lw.ttzw;

import java.util.List;

import org.htmlparser.util.ParserException;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.Novels;

import android.util.Pair;

public interface DataInterface {
	

	public Novels search(String keyword) throws ParserException;
	public Novels loadSearchNovel(String source) throws ParserException;
	
	public String getChapterContent(String url) throws ParserException;
	
	public List<Chapter> getNovelChapers(String url) throws ParserException;
	
	public NovelDetail getNovelDetail(String url) throws ParserException;
	
	public List<Novel> getLastUpdates(String url) throws ParserException;
	
	public Novels getSortKindNovels(String url) throws ParserException;
	
	public Novels getRankNovels(String url) throws ParserException;
	
	public List<Pair<String, String>> getSortKindUrlPairs();
	public List<Pair<String, String>> getLastUpdateUrlPairs();
	public List<Pair<String, String>> getWeekRankUrlPairs();
	public List<Pair<String, String>> getMonthRankUrlPairs();
	public List<Pair<String, String>> getAllRankUrlPairs();
	
	/**get the novel's chapters url by the novel url*/
	public String getChapterUrl(String url);
	public String getTag();
	public DataInterface select(String url);
}
