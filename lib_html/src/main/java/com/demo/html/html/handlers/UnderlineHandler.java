package com.demo.html.html.handlers;

import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;

import com.demo.html.html.utils.SpanStack;
import com.demo.html.html.utils.TagNodeHandler;
import com.demo.html.htmlcleaner.TagNode;


public class UnderlineHandler extends TagNodeHandler {

    @Override
    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack spanStack) {
        spanStack.pushSpan(new UnderlineSpan(), start, end);
    }
}
