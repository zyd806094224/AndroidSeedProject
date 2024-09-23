package com.demo.common.text.html.tag;

import android.text.TextUtils;

public class HYSuffixTag {
    public static final String NAME = "hy_suffix";

    private int leftPad = 0;

    private float height = 0;
    private float width = 0;
    private String src;

    private HYSuffixTag(String src) {
        this.src = src;
    }

    public static HYSuffixTag build(String src) {
        return new HYSuffixTag(src);
    }


    public int getLeftPad() {
        return leftPad;
    }

    public HYSuffixTag setLeftPad(int leftPad) {
        this.leftPad = leftPad;
        return this;
    }


    public float getHeight() {
        return height;
    }

    public HYSuffixTag setHeight(float height) {
        this.height = height;
        return this;
    }

    public float getWidth() {
        return width;
    }

    public HYSuffixTag setWidth(float width) {
        this.width = width;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public HYSuffixTag setSrc(String src) {
        this.src = src;
        return this;
    }


    public String buildTag() {
        return "<" + NAME + " " +
                "src='" + src + "' " +
                "height=" + height + " " +
                "width=" + width + " " +
                "left_pad=" + leftPad + " " +
                "/>";
    }

    public String buildSafeTag() {
        if (TextUtils.isEmpty(src)) {
            return "";
        }
        return buildTag();
    }
}
