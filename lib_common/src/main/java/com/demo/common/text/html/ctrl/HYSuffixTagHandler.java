package com.demo.common.text.html.ctrl;

import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.widget.TextView;

import com.demo.common.text.html.ctrl.img.HtmlImageGetter;
import com.demo.common.text.html.tag.HYSuffixTag;
import com.demo.common.text.span.ImgSpan;
import com.demo.common.text.span.SuffixImgSpan;
import com.demo.html.html.ctrl.base.AbstractHtmlTagHandler;

import org.xml.sax.XMLReader;

/**
 * @Description:
 * @Date: 2023/8/2 12:05 PM
 * 
 * @version: 1.0
 */
public class HYSuffixTagHandler extends AbstractHtmlTagHandler {

    private HtmlImageGetter imageGetter;

    public HYSuffixTagHandler(TextView textView) {
        super(textView);
        imageGetter = new HtmlImageGetter();
    }

    @Override
    public boolean processAble(String tag) {
        return HYSuffixTag.NAME.equalsIgnoreCase(tag);
    }

    @Override
    public void handleStartTag(String tag, Editable output, XMLReader xmlReader) {
        this.attributes = this.getAttributes(xmlReader);
        String src = getAttr("src");
        float leftPad = getFloatAttr("left_pad");
        float height = getFloatAttr("height");
        float width = getFloatAttr("width");

        SuffixImgSpan span = new SuffixImgSpan(imageGetter.getDrawable(src, new HtmlImageGetter.OnImageLoadComplete() {
            @Override
            public void onImageLoadComplete(BitmapDrawable drawable) {
                if (onTagLoadListener != null) {
                    onTagLoadListener.refreshView();
                }
            }
        }, (int)Math.ceil(width), (int)Math.ceil(height)), ImgSpan.ALIGN_CENTER);
        span.setLeftPad((int)Math.ceil(leftPad));
        int len = output.length();
        output.append("\uFFFC");
        output.setSpan(span, len, output.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void handleEndTag(String tag, Editable output, XMLReader xmlReader) {

    }

}
