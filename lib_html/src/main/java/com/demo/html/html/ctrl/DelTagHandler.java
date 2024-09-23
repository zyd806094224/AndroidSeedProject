package com.demo.html.html.ctrl;

import android.text.Editable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.widget.TextView;

import com.demo.html.html.ctrl.base.AbstractHtmlTagHandler;

import org.xml.sax.XMLReader;

import java.util.Stack;

/**
 * 删除线
 * <p>
 * 由于 del 标签不支持展示删除线，特殊实现，自行添加删除线
 * <del>删除线</del>
 */
public class DelTagHandler extends AbstractHtmlTagHandler {

    public static final String TAG = "del";
    /**
     * html 标签的开始下标
     */
    private Stack<Integer> startIndex;

    public DelTagHandler(TextView textView) {
        super(textView);
    }

    @Override
    public boolean processAble(String tag) {
        return TAG.equalsIgnoreCase(tag);
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
        output.setSpan(new StrikethroughSpan(), startIndex.pop(), output.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
