/**
 * 
 */
package com.lw.ttzw;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.Html;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.ParagraphTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.justwayward.reader.view.readview.LogUtils;
import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.Novels;
import com.lw.novel.common.HtmlUtil;
import com.lw.novel.common.TagAttrFilter;

import android.text.TextUtils;
import android.util.Log;

/**
 * @author Administrator
 *
 */
public class PhoneFrameworkManager {
	
	private static final String TAG = "PhoneFrameworkManager";
	
	private static final String RE_NEW = "更新：";
	private static final String LASTEST = "最新：";
	private static final String STATUS = "状态：";
	private static final String KIND = "类别：";
	private static final String AUTHOR = "作者：";

	public static final String TTZW_BASE_URL = "http://m.ttzw.com/";
	
	public static final String BASE_XS84_URL = "http://m.xs84.me";
	
	public static final String BASE_QUERY_URL = "http://zhannei.baidu.com/cse/search";
	
	
	public static List<Chapter> getNovelChapers(String baseUrl,String source,String tag) throws ParserException {
		List<Chapter> chapters = new ArrayList<Chapter>();
		Parser parser = new Parser(source);
		NodeList nodeList = parser
				.parse(new TagAttrFilter("DIV", "id", "chapterlist"));
		nodeList = nodeList.extractAllNodesThatMatch(new TagNameFilter("p"),
				true);
		int size = nodeList.size();
		for (int i = 0; i < size; i++) {
			TagNode p = (TagNode) nodeList.elementAt(i);
			String link = HtmlUtil.getFirstNodeAttr(p, "a", "href");
			if(link.startsWith("#"))
				continue;
			Chapter chapter = new Chapter();
			chapter.setUrl(baseUrl  + link);
			chapter.setTitle(p.toPlainTextString().trim());
			chapter.setSource(tag);
			chapters.add(chapter);
//			System.out.println(chapter);
		}
		return chapters;
	}
	
	public static String getChapterContent(String source) throws ParserException {
		Parser parser = new Parser(source);
		NodeList nodeList = parser.parse(new TagAttrFilter("DIV", "id",
				"chaptercontent"));
		String content = "";
		if (nodeList.size() != 0) {
			Node node = nodeList.elementAt(0);
			// chapter.setContent(node.toPlainTextString());
			content = HtmlUtil.parseContent(node);
		}
		// System.out.println(chapter.getContent());
		return content;
	}
	
	
	public static NovelDetail getNovelDetail(String baseUrl,String url) throws ParserException {
		Novel novel = new Novel();
		NovelDetail detail = new NovelDetail();
		detail.setNovel(novel);
		novel.setUrl(url);
		Parser parser = new Parser(url);
		NodeList nl = parser.parse(null);
		Node html = null;
		for (int i = 0; i < nl.size(); i++) {
			Node node = nl.elementAt(i);
			if (node instanceof Html) {
				html = node;
				break;
			}
		}
		NodeList spans = html.getChildren().extractAllNodesThatMatch(new TagAttrFilter("span", "class", "title"), true);
		if (spans.size() > 0) {
			TagNode span = (TagNode) spans.elementAt(0);
			novel.setName(span.toPlainTextString().trim());
		}
		Node synopsisArea = HtmlUtil.getFirstNodeByAttr(html, "div", "class","synopsisArea");
		if(synopsisArea == null) {
			synopsisArea = html;
		} else {
			TagNode chapterLink = HtmlUtil.getFirstNodeByAttr(synopsisArea, "p", "class","btn");
			String allLink = HtmlUtil.getFirstNodeAttr(chapterLink, "a", "href");
			detail.setChapterUrl(baseUrl + allLink);
		}
		NodeList nodes = synopsisArea.getChildren()
				.extractAllNodesThatMatch(new TagAttrFilter("div", "class", "synopsisArea_detail"), true);
		if (nodes.size() > 0) {
			nodes = nodes.elementAt(0).getChildren();
			for (int i = 0; i < nodes.size(); i++) {
				Node n = nodes.elementAt(i);
				if (n instanceof TagNode) {
					TagNode tag = (TagNode) n;
					if (tag instanceof ImageTag) {
						novel.setThumb(tag.getAttribute("src"));
					} else if (tag instanceof LinkTag) {
						if (HtmlUtil.hasChild(tag)) {
							System.out.println(tag.toHtml());
							TagNode t = HtmlUtil.getFistrtTagNode(tag, "p");
							if (t != null) {
								novel.setAuthor(t.toPlainTextString().replace(AUTHOR, "").trim());
							}
						}
					} else if (tag instanceof ParagraphTag) {
						String clas = tag.getAttribute("class");
						if ("sort".equals(clas)) {
							novel.setKind(tag.toPlainTextString().trim().replace(KIND, ""));
						} else if ("author".equals(clas)) {
							novel.setAuthor(tag.toPlainTextString().replace(AUTHOR, "").trim());
						} else {
							String planText = tag.toPlainTextString();
							if (planText.contains(RE_NEW)) {
								novel.setLastUpdateTime(planText.replace(RE_NEW, "").trim());
							} else if (planText.contains(LASTEST)) {
								TagNode a = HtmlUtil.getFistrtTagNode(tag, "a");
								novel.setLastUpdateChapter(a.toPlainTextString().trim());
								novel.setLastUpdateChapterUrl(baseUrl + a.getAttribute("href"));
							}
						}
					}
				}
			}
		}
		TagNode revew = HtmlUtil.getFirstNodeByAttr(html, "p", "class","review");
		if(revew != null) {
			novel.setBrief(HtmlUtil.trim(revew.toPlainTextString()));
		}
		if(TextUtils.isEmpty(novel.getLastUpdateChapter())) {
			NodeList divs = html.getChildren().extractAllNodesThatMatch(new TagAttrFilter("div", "class","directoryArea"),true);
			if(divs.size() == 1) {
				Node div = divs.elementAt(0);
				Node p = HtmlUtil.getFistrtTagNode(div, "p");
				if(p != null) {
					TagNode a = HtmlUtil.getFistrtTagNode(p, "a");
					if(a != null) {
						novel.setLastUpdateChapter(a.toPlainTextString().trim());
						novel.setLastUpdateChapterUrl(baseUrl + a.getAttribute("href"));
					}
				}
			}
		}
		if(TextUtils.isEmpty(detail.getChapterUrl())) {
			Log.e(TAG, "parser html fail,not get the chapter list,url = " +url);
			detail.setChapterUrl(url + "all.html");
		}
		novel.setChapterUrl(detail.getChapterUrl());
		return detail;
	}
	
	public static Novels getXS84SortKindNovels(String url) throws ParserException {
		Novels novels = new Novels();
		novels.setCurrentUrl(url);
		List<Novel> listNovel = new ArrayList<Novel>();
		novels.setNovels(listNovel);
		Parser parser = new Parser(url);
		NodeList nodelist = parser.parse(new TagAttrFilter("div", "class","hot_sale"));
		System.out.println(nodelist.size());
		for(int i=0;i<nodelist.size();i++) {
			Node novelNode = nodelist.elementAt(i);
			Novel novel = parseNovelNode(BASE_XS84_URL,novelNode);
			System.out.println(novel);
			listNovel.add(novel);
		}
		parser.reset();
		NodeList nl = parser.extractAllNodesThatMatch(new TagAttrFilter("p", "class","page"));
		if(nl.size() > 0) {
			TagNode tag = (TagNode) nl.elementAt(0);
			List<TagNode> as = HtmlUtil.getNodesByTag(tag, "a");
			TagNode a = as.get(as.size() - 1);
			novels.setNextUrl(BASE_XS84_URL + a.getAttribute("href"));
		}
		return novels;
	}
	
	public static Novels getTTZWSortKindNovels(String url) throws ParserException {
		Novels novels = new Novels();
		novels.setCurrentUrl(url);
		List<Novel> listNovel = new ArrayList<Novel>();
		novels.setNovels(listNovel);
		Parser parser = new Parser(url);
		NodeList nodelist = parser.parse(new TagAttrFilter("div", "class","hot_sale"));
		for(int i=0;i<nodelist.size();i++) {
			Node novelNode = nodelist.elementAt(i);
			Novel novel = parseNovelNode(TTZW_BASE_URL,novelNode);
			listNovel.add(novel);
		}
		parser.reset();
		NodeList nl = parser.extractAllNodesThatMatch(new TagAttrFilter("a", "id","nextPage"));
		if(nl.size() > 0) {
			TagNode tag = (TagNode) nl.elementAt(0);
			novels.setNextUrl(TTZW_BASE_URL + tag.getAttribute("href"));
		}
		return novels;
	}

	private static Novel parseNovelNode(String baseUrl,Node node) {
		NodeList nl = HtmlUtil.getAllTagNodeChildren(node);
		Novel novel = new Novel();
		for(int i=0;i<nl.size();i++) {
			TagNode tag = (TagNode) nl.elementAt(i);
			if(tag.isEndTag()) {
				continue;
			}
			if(tag instanceof LinkTag) {
				novel.setUrl(baseUrl + tag.getAttribute("href"));
			} else if(tag instanceof ImageTag) {
				novel.setThumb(tag.getAttribute("data-original"));
			} else if(tag instanceof ParagraphTag) {
				String classAttr = tag.getAttribute("class");
				if("author".equals(classAttr)) {
					String author = tag.toPlainTextString().trim();
					novel.setAuthor(author.substring(author.indexOf("：") + 1));
				} else if("title".equals(classAttr)) {
					novel.setName(tag.toPlainTextString().trim());
				} else if("review".equals(classAttr)) {
					novel.setBrief(tag.toPlainTextString().trim().replace("&nbsp;", " "));
				}
			} 
		}
		return novel;
	}
}
