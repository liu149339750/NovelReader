package com.lw.ttzw;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.lw.bean.Chapter;
import com.lw.bean.Novel;
import com.lw.bean.NovelDetail;
import com.lw.novel.utils.HtmlUtil;
import com.lw.novel.utils.TagAttrFilter;

public class BiqugeManager {

	
    /**get novel detail and chapter list*/
	public static NovelDetail getNovelDetailByMeta(String url, String tag) throws ParserException {
		NovelDetail detail = new NovelDetail();
		List<Chapter> chapters = new ArrayList<Chapter>();
		Novel novel = new Novel();
		detail.setNovel(novel);
		detail.setChapters(chapters);
		
		Parser parser = new Parser(url);
		NodeList nodeList = parser
				.parse(new TagAttrFilter("DIV", "id", "list"));
		
		getChapters(chapters, nodeList,tag);
		novel.setChapterUrl(url);
		detail.setChapterUrl(url);
		parser.reset();
		getDetailByMeta(novel, parser);
		return detail;
	}

	private static void getDetailByMeta(Novel novel, Parser parser) throws ParserException {
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
	}
	
	public static String getChapterContent(String url) throws ParserException {
		return TTZWManager.getChapterContent(url);
	}
	
	public static List<Chapter> getChapters(String url,String source) throws ParserException {
		List<Chapter> chapters = new ArrayList<Chapter>();
		Parser parser = new Parser(url);
		NodeList nodeList = parser
				.parse(new TagAttrFilter("DIV", "id", "list"));
		getChapters(chapters, nodeList,source);
		return chapters;
	}

	private static void getChapters(List<Chapter> chapters, NodeList nodeList, String source) {
		nodeList = nodeList.extractAllNodesThatMatch(new TagNameFilter("dl"),
				true);
		int size = nodeList.size();
		if(size == 0)
			return;
		nodeList = nodeList.elementAt(0).getChildren();
		size = nodeList.size();
		boolean start = false;
		for (int i = 0; i < size; i++) {
			Node node = nodeList.elementAt(i);
			if(!(node instanceof TagNode))
				continue;
			TagNode ddt = (TagNode) node;
			String title = ddt.toPlainTextString();
			if(!start && title.contains("正文")) {
				start = true;
				continue;
			} else if(!start) {
				continue;
			} else if("dt".equals(ddt.getTagName())) {
				continue;
			}
			Chapter chapter = new Chapter();
			chapter.setUrl(HtmlUtil.getFirstNodeAttr(ddt, "a", "href"));
			chapter.setTitle(title);
			chapter.setSource(source);
			chapters.add(chapter);
		}
	}
}
