package com.demo.common.text.html.tag;

import android.text.TextUtils;

public class FontSizeTag {
    public static final String NAME = "size";
    private int size = 24;
    private String text;

    private FontSizeTag() {
    }

    public static FontSizeTag build(int size, String text) {
        FontSizeTag tag = new FontSizeTag();
        tag.setSize(size);
        tag.setText(text);
        return tag;
    }

    public static FontSizeTag build(String text) {
        FontSizeTag tag = new FontSizeTag();
        tag.setText(text);
        return tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    private String buildStartTag() {
        return "<" + NAME +
                " value=" + this.size +
                ">";
    }

    public String buildTag() {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        return this.buildStartTag() +
                this.text +
                "</" + NAME + ">";
    }
}
