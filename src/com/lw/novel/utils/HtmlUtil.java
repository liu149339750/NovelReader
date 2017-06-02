package com.lw.novel.utils;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.lw.ttzw.DataQueryManager;

import android.text.TextUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HtmlUtil {

	/**get the children nodes*/
	public static List<TagNode> getNodesByTag(Node parent,String tag) {
		Node fnode = parent.getFirstChild();
		List<TagNode> nodes = new ArrayList<TagNode>();
		if(fnode != null) {
			while(fnode != null) {
				if((fnode instanceof TagNode)) {
					TagNode tagNode = (TagNode) fnode; 
					if((tagNode.getTagName().equalsIgnoreCase(tag))) {
						nodes.add(tagNode);
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
	
	public static TagNode getFirstNodeByAttr(Node parent,String tag,String ...attr) {
		NodeList nl = parent.getChildren().extractAllNodesThatMatch(new TagAttrFilter(tag, attr), true);
		if(nl.size() > 0) 
			return (TagNode) nl.elementAt(0);
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
	
	public static String trim(String text) {
		if(TextUtils.isEmpty(text)) {
			return "";
		}
		return text.trim().replace("&nbsp;", " ");
	}
	
	public static String parseContent(Node node) {
		String content = "";
		int size = node.getChildren().size();
		for (int i = 0; i < size; i++) {
			// System.out.println(node.getChildren().elementAt(i).getClass().getName());
			Node cn = node.getChildren().elementAt(i);
			if (cn instanceof TextNode) {
				content += cn.toPlainTextString().replace("&nbsp;", " ");
//					System.out.println(cn.toPlainTextString());
			} else if (cn instanceof TagNode) {
				TagNode tn = (TagNode) cn;
				if ("br".equalsIgnoreCase(tn.getTagName())) {
					content += "\n";
				}
				// System.out.println(tn.getTagName());
			}
		}
		return content;
	}
	
	public static String readHtml(String url) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();
		try {
			Response response = client.newCall(request).execute();
			String content = response.body().string();
			return content;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
