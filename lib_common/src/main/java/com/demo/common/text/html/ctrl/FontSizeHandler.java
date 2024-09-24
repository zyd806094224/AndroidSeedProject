package com.demo.common.text.html.ctrl;

import android.text.Editable;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.widget.TextView;

import com.demo.common.text.html.tag.FontSizeTag;
import com.demo.framework.helper.AppHelper;
import com.demo.framework.utils.DisplayUtils;
import com.demo.html.html.ctrl.base.AbstractHtmlTagHandler;

import org.xml.sax.XMLReader;

import java.util.Stack;

/**
 * 字体大小设置
 *
 * <size value=10>文本大小设置</size>
 * <p>
 * value的值表示 sp 单位的两倍值
 */
public class FontSizeHandler extends AbstractHtmlTagHandler {

    /**
     * html 标签的开始下标
     */
    private Stack<Integer> startIndex;

    /**
     * html的标签的属性值 value，如:<size value='16'></size>
     * 注：value的值不能带有单位,默认就是sp
     */
    private Stack<String> propertyValue;

    public FontSizeHandler(TextView textView) {
        super(textView);
    }

    @Override
    public boolean processAble(String tag) {
        return FontSizeTag.NAME.equalsIgnoreCase(tag);
    }

    @Override
    public void handleStartTag(String tag, Editable output, XMLReader xmlReader) {
        if (startIndex == null) {
            startIndex = new Stack<>();
        }
        startIndex.push(output.length());

        if (propertyValue == null) {
            propertyValue = new Stack<>();
        }
        propertyValue.push(this.getAttribute(xmlReader, "value"));
    }


    @Override
    public void handleEndTag(String tag, Editable output, XMLReader xmlReader) {
        if (propertyValue != null && !propertyValue.isEmpty()) {
            try {
                int fondSize = DisplayUtils.INSTANCE.spToPx(AppHelper.INSTANCE.getApplication(), this.getFontSize(propertyValue.pop()));
                int start = startIndex.pop();
                int end = output.length();
                output.setSpan(new AbsoluteSizeSpan(fondSize), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private float getFontSize(String originFontSize) {
        return Float.parseFloat(originFontSize) / 2;
    }
}
