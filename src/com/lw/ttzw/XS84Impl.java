package com.lw.ttzw;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.util.ParserException;

import com.justwayward.reader.view.readview.AppUtils;
import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.Novels;
import com.lw.novel.common.HtmlUtil;
import com.lw.novelreader.R;

import android.text.TextUtils;
import android.util.Pair;


public class XS84Impl implements DataInterface{

	public static final String BASE_XS86_URL = "http://m.xs84.me";
	public static final String BASE_URL = "http://www.xs84.me/";
	
	private static final String s = "11692093125118203496";
	
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
		path = path.replace("/index.html", "");
		path = path.substring(path.lastIndexOf("/"));
		path = path + "_0";
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
		return PhoneFrameworkManager.getNovelChapers(BASE_XS86_URL, source);
	}

	@Override
	public NovelDetail getNovelDetail(String url) throws ParserException {
		System.out.println(url);
		return PhoneFrameworkManager.getNovelDetail(BASE_XS86_URL, url);
	}

	@Override
	public List<Novel> getLastUpdates(String url) throws ParserException {
		return TTZWManager.getLastUpdates("", url);
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




}
