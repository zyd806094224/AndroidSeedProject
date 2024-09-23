package com.demo.common.text.html.ctrl;


import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.widget.TextView;

import com.demo.common.text.html.ctrl.img.HtmlImageGetter;
import com.demo.common.text.html.tag.HYImageTag;
import com.demo.common.text.span.ImgSpan;
import com.demo.html.html.ctrl.base.AbstractHtmlTagHandler;

import org.xml.sax.XMLReader;

public class HYImageTagHandler extends AbstractHtmlTagHandler {




    private HtmlImageGetter imageGetter;

    public HYImageTagHandler(TextView textView) {
        super(textView);
        imageGetter = new HtmlImageGetter();
    }

    @Override
    public boolean processAble(String tag) {
        return HYImageTag.NAME.equalsIgnoreCase(tag);
    }

    @Override
    public void handleStartTag(String tag, Editable output, XMLReader xmlReader) {
        this.attributes = this.getAttributes(xmlReader);
        String src = getAttr("src");
        int offset = getDipAttr("offset");
        int leftPad = getDipAttr("left_pad");
        int rightPad = getDipAttr("right_pad");
        int height = getDipAttr("height");
        float heightPx = getFloatAttr("height_px");
        float widthPx = getFloatAttr("width_px");
        int width = getDipAttr("width");
        ImgSpan span = new ImgSpan(imageGetter.getDrawable(src, new HtmlImageGetter.OnImageLoadComplete() {
            @Override
            public void onImageLoadComplete(BitmapDrawable drawable) {
                if (onTagLoadListener != null) {
                    onTagLoadListener.refreshView();
                }
            }
        }, widthPx == 0 ? width : (int) widthPx, heightPx == 0 ? height : (int) heightPx), ImgSpan.ALIGN_CENTER);
        span.setOffset(offset);
        span.setLeftPad(leftPad);
        span.setRightPad(rightPad);
        span.setOffset(offset);
        int len = output.length();
        output.append("\uFFFC");
        output.setSpan(span, len, output.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void handleEndTag(String tag, Editable output, XMLReader xmlReader) {

    }

}
