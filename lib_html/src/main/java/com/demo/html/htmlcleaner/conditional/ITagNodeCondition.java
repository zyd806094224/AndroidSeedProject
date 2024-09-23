package com.demo.html.htmlcleaner.conditional;


import com.demo.html.htmlcleaner.TagNode;

/**
 * Used as base for different node checkers.
 */
public interface ITagNodeCondition {
    public boolean satisfy(TagNode tagNode);
}