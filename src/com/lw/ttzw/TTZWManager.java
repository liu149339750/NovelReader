package com.lw.ttzw;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.tags.ParagraphTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.bean.Novels;
import com.lw.novel.utils.HtmlUtil;
import com.lw.novel.utils.TagAttrFilter;
import com.lw.novel.utils.Util;

import android.util.Log;

public class TTZWManager {

	private static TTZWManager ttzwManager = new TTZWManager();
	
	private static final String s="4548207915219874571";
	private static final String BASE_QUERY_URL = "http://zhannei.baidu.com/cse/";
	public static final String BASE_URL = "https://www.ttzw.com/";
	
	private static String TEXT_1 = "类型：";
	private static String TEXT_2 = "更新时间：";
	private static String TEXT_3 = "最后更新";
	private static String TEXT_4 = "最新更新";
	private static String TEXT_5 = "作";
	private static String TEXT_6 = "：";

	public static TTZWManager getInstance() {
		return ttzwManager;
	}

	public static Novels searchNovel(String keyword)
			throws ParserException {
		String url = BASE_QUERY_URL + "search?s="+s+"&q="+keyword; 
		return loadSearchNovel(url);
	}
	
	public static Novels loadSearchNovel(String source) throws ParserException {
		System.out.println("loadSearchNovel---"+source);
		List<Novel> novels = new ArrayList<Novel>();
		Novels sr = new Novels();
		sr.setNovels(novels);
		Parser parser = new Parser(HtmlUtil.getHtml(source));
		parser.setEncoding("utf-8");
		// get novel list;
		NodeList nodeList = parser.parse(new TagAttrFilter("DIV", "class",
				"result-item result-game-item"));
		int size = nodeList.size();
		
		for (int i = 0; i < size; i++) {
			Node novelNode = nodeList.elementAt(i);
			NodeList nl = novelNode.getChildren();
			Novel novel = new Novel();
			//get every novel infos;
			for (int j = 0; j < nl.size(); j++) {
				Node node = nl.elementAt(j);
				if (node instanceof Div) {
					String attClass = ((Div) node).getAttribute("class");
					// get the pic src
					if ("result-game-item-pic".equals(attClass)) {
						//here,we can also use nodelist's extractAllNodesThatMatch to get this
						List<TagNode> img = HtmlUtil.getNodesByTag(node, "img");
						if (img.size() > 0) {
							novel.thumb = ((TagNode)img.get(0)).getAttribute("src");
						}
					} else if ("result-game-item-detail".equals(attClass)) { 
						//get the detail
						NodeList nodelist1 = HtmlUtil.getAllTagNodeChildren(node);
						for(int k=0;k<nodelist1.size();k++) {
							TagNode tag = (TagNode) nodelist1.elementAt(k);
							if(tag instanceof LinkTag) {
								if("title".equals(tag.getAttribute("cpos"))) {
									novel.url = tag.getAttribute("href");
									novel.name = tag.toPlainTextString().trim();
								} else if("newchapter".equals(tag.getAttribute("cpos"))) {
									novel.lastUpdateChapter = tag.toPlainTextString();
									novel.lastUpdateChapterUrl = tag.getAttribute("href");
								}
							} else if(tag instanceof Span) {
								String att = tag.getAttribute("class");
								if(att == null) {
									Node preNode = tag.getPreviousSibling();
									TagNode pre = null;
									while(!(preNode instanceof TagNode)) {
										preNode = preNode.getPreviousSibling();
										if(preNode == null) {
											preNode = new Span();
											break;
										}
									}
									pre = (TagNode) preNode;
									if(pre != null && "result-game-item-info-tag-title preBold".equals(pre.getAttribute("class")))
										novel.author = tag.toPlainTextString().trim();
								} else if("result-game-item-info-tag-title".equals(att)) {
									Node preNode = tag.getPreviousSibling();
									TagNode pre = null;
									while(!(preNode instanceof TagNode)) {
										preNode = preNode.getPreviousSibling();
										if(preNode == null) {
											preNode = new Span();
											break;
										}
									}
									pre = (TagNode) preNode;
									if(pre != null) {
										String text = pre.toPlainTextString().trim();
										if(TEXT_1.equals(text.trim())) {
											novel.kind = tag.toPlainTextString().trim();
										} else if(TEXT_2.equals(text)) {
											novel.lastUpdateTime = tag.toPlainTextString().trim();
										}
									}
								}
							} 
						}
						
						//get the detail info
						NodeList detail = node.getChildren().extractAllNodesThatMatch(new NodeFilter() {
							
							public boolean accept(Node node) {
								if(node instanceof ParagraphTag) {
									ParagraphTag p = (ParagraphTag) node;
									if("result-game-item-desc".equals(p.getAttribute("class"))) {
										return true;
									}
								}
								return false;
							}
						}) ;
						if(detail.size() > 0) {
							novel.brief = detail.elementAt(0).toPlainTextString().trim();
						}
					}
				}
			}
			novels.add(novel);
		}
		//get the next page url
		parser.reset();
		NodeList nexts = parser.parse(new NodeFilter() {
			
			public boolean accept(Node node) {
				if(node instanceof LinkTag) {
					LinkTag a = (LinkTag) node;
					if("pager-next-foot n".equals(a.getAttribute("class")))
						return true;
				}
				return false;
			}
		});
		if(nexts.size() == 1) {
			LinkTag link = (LinkTag) nexts.elementAt(0);
			sr.setNextUrl(BASE_QUERY_URL + link.getAttribute("href"));
		}
		return sr;
	}

	/** get the chapterContent by the content url or the file or the html*/
	public static String getChapterContent(String source) throws ParserException {
		Parser parser = new Parser(HtmlUtil.getHtml(source));
		NodeList nodeList = parser.parse(new TagAttrFilter("DIV", "id",
				"content"));
		String content = "";
		if (nodeList.size() != 0) {
			Node node = nodeList.elementAt(0);
			// chapter.setContent(node.toPlainTextString());
			content = HtmlUtil.parseContent(node);
		}
		// System.out.println(chapter.getContent());
		return content;
	}

	/** get the chapters by the novel url */
	public static List<Chapter> getNovelChapers(String url)
			throws ParserException {
		List<Chapter> chapters = new ArrayList<Chapter>();
		Parser parser = new Parser(HtmlUtil.getHtml(url));
		NodeList nodeList = parser
				.parse(new TagAttrFilter("DIV", "id", "list"));
		nodeList = nodeList.extractAllNodesThatMatch(new TagNameFilter("dd"),
				true);
		int size = nodeList.size();
		for (int i = 0; i < size; i++) {
			Chapter chapter = new Chapter();
			TagNode dd = (TagNode) nodeList.elementAt(i);
			chapter.setUrl(url + "/" +HtmlUtil.getFirstNodeAttr(dd, "a", "href"));
			chapter.setTitle(dd.toPlainTextString().trim());
			chapters.add(chapter);
		}
		return chapters;
	}
	
	public static NovelDetail getNovelDetailByMeta(String url,String tag) throws ParserException {
		System.out.println("getNovelDetailByMeta >"+url);
		NovelDetail detail = new NovelDetail();
		List<Chapter> chapters = new ArrayList<Chapter>();
		Novel novel = new Novel();
		detail.setNovel(novel);
		detail.setChapters(chapters);
		
		Parser parser = new Parser(HtmlUtil.getHtml(url));
		NodeList nodeList = parser
				.parse(new TagAttrFilter("DIV", "id", "list"));
		nodeList = nodeList.extractAllNodesThatMatch(new TagNameFilter("dd"),
				true);
		int size = nodeList.size();
		for (int i = 0; i < size; i++) {
			Chapter chapter = new Chapter();
			TagNode dd = (TagNode) nodeList.elementAt(i);
			chapter.setUrl(url + "/" + HtmlUtil.getFirstNodeAttr(dd, "a", "href"));
			chapter.setTitle(dd.toPlainTextString().trim());
			chapter.setSource(tag);
			chapters.add(chapter);
		}
		parser.reset();
		NodeList metas = parser.extractAllNodesThatMatch(new TagNameFilter("meta"));
		for(Node node : metas.toNodeArray()) {
			MetaTag meta = (MetaTag) node;
			String prop = meta.getAttribute("property");
			String content = meta.getMetaContent();
			if(content != null && !"og:description".equals(prop))
				content = content.trim();
			if("og:novel:book_name".equals(prop)) {
				novel.setName(content);
			} else if("og:description".equals(prop)) {
				novel.setBrief(content.replace("&nbsp;", " ").replace("<br/>", "\n"));
			} else if("og:image".equals(prop)) {
				novel.setThumb(content);
			}else if("og:novel:category".equals(prop)) {
				novel.setKind(content);
			}else if("og:novel:author".equals(prop)) {
				novel.setAuthor(content);
			}else if("og:novel:read_url".equals(prop)) {
				novel.setUrl(content);
			}else if("og:novel:update_time".equals(prop)) {
				novel.setLastUpdateTime(content);
			}else if("og:novel:latest_chapter_name".equals(prop)) {
				novel.setLastUpdateChapter(content);
			}else if("og:novel:latest_chapter_url".equals(prop)) {
				novel.setLastUpdateChapterUrl(content);
			}
		}
		detail.setChapterUrl(url);
		novel.setChapterUrl(url);
		return detail;
	}
	
	/**get the novel brief and chapters*/
	public static NovelDetail getNovelDetail(String url)
			throws ParserException {
		NovelDetail detail = new NovelDetail();
		List<Chapter> chapters = new ArrayList<Chapter>();
		Novel novel = new Novel();
		detail.setNovel(novel);
		detail.setChapters(chapters);
		
		Parser parser = new Parser(HtmlUtil.getHtml(url));
		NodeList nodeList = parser
				.parse(new TagAttrFilter("DIV", "id", "list"));
		nodeList = nodeList.extractAllNodesThatMatch(new TagNameFilter("dd"),
				true);
		int size = nodeList.size();
		for (int i = 0; i < size; i++) {
			Chapter chapter = new Chapter();
			TagNode dd = (TagNode) nodeList.elementAt(i);
			chapter.setUrl(url + "/" + HtmlUtil.getFirstNodeAttr(dd, "a", "href"));
			chapter.setTitle(dd.toPlainTextString());
			chapters.add(chapter);
		}
		parser.reset();
		NodeList maininfo = parser.extractAllNodesThatMatch(new TagAttrFilter("div", "id","maininfo"));
		if(maininfo.size() == 0) {
			Log.e("TT", "getNovelDetail error");
			return detail;
		}
		NodeList nl = maininfo.elementAt(0).getChildren();
		for(Node n : nl.toNodeArray()) {
			if(n instanceof Div) {
				Div div = (Div) n;
				String id = div.getAttribute("id");
				if("info".equals(id)) {
					Node children[] = div.getChildrenAsNodeArray();
					for(Node c : children) {
						if(c instanceof HeadingTag) {
							novel.setName(c.toPlainTextString().trim());
						} else if(c instanceof ParagraphTag) {
							String text = c.toPlainTextString().trim();
							if(text.startsWith(TEXT_5)) {
								novel.setAuthor(text.substring(text.indexOf(TEXT_6) + 1));
							} else if(text.startsWith(TEXT_3)) {
								novel.setLastUpdateTime(text.substring(text.indexOf(TEXT_6) + 1));
							} else if(text.startsWith(TEXT_4)) {
								TagNode tag = HtmlUtil.getFistrtTagNode(c, "a");
								novel.setLastUpdateChapter(tag.toPlainTextString().trim());
								novel.setLastUpdateChapterUrl(url + "/" + tag.getAttribute("href"));
							}
						}
					}
				} else if("intro".equals(id)) {
					novel.setBrief(div.toPlainTextString().trim());
				}
			}
		}
		parser.reset();
		NodeList fmimg = parser.extractAllNodesThatMatch(new TagAttrFilter("div", "id","fmimg"));
		for(int i=0;i<fmimg.size();i++) {
			Node node = fmimg.elementAt(i);
			if(node instanceof Div) {
				novel.setThumb(BASE_URL + HtmlUtil.getFirstNodeAttr(node, "img", "src"));
				break;
			}
		} 
		return detail;
	}

	/** get the last update novels */
	public static List<Novel> getLastUpdates(String baseUrl,String url)
			throws ParserException {
		System.out.println("getLastUpdates");
		List<Novel> novels = new ArrayList<Novel>();
		String html = null;
		if(Util.isUrl(url)) {
		    html = HtmlUtil.readHtml(url);
		} else {
            html = url;
        }
		Parser parser = new Parser(html);
		NodeList nodeList = parser.parse(new TagAttrFilter("DIV", "id",
				"newscontent"));
		Node fnode = null;
		Node newNode = null;
		for (int i = 0; i < nodeList.size(); i++) {
			fnode = nodeList.elementAt(i);
			Div div = (Div) fnode;
			if ("newscontent".equals(div.getAttribute("id"))) {
				List<TagNode> notes = HtmlUtil.getNodesByTag(div, "div");
				for (int j = 0; j < notes.size(); j++) {
					if ("l".equals(((TagNode) notes.get(j))
							.getAttribute("class"))) {
						newNode = notes.get(j);
						break;
					}
				}
				break;
			}
		}
		if (newNode == null) {
			System.out.println("return");
			return novels;
		}
		NodeList nl = newNode.getChildren();
		nl = nl.extractAllNodesThatMatch(new TagAttrFilter("li"), true);
		int size = nl.size();
		for (int i = 0; i < size; i++) {
			TagNode li = (TagNode) nl.elementAt(i);
			NodeList spans = li.getChildren();
			int lisize = spans.size();
			Novel nn = new Novel();
			for (int j = 0; j < lisize; j++) {
				TagNode span = (TagNode) spans.elementAt(j);
				String atc = span.getAttribute("class");
				// System.out.println(span.toPlainTextString());
				String text = span.toPlainTextString();
				if ("s1".equals(atc)) {
					nn.setKind(text);
				} else if ("s2".equals(atc)) {
					nn.setName(text);
					String link = HtmlUtil.getFirstNodeAttr(span, "a", "href");
					URI uri = URI.create(link);
					if(uri.isAbsolute()) {
						nn.setUrl(link);
					} else {
						nn.setUrl(baseUrl + link);
					}
				} else if ("s3".equals(atc)) {
					nn.setLastUpdateChapter(text);
					nn.setLastUpdateChapterUrl(HtmlUtil.getFirstNodeAttr(span, "a", "href"));
				} else if ("s4".equals(atc)) {
					nn.setAuthor(text);
				} else if ("s5".equals(atc)) {
					nn.setLastUpdateTime(text);
				}
			}
			novels.add(nn);
//			System.out.println(nn);
		}
		return novels;
	}
	
}
