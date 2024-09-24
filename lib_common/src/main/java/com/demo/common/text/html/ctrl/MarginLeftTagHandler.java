package com.demo.common.text.html.ctrl;

import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import com.demo.common.text.html.tag.MarginLeftTag;
import com.demo.common.text.span.MarginLeftSpan;
import com.demo.framework.helper.AppHelper;
import com.demo.framework.utils.DisplayUtils;
import com.demo.html.html.ctrl.base.AbstractHtmlTagHandler;

import org.xml.sax.XMLReader;

import java.util.Stack;

/**
 * 设置 marginLeft
 * <ml>10</ml>该文本距离左侧有5dp
 */
public class MarginLeftTagHandler extends AbstractHtmlTagHandler {

    /**
     * html 标签的开始下标
     */
    private Stack<Integer> startIndex;

    public MarginLeftTagHandler(TextView textView) {
        super(textView);
    }

    @Override
    public boolean processAble(String tag) {
        return MarginLeftTag.NAME.equalsIgnoreCase(tag);
    }

    @Override
    public void handleStartTag(String tag, Editable output, XMLReader xmlReader) {
        if (startIndex == null) {
            startIndex = new Stack<>();
        }
        startIndex.push(output.length());
    }

    @Override
    public void handleEndTag(String tag, Editable output, XMLReader xmlReader) {
        int start = startIndex.pop();
        int end = output.length();
        int marginLeft = 0;
        if (end > start) {
            try {
                marginLeft = this.getSize(output.toString().substring(start, end));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        output.setSpan(new MarginLeftSpan(marginLeft), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private int getSize(String originSize) {
        if (TextUtils.isEmpty(originSize)) {
            return 0;
        }

        // 首先按照 750 计算出来多少 dp，然后按照设计稿的 720 与 750 算出真正的 dp 值

        float dpFValue = Float.parseFloat(originSize) / 2;

        int dpValue = (int) dpFValue;
        return DisplayUtils.INSTANCE.dpToPx(AppHelper.INSTANCE.getApplication(), dpValue);
    }
}
