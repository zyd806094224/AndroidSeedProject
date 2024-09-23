package com.demo.html.html.handlers;

import android.text.Spannable;
import android.text.SpannableStringBuilder;

import com.demo.html.html.spans.HrSpan;
import com.demo.html.html.utils.SpanStack;
import com.demo.html.html.utils.TagNodeHandler;
import com.demo.html.htmlcleaner.TagNode;


public class HorizontalRuleHandler extends TagNodeHandler {

    @Override
    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack spanStack) {
        appendNewLine(builder);
        int hrStart = builder.length();
        builder.append("\uFFFC"); // gets spanned with the horizontal rule
        int hrEnd = builder.length();
        appendNewLine(builder);

        builder.setSpan(new HrSpan(), hrStart, hrEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
