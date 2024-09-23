package com.demo.html.htmlcleaner.conditional;


import com.demo.html.htmlcleaner.TagNode;

/**
 * Checks if node has specified name.
 */
public class TagNodeNameCondition implements ITagNodeCondition {
    private String name;

    public TagNodeNameCondition(String name) {
        this.name = name;
    }

    public boolean satisfy(TagNode tagNode) {
        return tagNode == null ? false : tagNode.getName().equalsIgnoreCase(this.name);
    }
}