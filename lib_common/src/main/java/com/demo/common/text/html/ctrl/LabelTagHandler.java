package com.demo.common.text.html.ctrl;

import android.text.Editable;
import android.text.Spanned;
import android.widget.TextView;

import com.demo.common.text.html.tag.LabelTag;
import com.demo.common.text.span.LabelSpan;
import com.demo.html.html.ctrl.base.AbstractHtmlTagHandler;

import org.xml.sax.XMLReader;

public class LabelTagHandler extends AbstractHtmlTagHandler {

    public static final String TAG = "label";

    public LabelTagHandler(TextView textView) {
        super(textView);
    }

    @Override
    public boolean processAble(String tag) {
        return LabelTag.NAME.equalsIgnoreCase(tag);
    }

    @Override
    public void handleStartTag(String tag, Editable output, XMLReader xmlReader) {
        this.attributes = this.getAttributes(xmlReader);
        LabelSpan span = new LabelSpan();
        span.setBgColor(getColorAttr("background"));
        span.setPaddingHorizontal(getDipAttr("padding"));
        span.setRadius(getDipAttr("radius"));
        span.setRadius(getDipAttr("left_top"), getDipAttr("right_top"),
                getDipAttr("left_bottom"), getDipAttr("right_bottom"));
        span.setMarginRight(getDipAttr("left_margin"));
        span.setMarginRight(getDipAttr("right_margin"));
        span.setTextColor(getColorAttr("color"));
        span.setTextSize(getDipAttr("size"));
        output.setSpan(span, output.length(), output.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        this.attributes.clear();
    }

    @Override
    public void handleEndTag(String tag, Editable output, XMLReader xmlReader) {
        int len = output.length();
        LabelSpan span = getLast(output, LabelSpan.class);
        if (span == null) return;
        int start = output.getSpanStart(span);
        String text = output.toString().substring(start, len);
        span.setText(text);
        output.removeSpan(span);
        output.setSpan(span, start, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


}
