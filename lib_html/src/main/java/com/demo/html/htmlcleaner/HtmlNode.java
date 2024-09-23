package com.demo.html.htmlcleaner;

import java.util.List;

/**
 * Marker interface denoting nodes of the document tree
 */
public interface HtmlNode extends BaseToken {
	
    public List<? extends BaseToken> getSiblings();
    
    public TagNode getParent();
    
    public void setParent(TagNode parent);
    
}