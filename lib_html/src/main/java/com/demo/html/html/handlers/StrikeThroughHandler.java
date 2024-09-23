package com.demo.html.html.handlers;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;

import com.demo.html.html.utils.SpanStack;
import com.demo.html.html.utils.TagNodeHandler;
import com.demo.html.htmlcleaner.TagNode;


public class StrikeThroughHandler extends TagNodeHandler {

    @Override
    public void handleTagNode(final TagNode node, final SpannableStringBuilder builder, final int start, final int end, final SpanStack spanStack) {
        builder.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}
