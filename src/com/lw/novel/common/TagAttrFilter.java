package com.lw.novel.common;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.nodes.TagNode;

 
public class TagAttrFilter implements NodeFilter{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tag;
	private String attr[];

	public TagAttrFilter(String tag,String ...attr) {
		this.tag = tag;
		this.attr = attr;
	}
	public boolean accept(Node node) {
		if(!(node instanceof TagNode))
			return false;
		TagNode tn = (TagNode) node;
		if(!tn.getTagName().equalsIgnoreCase(tag)) {
			return false;
		}
		if(attr == null || attr.length%2 == 1)
			return false;
		for(int i =0;i<attr.length;i+=2){
			if(!attr[i + 1].equalsIgnoreCase(tn.getAttribute(attr[i])))
				return false;
		}
		return true;
	}

}
