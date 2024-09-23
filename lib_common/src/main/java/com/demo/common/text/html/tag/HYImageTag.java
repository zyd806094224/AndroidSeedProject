package com.demo.common.text.html.tag;

import android.text.TextUtils;

public class HYImageTag {
    public static final String NAME = "hy_image";
    private int offset = 0;
    private int leftPad = 0;
    private int rightPad = 0;
    private int height = 12;
    private int width = 0;
    private float heightPx = 0;
    private float widthPx = 0;
    private String src;

    private HYImageTag(String src) {
        this.src = src;
    }

    public static HYImageTag build(String src) {
        return new HYImageTag(src);
    }


    public int getOffset() {
        return offset;
    }

    public HYImageTag setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public int getLeftPad() {
        return leftPad;
    }

    public HYImageTag setLeftPad(int leftPad) {
        this.leftPad = leftPad;
        return this;
    }

    public int getRightPad() {
        return rightPad;
    }

    public HYImageTag setRightPad(int rightPad) {
        this.rightPad = rightPad;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public HYImageTag setHeight(int height) {
        this.height = height;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public HYImageTag setWidth(int width) {
        this.width = width;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public HYImageTag setSrc(String src) {
        this.src = src;
        return this;
    }

    public float getHeightPx() {
        return heightPx;
    }

    public HYImageTag setHeightPx(float heightPx) {
        this.heightPx = heightPx;
        return this;
    }

    public float getWidthPx() {
        return widthPx;
    }

    public HYImageTag setWidthPx(int widthPx) {
        this.widthPx = widthPx;
        return this;
    }

    public String buildTag() {
        return "<" + NAME + " " +
                "src='" + src + "' " +
                "height=" + height + " " +
                "width=" + width + " " +
                "right_pad=" + rightPad + " " +
                "left_pad=" + leftPad + " " +
                "offset=" + offset + " " +
                "height_px=" + heightPx + " " +
                "width_px=" + widthPx + " " +
                "/>";
    }

    public String buildSafeTag() {
        if (TextUtils.isEmpty(src)) {
            return "";
        }
        return buildTag();
    }
}
