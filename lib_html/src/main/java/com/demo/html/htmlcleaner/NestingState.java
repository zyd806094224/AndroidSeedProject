package com.demo.html.htmlcleaner;


/**
 * Nesting State
 * Wrapper for a current HtmlCleaner cleaning state, keeping together
 * the set of open tags and breaks in the current state.
 * @author scottw
 */
class NestingState {
	
	private OpenTags openTags;
	private ChildBreaks childBreaks;
	
	public NestingState(OpenTags openTags, ChildBreaks childBreaks) {
		this.openTags = openTags;
		this.childBreaks = childBreaks;
	}

	public OpenTags getOpenTags() {
		return this.openTags;
	}
	public ChildBreaks getChildBreaks() {
		return this.childBreaks;
	}
}