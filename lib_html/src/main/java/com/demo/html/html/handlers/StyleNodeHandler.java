package com.demo.html.html.handlers;

import android.text.SpannableStringBuilder;
import android.util.Log;

import com.demo.html.cssparser.CSSParser;
import com.demo.html.cssparser.Rule;
import com.demo.html.html.css.CSSCompiler;
import com.demo.html.html.utils.SpanStack;
import com.demo.html.html.utils.TagNodeHandler;
import com.demo.html.htmlcleaner.ContentNode;
import com.demo.html.htmlcleaner.TagNode;


/**
 * TagNodeHandler that reads <style> blocks and parses the CSS rules within.
 */
public class StyleNodeHandler extends TagNodeHandler {

    @Override
    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack spanStack) {

        if ( getSpanner().isAllowStyling() ) {

            if ( node.getAllChildren().size() == 1 ) {
                Object childNode = node.getAllChildren().get(0);

                if ( childNode instanceof ContentNode) {
                    parseCSSFromText( ( (ContentNode) childNode ).getContent(),
                            spanStack );
                }
            }
        }

    }

    private void parseCSSFromText( String text, SpanStack spanStack ) {
        try {
            for ( Rule rule: CSSParser.parse( text ) ) {
                spanStack.registerCompiledRule(CSSCompiler.compile(rule, getSpanner()));
            }
        } catch ( Exception e ) {
            Log.e( "StyleNodeHandler", "Unparseable CSS definition", e );
        }
    }

    @Override
    public boolean rendersContent() {
        return true;
    }
}
