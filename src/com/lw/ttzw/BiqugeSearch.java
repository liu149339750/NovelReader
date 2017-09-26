package com.lw.ttzw;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.lw.bean.Novel;
import com.lw.bean.Novels;
import com.lw.novel.utils.HtmlUtil;
import com.lw.novel.utils.TagAttrFilter;

import android.text.TextUtils;

public class BiqugeSearch {
	
	public static final String URL = "http://www.biquge5200.com/modules/article/search.php?searchkey=";
	
	public static Novels searchUrl(String url) throws ParserException {
		Parser parser = new Parser(url);
		NodeList list = parser.parse(new TagAttrFilter("table", "class","grid"));
		int size = list.size();
		if(size < 1)
			return null;
		Novels novels = new Novels();
		List<Novel> data = new ArrayList<Novel>();
		novels.setCurrentUrl(url);
		novels.setNovels(data);
		
		Node parent = list.elementAt(0);
		NodeList children = parent.getChildren();
		for(Node node : children.toNodeArray()) {
			if(!(node instanceof TableRow))
				continue;
			TagNode tagNode = (TagNode) node;
			if(!TextUtils.isEmpty(tagNode.getAttribute("align")))
				continue;
			Novel novel = parseNovelNode(tagNode);
			data.add(novel);
		}
		return novels;
	}

	
	public static Novels searchBiquge(String keyword) throws ParserException {
		String url = URL + keyword;
		return searchUrl(url);
	}

	private static Novel parseNovelNode(Node tr) {
		NodeList list = tr.getChildren();
		Novel novel = new Novel();
		for(Node td : list.toNodeArray()) {
			if(!(td instanceof TableColumn))
				continue;
			TableColumn column = (TableColumn) td;
			String cl = column.getAttribute("class");
			if("odd".equals(cl)) {
				if(TextUtils.isEmpty(novel.name)) {
					novel.name = column.toPlainTextString();
					novel.url = HtmlUtil.getFirstNodeAttr(column, "a", "href");
				} else if(TextUtils.isEmpty(novel.author)) {
					novel.author = column.toPlainTextString();
				} else {
					novel.lastUpdateTime = column.toPlainTextString();
				}
			} else if ("even".equals(cl)) {
				if(TextUtils.isEmpty(novel.lastUpdateChapter)) {
					novel.lastUpdateChapter = column.toPlainTextString();
					if(TextUtils.isEmpty(novel.lastUpdateChapter)) {
						novel.lastUpdateChapter = ".";
					}
					novel.lastUpdateChapterUrl = HtmlUtil.getFirstNodeAttr(column, "a", "href");
				}
			}
			
		}
		return novel;
	}
}
