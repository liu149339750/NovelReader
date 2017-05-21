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
	
	public String getChapterContent(String source) throws ParserException;
	
	public List<Chapter> getNovelChapers(String source) throws ParserException;
	
	public NovelDetail getNovelDetail(String url) throws ParserException;
	
	public List<Novel> getLastUpdates(String url) throws ParserException;
	
	public Novels getSortKindNovels(String url) throws ParserException;
	
	public List<Pair<String, String>> getSortKindUrlPairs();
	public List<Pair<String, String>> getLastUpdateUrlPairs();
}
