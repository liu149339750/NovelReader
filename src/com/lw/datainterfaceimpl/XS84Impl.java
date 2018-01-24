package com.lw.datainterfaceimpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.util.ParserException;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.Novels;
import com.lw.novel.utils.AppUtils;
import com.lw.novel.utils.HtmlUtil;
import com.lw.novel.utils.LogUtils;
import com.lw.novel.utils.Util;
import com.lw.novelreader.R;
import com.lw.ttzw.DataInterface;
import com.lw.ttzw.PhoneFrameworkManager;
import com.lw.ttzw.TTZWManager;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;


public class XS84Impl implements DataInterface{

	public static final String BASE_XS86_URL = "http://m.xs84.me";
	public static final String BASE_URL = "http://www.xs84.me/";
	
	private static final String s = "11692093125118203496";
	private static final String TAG = "xs84";
	
	@Override
	public Novels search(String keyword) throws ParserException {
		String url = PhoneFrameworkManager.BASE_QUERY_URL + "?s=" + s + "&q="+keyword;
		Novels novels = TTZWManager.loadSearchNovel(url);
		List<Novel> novelList = novels.getNovels();
		for(Novel novel : novelList) {
			try {
				novel.setUrl(mapUrl(novel.getUrl()));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return novels;
	}
	
	private String mapUrl(String url) throws URISyntaxException {
		URI uri = URI.create(url);
		String host = uri.getHost();
		String scheme = uri.getScheme();
		String path = uri.getPath();
		if(path.contains("index.html")) {
			path = path.replace("/index.html", "");
			path = path.substring(path.lastIndexOf("/"));
			path = path + "_0/";
		}
		host = host.replace("www", "m");
		return new URI(scheme, host, path, null).toString();
	}

	@Override
	public Novels loadSearchNovel(String source) throws ParserException {
		Novels novels = TTZWManager.loadSearchNovel(source);
		List<Novel> novelList = novels.getNovels();
		for(Novel novel : novelList) {
			try {
				novel.setUrl(mapUrl(novel.getUrl()));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return novels;
	}

	@Override
	public String getChapterContent(String source) throws ParserException {
		return PhoneFrameworkManager.getChapterContent(source);
	}

	@Override
	public List<Chapter> getNovelChapers(String source) throws ParserException {
		LogUtils.v(TAG, "getNovelChapers::url = " + source);
		if(TextUtils.isEmpty(source))
			return null;
		return PhoneFrameworkManager.getNovelChapers(BASE_XS86_URL, source,getTag());
	}

	@Override
	public NovelDetail getNovelDetail(String url) throws ParserException {
		System.out.println("xs84 > " + url);
		NovelDetail detail = null;
//		if (!Util.isPhoneUrl(url)) {
//			url = url.replace("www", "m");
//		} 
		try {
			url = mapUrl(url);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogUtils.v(TAG, "getNovelDetail  url = " + url);
		detail = PhoneFrameworkManager.getNovelDetail(BASE_XS86_URL, url);
		if (detail.getChapters() == null && detail.getChapterUrl() != null) {
			detail.setChapters(getNovelChapers(HtmlUtil.readHtml(detail.getChapterUrl())));
		}
		return detail;
	}

	@Override
	public List<Novel> getLastUpdates(String url) throws ParserException {
		List<Novel> data = TTZWManager.getLastUpdates("", url);
		if (url.contains("fenlei")) {
			for (Novel novel : data) {
				String name = novel.getName();
				novel.setAuthor(name.substring(name.indexOf("(") + 1, name.length() - 1));
				novel.setName(name.substring(0, name.indexOf("(")));
			}
		}
		return data;
	}

	@Override
	public Novels getSortKindNovels(String url) throws ParserException {
		Novels novels = null;
		String content = HtmlUtil.readHtml(url);
		if(!TextUtils.isEmpty(content)) {
			novels = PhoneFrameworkManager.getXS84SortKindNovels(content);
		}  else {
			novels =  PhoneFrameworkManager.getXS84SortKindNovels(url);
		}
		List<Novel> ns = novels.getNovels();
		for(Novel novel : ns) {
			String imag = novel.getThumb();
			novel.setThumb(imag.replace("_0", ""));
		}
		return novels;
	}

	@Override
	public List<Pair<String, String>> getSortKindUrlPairs() {	
		List<Pair<String, String>>rs = new ArrayList<Pair<String,String>>();
		String kinds[] = AppUtils.getResource().getStringArray(R.array.xs84_sort_novel_kinds);
		String urls[] = AppUtils.getResource().getStringArray(R.array.xs84_sort_urls);
		for(int i=0;i<kinds.length;i++) {
			Pair<String, String>p = new Pair<String, String>(kinds[i], urls[i]);
			rs.add(p);
		}
		return rs;
	}

	@Override
	public List<Pair<String, String>> getLastUpdateUrlPairs() {
		List<Pair<String, String>>rs = new ArrayList<Pair<String,String>>();
		String kinds[] = AppUtils.getResource().getStringArray(R.array.xs84_last_novel_kinds);
		String urls[] = AppUtils.getResource().getStringArray(R.array.xs84_last_urls);
		for(int i=0;i<kinds.length;i++) {
			Pair<String, String>p = new Pair<String, String>(kinds[i], urls[i]);
			rs.add(p);
		}
		return rs;
	}

	@Override
	public DataInterface select(String url) {
		if(url.contains(TAG))
			return this;
		return null;
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getChapterUrl(String url) {
		try {
			url = mapUrl(url);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url + "all.html";
	}

	@Override
	public Novels getRankNovels(String url) throws ParserException {
		return getSortKindNovels(url);
	}


	@Override
	public List<Pair<String, String>> getWeekRankUrlPairs() {
		return getUrlPairs(R.array.xs84_week_rank_urls);
	}

	private List<Pair<String, String>> getUrlPairs(int id) {
		List<Pair<String, String>>rs = new ArrayList<Pair<String,String>>();
		String kinds[] = AppUtils.getResource().getStringArray(R.array.xs84_last_novel_kinds);
		String urls[] = AppUtils.getResource().getStringArray(id);
		for(int i=0;i<kinds.length;i++) {
			Pair<String, String>p = new Pair<String, String>(kinds[i], urls[i]);
			rs.add(p);
		}
		return rs;
	}

	@Override
	public List<Pair<String, String>> getMonthRankUrlPairs() {
		return getUrlPairs(R.array.xs84_month_rank_urls);
	}

	@Override
	public List<Pair<String, String>> getAllRankUrlPairs() {
		return getUrlPairs(R.array.xs84_all_rank_urls);
	}




}
