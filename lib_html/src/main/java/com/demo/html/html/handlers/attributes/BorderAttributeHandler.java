package com.demo.html.html.handlers.attributes;

import android.text.SpannableStringBuilder;
import android.util.Log;

import com.demo.html.html.handlers.StyledTextHandler;
import com.demo.html.html.spans.BorderSpan;
import com.demo.html.html.style.Style;
import com.demo.html.html.utils.SpanStack;
import com.demo.html.htmlcleaner.TagNode;


/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 6/23/13
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class BorderAttributeHandler extends WrappingStyleHandler {

    public BorderAttributeHandler(StyledTextHandler handler) {
        super(handler);
    }

    @Override
    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end,
                              Style useStyle, SpanStack spanStack) {

        if ( node.getAttributeByName("border") != null ) {
            Log.d("BorderAttributeHandler", "Adding BorderSpan from " + start + " to " + end);
            spanStack.pushSpan(new BorderSpan(useStyle, start, end, getSpanner()), start, end);
        }

        super.handleTagNode(node, builder, start, end, useStyle, spanStack);

    }


}
