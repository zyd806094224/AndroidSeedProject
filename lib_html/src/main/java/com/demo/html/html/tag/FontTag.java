package com.demo.html.html.tag;

import android.text.TextUtils;

/**
 * font  标签
 * 目前在Android源码中只支持 color 和 face 属性
 */
public class FontTag {
    public static final String NAME = "font";

    private String text;
    private String color;
    private String face;

    private FontTag(String text) {
        this.text = text;
    }

    public static FontTag build(String text) {
        return new FontTag(text);
    }


    public String getText() {
        return text;
    }

    public FontTag setText(String text) {
        this.text = text;
        return this;
    }

    public String getColor() {
        return color;
    }

    public FontTag setColor(String color) {
        this.color = color;
        return this;
    }

    public String getFace() {
        return face;
    }

    public FontTag setFace(String face) {
        this.face = face;
        return this;
    }

    public String buildTag() {
        if (TextUtils.isEmpty(this.text)) {
            return "";
        }
        StringBuilder resultSb = new StringBuilder();
        resultSb.append("<").append(NAME);
        if (!TextUtils.isEmpty(color)) {
            resultSb.append(" color").append("='").append(color).append("'");
        }
        if (!TextUtils.isEmpty(face)) {
            resultSb.append(" face").append("='").append(face).append("'");
        }
        resultSb.append(">");

        resultSb.append(text);

        resultSb.append("</").append(NAME).append(">");
        return resultSb.toString();
    }
}
