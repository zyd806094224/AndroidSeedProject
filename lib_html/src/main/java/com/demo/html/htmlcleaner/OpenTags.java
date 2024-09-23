package com.demo.html.htmlcleaner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Class that contains information and methods for managing list of open,
 * but unhandled tags.
 */
class OpenTags {
	/**
	 * 
	 */
	private final HtmlCleaner htmlCleaner;

	/**
	 * @param htmlCleaner
	 */
	OpenTags(HtmlCleaner htmlCleaner) {
		this.htmlCleaner = htmlCleaner;
	}

	List<TagPos> list = new ArrayList<TagPos>();
	private TagPos last;
	private Set<String> set = new HashSet<String>();

	boolean isEmpty() {
		return list.isEmpty();
	}

	void addTag(String tagName, TagInfo tagInfo, int position, CleanTimeValues cleanTimeValues) {
		last = new TagPos(position, tagName, tagInfo, cleanTimeValues);
		list.add(last);
		set.add(tagName);
	}

	void removeTag(String tagName) {
		ListIterator<TagPos> it = list.listIterator( list.size() );
		while ( it.hasPrevious() ) {
			if (Thread.currentThread().isInterrupted()) {
				this.htmlCleaner.handleInterruption();
				break;
			}
			TagPos currTagPos = it.previous();
			if (tagName.equals(currTagPos.name)) {
				it.remove();
				break;
			}
		}

		last =  list.isEmpty() ? null : (TagPos) list.get( list.size() - 1 );
	}

	TagPos findFirstTagPos() {
		return list.isEmpty() ? null : (TagPos) list.get(0);
	}

	TagPos getLastTagPos() {
		return last;
	}

	TagPos findTag(String tagName, CleanTimeValues cleanTimeValues) {
		if (tagName != null) {
			ListIterator<TagPos> it = list.listIterator(list.size());
			String fatalTag = null;
			TagInfo fatalInfo = this.htmlCleaner.getTagInfo(tagName, cleanTimeValues);

			while (it.hasPrevious()) {
				if (Thread.currentThread().isInterrupted()) {
					this.htmlCleaner.handleInterruption();
					return null;
				}
				TagPos currTagPos = it.previous();
				if (tagName.equals(currTagPos.name)) {
					return currTagPos;
				} else if (fatalInfo != null && fatalInfo.isFatalTag(currTagPos.name)) {
					// do not search past a fatal tag for this tag
					return null;
				}
			}
		}

		return null;
	}

	boolean tagExists(String tagName, CleanTimeValues cleanTimeValues) {
		TagPos tagPos = findTag(tagName, cleanTimeValues);
		return tagPos != null;
	}

	TagPos findTagToPlaceRubbish() {
		TagPos result = null, prev = null;

		if ( !isEmpty() ) {
			ListIterator<TagPos> it = list.listIterator( list.size() );
			while ( it.hasPrevious() ) {
				if (Thread.currentThread().isInterrupted()) {
					this.htmlCleaner.handleInterruption();
					return null;
				}
				result = it.previous();
				if ( result.info == null || result.info.allowsAnything() ) {
					if (prev != null) {
						return prev;
					}
				}
				prev = result;
			}
		}

		return result;
	}

	boolean tagEncountered(String tagName) {
		return set.contains(tagName);
	}

	/**
	 * Checks if any of tags specified in the set are already open.
	 * @param tags
	 */
	boolean someAlreadyOpen(Set<String> tags) {
		for (TagPos curr : list) {
			if ( tags.contains(curr.name) ) {
				return true;
			}
		}
		return false;
	}
}