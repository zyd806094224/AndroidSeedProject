package com.demo.html.htmlcleaner.conditional;


import com.demo.html.htmlcleaner.TagNode;

/**
 * All nodes.
 */
public class TagAllCondition implements ITagNodeCondition {
    public boolean satisfy(TagNode tagNode) {
        return true;
    }
}