package com.lw.ttzw;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.util.ParserException;

import com.justwayward.reader.view.readview.AppUtils;
import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.Novels;
import com.lw.novelreader.MyApp;
import com.lw.novelreader.R;
import com.lw.ui.fragment.LastNovelListFragment;

import android.util.Pair;

public class TTZWImpl implements DataInterface{
	public static final String BASE_URL = "http://www.ttzw.com/";
	
	@Override
	public Novels search(String keyword) throws ParserException {
		return TTZWManager.searchNovel(keyword);
	}

	@Override
	public Novels loadSearchNovel(String source) throws ParserException {
		return TTZWManager.loadSearchNovel(source);
	}

	@Override
	public String getChapterContent(String source) throws ParserException{
		return TTZWManager.getChapterContent(source);
	}

	@Override
	public List<Chapter> getNovelChapers(String source) throws ParserException{
		return TTZWManager.getNovelChapers(source);
	}

	@Override
	public NovelDetail getNovelDetail(String url) throws ParserException{
		return TTZWManager.getNovelDetailByMeta(url);
	}

	@Override
	public List<Novel> getLastUpdates(String url) throws ParserException{
		return TTZWManager.getLastUpdates(BASE_URL,url);
	}

	@Override
	public Novels getSortKindNovels(String url) throws ParserException{
		return PhoneFrameworkManager.getTTZWSortKindNovels(url);
	}

	@Override
	public List<Pair<String, String>> getSortKindUrlPairs() {	
		List<Pair<String, String>>rs = new ArrayList<Pair<String,String>>();
		String kinds[] = AppUtils.getResource().getStringArray(R.array.sort_novel_kinds);
		String urls[] = AppUtils.getResource().getStringArray(R.array.sort_urls);
		for(int i=0;i<kinds.length;i++) {
			Pair<String, String>p = new Pair<String, String>(kinds[i], urls[i]);
			rs.add(p);
		}
		return rs;
	}

	@Override
	public List<Pair<String, String>> getLastUpdateUrlPairs() {
		List<Pair<String, String>>rs = new ArrayList<Pair<String,String>>();
		String kinds[] = AppUtils.getResource().getStringArray(R.array.last_novel_kinds);
		String urls[] = AppUtils.getResource().getStringArray(R.array.last_urls);
		for(int i=0;i<kinds.length;i++) {
			Pair<String, String>p = new Pair<String, String>(kinds[i], urls[i]);
			rs.add(p);
		}
		return rs;
	}



}
