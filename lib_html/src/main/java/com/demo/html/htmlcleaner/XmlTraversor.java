package com.demo.html.htmlcleaner;

import java.util.Iterator;
import java.util.List;

/**
 * Depth-first node traversor. Use to iterate through all nodes under and including the specified root node.
 * <p>
 * This implementation does not use recursion, so a deep DOM does not risk blowing the stack.
 * </p>
 */
public class XmlTraversor {
    private XmlVisitor visitor;

    /**
     * Start a depth-first traverse of the root and all of its descendants.
     * @param visitor Node visitor.
     * @param root the root node point to traverse.
     */
    public static void traverse(XmlVisitor visitor, HtmlNode root) {
        HtmlNode node = root;
        int depth = 0;
        
        while (node != null) {
            visitor.head(node, depth);
            if ( node instanceof TagNode && ((TagNode)node).hasChildren() ) {
                node = (HtmlNode)((TagNode)node).getAllChildren().get(0);
                depth++;
            } else {
            	List<? extends BaseToken> siblings = node.getSiblings();
            	Iterator<? extends BaseToken> it = siblings.iterator();
                while (it.hasNext() && it.next() == null && depth > 0) {
                    visitor.tail(node, depth);
                    node = node.getParent();
                    depth--;
                }
                visitor.tail(node, depth);
                if (node == root)
                    break;
                if (it.hasNext()){
                	node = (HtmlNode)it.next();
                } else {
                	node = null;
                }
            }
        }
    }
}