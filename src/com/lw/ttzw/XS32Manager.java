package com.lw.ttzw;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.lw.bean.Novel;
import com.lw.bean.Novels;
import com.lw.novel.utils.HtmlUtil;
import com.lw.novel.utils.TagAttrFilter;

public class XS32Manager {

	
	public static final String BASE_URL = "http://www.32xs.org";
	
	
	public static Novels get32XSSortKindNovels(String url) throws ParserException {
		Novels novels = new Novels();
		novels.setCurrentUrl(url);
		List<Novel> listNovel = new ArrayList<Novel>();
		novels.setNovels(listNovel);
		Parser parser = new Parser(HtmlUtil.getHtml(url));
		parser.setEncoding("GBK");
		NodeList nodelist = parser.parse(new TagAttrFilter("ul", "class","item-con"));
		if(nodelist.size() < 1) {
			return novels;
		}
		Node itemCon = nodelist.elementAt(0);
		List<TagNode>nodes = HtmlUtil.getNodesByTag(itemCon, "li");
		for(int i=0;i<nodes.size();i++) {
			Node novelNode = nodes.get(i);
			Novel novel = parseXS32NovelNode(novelNode);
			System.out.println(novel);
			listNovel.add(novel);
		}
		parser.reset();
		NodeList nl = parser.extractAllNodesThatMatch(new TagAttrFilter("a", "class","next"));
		if(nl.size() > 0) {
			TagNode tag = (TagNode) nl.elementAt(0);
			novels.setNextUrl(tag.getAttribute("href"));
		}
		return novels;
	}
	
	
	private static Novel parseXS32NovelNode(Node novelNode) {
		Novel novel = new Novel();
		NodeList nl = novelNode.getChildren();
		for(int i=0;i<nl.size();i++){
			Node n = nl.elementAt(i);
			if(n instanceof TagNode) {
				TagNode tag = (TagNode) n;
				String clas = tag.getAttribute("class");
				String text = tag.toPlainTextString().trim();
				if("s1".equals(clas)) {
					novel.setKind(text);
				} else if("s2".equals(clas)) {
					List<TagNode> as = HtmlUtil.getNodesByTag(tag, "a");
					if(as.size() >= 2) {
						TagNode nameNode = as.get(0);
						String url = nameNode.getAttribute("href");
						novel.setUrl(url);
						novel.setName(nameNode.toPlainTextString().trim());
						
						TagNode lastUpdateNode = as.get(1);
						novel.setLastUpdateChapterUrl(lastUpdateNode.getAttribute("href"));
						novel.setLastUpdateChapter(lastUpdateNode.toPlainTextString().trim());
					}
				} else if("s3".equals(clas)) {
					novel.setAuthor(text);
				} else if("s4".equals(clas)) {
					novel.setLastUpdateTime(text);
				}
			}
		}
		return novel;
	}
}