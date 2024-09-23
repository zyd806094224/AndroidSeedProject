package com.demo.html.htmlcleaner;

/**
 * Contains information about a single open tag
 */

class TagPos {
	
	int position;
	String name;
	TagInfo info;

	TagPos(int position, String name, TagInfo tagInfo, CleanTimeValues cleanTimeValues) {
		this.position = position;
		this.name = name;
		this.info = tagInfo;
	}
}