package com.demo.html.htmlcleaner;

import java.util.Stack;

/**
 * Contains information about nodes that were closed due to their child nodes.
 * i.e. if 'p' tag was closed due to 'table' child tag.
 *
 * @author Konstantin Burov
 *
 */
class ChildBreaks{
	Stack <TagPos> closedByChildBreak = new Stack < TagPos >();
	private Stack < TagPos > breakingTags = new Stack < TagPos >();

	/**
	 * Adds the break info to the top of the stacks.
	 *
	 * @param closedPos - position of the tag that was closed due to incorrect child
	 * @param breakPos - position of the child that has broken its parent
	 */
	public void addBreak(TagPos closedPos, TagPos breakPos){
		closedByChildBreak.add(closedPos);
		breakingTags.add(breakPos);
	}

	public boolean isEmpty() {
		return closedByChildBreak.isEmpty();
	}

	/**
	 * @return name of the last children tag that has broken its parent.
	 */
	public String getLastBreakingTag() {
		return breakingTags.peek().name;
	}

	/**
	 * pops out latest broken tag position.
	 *
	 * @return tag pos of the last parent that was broken.
	 */
	public TagPos pop() {
		breakingTags.pop();
		return closedByChildBreak.pop();
	}

	/**
	 * @return position of the last tag that has broken its parent. -1 if no such tag found.
	 */
	public int getLastBreakingTagPosition() {
		return breakingTags.isEmpty()?-1:breakingTags.peek().position;
	}
}