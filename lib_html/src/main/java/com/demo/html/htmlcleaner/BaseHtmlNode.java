package com.demo.html.htmlcleaner;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class BaseHtmlNode extends BaseTokenImpl implements HtmlNode {
	
    protected TagNode parent;

    public List<? extends BaseToken> getSiblings(){
    	//
    	// If this is a root node, return an empty list
    	//
    	if (this.parent == null) { return new ArrayList<BaseToken>(); };
    	//
    	// Otherwise, return all the children, including this node
    	//
    	return this.parent.getAllChildren();
    }

	public TagNode getParent() {
		return parent;
	}

	public void setParent(TagNode parent) {
		this.parent = parent;
	}

	public void serialize(Serializer serializer, Writer writer)
			throws IOException {
		// TODO Auto-generated method stub
	}
    
    

}
