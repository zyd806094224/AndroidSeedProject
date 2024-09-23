package com.demo.common.utils;

import android.graphics.Paint;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.demo.common.text.html.tag.HYImageTag;
import com.demo.common.text.html.tag.HYSuffixTag;
import com.demo.common.text.span.WhiteSpaceSpan;
import com.demo.html.html.HtmlTagCtrlFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Date: 2023/5/16 8:08 下午
 * 
 * @version: 1.0
 */
public class HYUIKitUtil {

    /**
     * 给 TextView 设置文本的同时设置前缀 span 文本，最终调用 HtmlTagCtrlFactory 对标签进行解析生成内容
     *
     * @param text
     * @param textView
     * @param prefixHtmlTagStrArray 前缀 html 标签 文本内容,支持数组
     * @Description:
     * @DateTime 2023-07-27, 周四, 10:45
     *
     */
    public static void setTextWithPrefix(@Nullable String text, TextView textView, @Nullable String... prefixHtmlTagStrArray) {
        if (textView == null) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (prefixHtmlTagStrArray != null && prefixHtmlTagStrArray.length > 0) {
            for (String item : prefixHtmlTagStrArray) {
                if (!TextUtils.isEmpty(item)) {
                    stringBuilder.append(item);
                }
            }
        }
        if (!TextUtils.isEmpty(text)) {
            stringBuilder.append(text);
        }
        HtmlTagCtrlFactory.getInstance().setText(textView, stringBuilder.toString());
    }

    /**
     * 给 TextView 设置文本的同时设置后缀 span
     * 内容仅仅支持文本
     *
     * @param text       原始文本内容
     * @param textView   TextView
     * @param iconUrl    icon url
     * @param heightPx   icon 高度
     * @param iconRatio  icon 宽高比例
     * @param lefPadding icon 离左侧的距离 - 如果 icon 作为了行的开头，则该padding不生效
     * @Description:
     * @DateTime 2023-08-02, 周三, 10:45
     *
     */
    public static void setTextWidthSuffixIcon(@NonNull String text, TextView textView, String iconUrl, float heightPx, float iconRatio, int lefPadding) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder(text);
        int widthPx = (int) (heightPx * iconRatio);
        int wrapperWidth = widthPx + lefPadding + (int) measureTextWidth("...", textView);
        spannableString.setSpan(new WhiteSpaceSpan(wrapperWidth), 0, 1, spannableString.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(spannableString);
        textView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Layout layout = textView.getLayout();
                if (layout != null) {
                    CharSequence charSequence = layout.getText();
                    HtmlTagCtrlFactory.getInstance().setText(textView, charSequence.toString() + HYSuffixTag.build(iconUrl).setWidth(widthPx).setLeftPad(lefPadding).setHeight(heightPx).buildSafeTag());
                }
                textView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    /**
     * 给 TextView 设置文本的同时设置后缀 span
     * 内容仅仅支持文本
     *
     * @param text       原始文本内容
     * @param textView   TextView
     * @param iconUrl    icon url
     * @param iconRatio  icon 宽高比例
     * @param lefPadding icon 离左侧的距离 - 如果 icon 作为了行的开头，则该padding不生效
     * @Description:
     * @DateTime 2023-08-02, 周三, 10:45
     *
     */
    public static void setTextWidthSuffixIcon(@NonNull String text, TextView textView, String iconUrl, float iconRatio, int lefPadding) {
        Paint.FontMetrics fontMetrics = textView.getPaint().getFontMetrics();
        float heightPx = Math.abs(fontMetrics.ascent);
        setTextWidthSuffixIcon(text, textView, iconUrl, heightPx, iconRatio, lefPadding);
    }

    /**
     * 给 TextView 设置文本的同时设置后缀 span
     * 内容仅仅支持文本
     *
     * @param text      原始文本内容
     * @param textView  TextView
     * @param iconUrl   icon url
     * @param iconRatio icon 宽高比例
     * @Description:
     * @DateTime 2023-08-02, 周三, 10:45
     *
     */
    public static void setTextWidthSuffixIcon(@NonNull String text, TextView textView, String iconUrl, float iconRatio) {
        setTextWidthSuffixIcon(text, textView, iconUrl, iconRatio, 0);
    }

    /**
     * 测量文本的宽度
     *
     * @param text     文本内容
     * @param textView TextView
     * @Description:
     * @DateTime 2023-08-02, 周三, 10:45
     *
     */
    public static float measureTextWidth(String text, TextView textView) {
        return measureTextWidth(text, textView.getPaint());
    }

    /**
     * 测量文本的宽度
     *
     * @param text      文本内容
     * @param textPaint TextView的 textPaint
     * @Description:
     * @DateTime 2023-08-02, 周三, 10:45
     *
     */
    public static float measureTextWidth(String text, TextPaint textPaint) {

        return StaticLayout.getDesiredWidth(text, textPaint);
    }

    /**
     * @param text      文本内容
     * @param startIcon 图片链接
     * @param textView  TextView
     * @Description: 设置带有 icon 的文本，支持在开始位置添加 icon，并根据icon的高度自适应宽度
     * @DateTime 2023-05-16, 周二, 20:12
     *
     */
    public static void setTextWithStartIcon(@Nullable String text, @Nullable String startIcon, TextView textView) {
        setTextWithStartIcon(text, textView, startIcon);
    }

    /**
     * @param text           文本内容
     * @param startIconArray 图片链接
     * @param textView       TextView
     * @Description: 设置带有 icon 的文本，支持在开始位置添加 icon，并根据icon的高度自适应宽度
     * @DateTime 2023-05-16, 周二, 20:12
     *
     */
    public static void setTextWithStartIcon(@Nullable String text, TextView textView, @Nullable String... startIconArray) {
        if (textView == null) {
            return;
        }
        textView.setIncludeFontPadding(false); // 设置 includeFont 边距，否则会导致有细微的偏移，字体越大越明显
        List<String> list = new ArrayList<>();
        if (startIconArray != null && startIconArray.length > 0) {
            Paint.FontMetrics fontMetrics = textView.getPaint().getFontMetrics();
            float heightPx = Math.abs(fontMetrics.ascent);
            for (String item : startIconArray) {
                if (!TextUtils.isEmpty(item)) {
                    list.add(HYImageTag.build(item).setRightPad(2).setHeightPx(heightPx).buildTag());
                }
            }
        }
        setTextWithPrefix(text, textView, list.toArray(new String[0]));
    }

    /**
     * @param text           文本内容
     * @param startIconArray 图片链接
     * @param textView       TextView
     * @Description: 设置带有 icon 的文本，支持在开始位置添加 icon，并根据icon的高度自适应宽度
     * @DateTime 2023-05-16, 周二, 20:12
     *
     */
    public static void setPostTitle(@Nullable String text, TextView textView, @Nullable String... startIconArray) {
        if (textView == null) {
            return;
        }
        textView.setIncludeFontPadding(false); // 设置 includeFont 边距，否则会导致有细微的偏移，字体越大越明显
        List<String> list = new ArrayList<>();
        if (startIconArray != null && startIconArray.length > 0) {
            Paint.FontMetrics fontMetrics = textView.getPaint().getFontMetrics();
            float heightPx = Math.abs(fontMetrics.ascent-fontMetrics.descent);
            for (String item : startIconArray) {
                if (!TextUtils.isEmpty(item)) {
                    list.add(HYImageTag.build(item).setRightPad(2).setHeightPx(heightPx).buildTag());
                }
            }
        }
        setTextWithPrefix(text, textView, list.toArray(new String[0]));
    }

}
