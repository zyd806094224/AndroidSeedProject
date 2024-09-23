package com.demo.common.text.html.ctrl;

import android.text.Editable;
import android.text.Spanned;
import android.widget.TextView;

import com.demo.common.text.html.tag.NoColorUrlTag;
import com.demo.common.text.span.NoColorURLSpan;
import com.demo.html.html.ctrl.base.AbstractHtmlTagHandler;

import org.xml.sax.XMLReader;

/**
 * 解析富文本终  <hy_a href='xxxx'></hy_a> 的元素，支持自定义点击
 * HtmlTagCtrlFactory.setText的时候最好增加OnSpanSetListener，因为span可能会刷新
 */
public class NoColorUrlHandler extends AbstractHtmlTagHandler {

    public NoColorUrlHandler(TextView textView) {
        super(textView);
    }

    @Override
    public boolean processAble(String tag) {
        return NoColorUrlTag.NAME.equalsIgnoreCase(tag);
    }

    @Override
    public void handleStartTag(String tag, Editable output, XMLReader xmlReader) {
        this.attributes = this.getAttributes(xmlReader);
        String href = getAttr("href");
        NoColorURLSpan span = new NoColorURLSpan(href);
        output.setSpan(span, output.length(), output.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        this.attributes.clear();
    }

    @Override
    public void handleEndTag(String tag, Editable output, XMLReader xmlReader) {
        int len = output.length();
        NoColorURLSpan span = getLast(output, NoColorURLSpan.class);
        if (span == null) return;
        int start = output.getSpanStart(span);
        output.setSpan(span, start, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


}
