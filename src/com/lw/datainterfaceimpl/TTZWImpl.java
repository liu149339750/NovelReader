package com.lw.datainterfaceimpl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.util.ParserException;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.Novels;
import com.lw.novel.utils.AppUtils;
import com.lw.novel.utils.HtmlUtil;
import com.lw.novel.utils.Util;
import com.lw.novelreader.R;
import com.lw.ttzw.DataInterface;
import com.lw.ttzw.PhoneFrameworkManager;
import com.lw.ttzw.TTZWManager;

import android.text.TextUtils;
import android.util.Pair;

public class TTZWImpl implements DataInterface{
	public static final String BASE_URL = "http://www.ttzw.com/";
	public static final String BASE_M_URL = PhoneFrameworkManager.TTZW_BASE_URL;
	
	private static final String TAG = "ttzw";
	
	@Override
	public Novels search(String keyword) throws ParserException {
		return TTZWManager.searchNovel(keyword);
	}

	@Override
	public Novels loadSearchNovel(String source) throws ParserException {
		return TTZWManager.loadSearchNovel(source);
	}

	@Override
	public String getChapterContent(String url) throws ParserException{
		System.out.println("url = " + url);
		String html = HtmlUtil.readHtml(url);
		if(TextUtils.isEmpty(html)) {
			html = url;
		}
		if(Util.isPhoneUrl(url)) {
			return PhoneFrameworkManager.getChapterContent(html);
		}
		return TTZWManager.getChapterContent(html);
	}

	@Override
	public List<Chapter> getNovelChapers(String url) throws ParserException{
		if(Util.isPhoneUrl(url)) {
			return PhoneFrameworkManager.getNovelChapers(BASE_M_URL, url,getTag());
		}
		return TTZWManager.getNovelChapers(url);
	}

	@Override
	public NovelDetail getNovelDetail(String url) throws ParserException{
		URI uri = URI.create(url); 
		String host = uri.getHost();
		NovelDetail detail = null;
		if(host.startsWith("m")) {
			detail =  PhoneFrameworkManager.getNovelDetail(BASE_M_URL, url);
			if(detail.getChapters() == null) {
				detail.setChapters(PhoneFrameworkManager.getNovelChapers(url,detail.getChapterUrl(),getTag()));
			}
		} else {
			detail = TTZWManager.getNovelDetailByMeta(url,getTag());
		}
		return detail;
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
		return getUrlPairs(R.array.sort_urls);
	}

	private List<Pair<String, String>> getUrlPairs(int id) {
		List<Pair<String, String>>rs = new ArrayList<Pair<String,String>>();
		String kinds[] = AppUtils.getResource().getStringArray(R.array.sort_novel_kinds);
		String urls[] = AppUtils.getResource().getStringArray(id);
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

	@Override
	public String getChapterUrl(String url) {
	    URI uri = URI.create(url); 
	    String host = uri.getHost();
	    if(!host.startsWith("m")) {
	        return url;
	    }
		return url + "all.html";
	}

	@Override
	public List<Pair<String, String>> getWeekRankUrlPairs() {
		return getUrlPairs(R.array.ttzw_week_rank__urls);
	}

	@Override
	public List<Pair<String, String>> getMonthRankUrlPairs() {
		return getUrlPairs(R.array.ttzw_month_rank__urls);
	}

	@Override
	public List<Pair<String, String>> getAllRankUrlPairs() {
		return getUrlPairs(R.array.ttzw_all_rank__urls);
	}

	@Override
	public Novels getRankNovels(String url) throws ParserException {
		return getSortKindNovels(url);
	}



}
