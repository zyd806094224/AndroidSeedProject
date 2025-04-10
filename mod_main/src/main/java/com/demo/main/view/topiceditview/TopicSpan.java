package com.demo.main.view.topiceditview;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * @Description:
 * @Date: 2025/3/17 14:50
 * @author: zhaoyudong
 */
public class TopicSpan<T> extends ClickableSpan {

    int defColor = Color.parseColor("#00F1F5");
    int color = defColor;
    float textSize = -1;
    boolean underline = false;
    boolean bolder = false;
    T data;
    Callback<T> callback;


    public TopicSpan(int color, float textSize, boolean underline, boolean bolder, T data, Callback<T> callback) {
        this.color = color == -1 ? defColor : color;
        this.textSize = textSize;
        this.underline = underline;
        this.bolder = bolder;
        this.data = data;
        this.callback = callback;
    }

    @Override
    public void onClick(View widget) {
        if (callback != null) {
            callback.onClick(data);
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        ds.setColor(color);
        if (textSize > -1) {
            ds.setTextSize(textSize);
        }
        ds.setFakeBoldText(bolder);
        ds.setUnderlineText(underline);
        ds.clearShadowLayer();
    }

    public static class Builder<T> {
        int color = Color.parseColor("#1fabf3");
        float textSize = -1;
        boolean underline = false;
        boolean bolder = false;
        T data;
        Callback<T> callback;

        public Builder(int color) {
            this.color = color;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder textSize(float textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder underline(boolean underline) {
            this.underline = underline;
            return this;
        }

        public Builder data(T data) {
            this.data = data;
            return this;
        }

        public Builder bolder(boolean bolder) {
            this.bolder = bolder;
            return this;
        }

        public Builder callback(Callback<T> callback) {
            this.callback = callback;
            return this;
        }

        public TopicSpan build() {
            return new TopicSpan(color, textSize, underline, bolder, data, callback);
        }

    }

    public interface Callback<T> {
        void onClick(T data);
    }

    public static <T> SpannableString getSpan(int color, String displayText, T data, Callback<T> callback) {
        SpannableString span = new SpannableString(displayText);
        TopicSpan tag = new Builder<T>(color)
                .data(data)
                .callback(callback).build();
        span.setSpan(tag, 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    public static <T> SpannableString getSpan(int color, float textSize, String displayText, boolean bolder, T data, Callback<T> callback) {
        SpannableString span = new SpannableString(displayText);
        TopicSpan tag = new Builder<T>(color)
                .data(data)
                .bolder(bolder)
                .textSize(textSize)
                .callback(callback).build();
        span.setSpan(tag, 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

}
