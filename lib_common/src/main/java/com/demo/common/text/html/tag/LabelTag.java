package com.demo.common.text.html.tag;

public class LabelTag {

    public static final String NAME = "label";

    private String text;
    private String background;
    private float padding;
    private float radius;
    private float left_top;
    private float right_top;
    private float left_bottom;
    private float right_bottom;
    private float left_margin;
    private float right_margin;
    private String color;
    private float size;

    private LabelTag(String text) {
        this.text = text;
    }

    public static LabelTag build(String text) {
        return new LabelTag(text);
    }

    public String getBackground() {
        return background;
    }

    public LabelTag setBackground(String background) {
        this.background = background;
        return this;
    }

    public float getPadding() {
        return padding;
    }

    public LabelTag setPadding(float padding) {
        this.padding = padding;
        return this;
    }

    public float getRadius() {
        return radius;
    }

    public LabelTag setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public float getLeft_top() {
        return left_top;
    }

    public LabelTag setLeft_top(float left_top) {
        this.left_top = left_top;
        return this;
    }

    public float getRight_top() {
        return right_top;
    }

    public LabelTag setRight_top(float right_top) {
        this.right_top = right_top;
        return this;
    }

    public float getLeft_bottom() {
        return left_bottom;
    }

    public LabelTag setLeft_bottom(float left_bottom) {
        this.left_bottom = left_bottom;
        return this;
    }

    public float getRight_bottom() {
        return right_bottom;
    }

    public LabelTag setRight_bottom(float right_bottom) {
        this.right_bottom = right_bottom;
        return this;
    }

    public float getLeft_margin() {
        return left_margin;
    }

    public LabelTag setLeft_margin(float left_margin) {
        this.left_margin = left_margin;
        return this;
    }

    public float getRight_margin() {
        return right_margin;
    }

    public LabelTag setRight_margin(float right_margin) {
        this.right_margin = right_margin;
        return this;
    }

    public String getColor() {
        return color;
    }

    public LabelTag setColor(String color) {
        this.color = color;
        return this;
    }

    public float getSize() {
        return size;
    }

    public LabelTag setSize(float size) {
        this.size = size;
        return this;
    }
}
