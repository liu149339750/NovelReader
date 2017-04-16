package com.lw.novel.common;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;


public class HtmlUtil {

	/**get the children nodes*/
	public static List<Node> getNodesByTag(Node parent,String tag) {
		Node fnode = parent.getFirstChild();
		List<Node> nodes = new ArrayList<Node>();
		if(fnode != null) {
			while(fnode != null) {
				if((fnode instanceof TagNode)) {
					if(((TagNode) fnode).getTagName().equalsIgnoreCase(tag)) {
						nodes.add(fnode);
//						if(deep&&hasChild(fnode)) {
//							nodes.addAll(getNodesByTag(parent, tag,deep)); //I make a mistake,the parent should be fnode.
//						}
					} else if(hasChild(fnode)) {
						nodes.addAll(getNodesByTag(fnode, tag));
					}
				}  
				fnode = fnode.getNextSibling();
			}
		}
		return nodes;
	}
	
	
	public static TagNode getFistrtTagNode(Node parent,String tag) {
		NodeList nl = parent.getChildren();
		int size = nl.size();
		if(nl.size() == 0) {
			return null;
		}
		for(int i=0;i<size;i++) {
			Node node =  nl.elementAt(i);
			if(node instanceof TagNode) {
				TagNode tnode = (TagNode) node;
				if(tnode.getTagName().equalsIgnoreCase(tag)) {
					return tnode;
				}
			}
		}
		return null;
	}
	
	/**get the first node that match the gived tag*/
	public static String getFirstNodeAttr(Node parent,String tag,String attr) {
		NodeList nl = parent.getChildren();
		int size = nl.size();
		if(nl.size() == 0) {
			return null;
		}
		for(int i=0;i<size;i++) {
			Node node =  nl.elementAt(i);
			if(node instanceof TagNode) {
				TagNode tnode = (TagNode) node;
				if(tnode.getTagName().equalsIgnoreCase(tag)) {
					return tnode.getAttribute(attr);
				}
			}
		}
		return null;
	}
	
	public static NodeList getAllTagNodeChildren(Node node) {
		NodeList nl = node.getChildren();
		return nl.extractAllNodesThatMatch(new NodeFilter() {
			
			public boolean accept(Node node) {
				if(node instanceof TagNode)
					return true;
				return false;
			}
		}, true);
	}
	
	public static boolean hasChild(Node node) {
		NodeList children = node.getChildren();
		return children != null && children.size() > 0;
	}
}
