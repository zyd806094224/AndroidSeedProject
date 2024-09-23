package com.demo.common.text.html.tag;

public class MarginLeftTag {
    public static final String NAME = "ml";
    private int space = 8;


    private MarginLeftTag() {
    }

    public static MarginLeftTag build() {
        return new MarginLeftTag();
    }


    public int getSpace() {
        return space;
    }

    public MarginLeftTag setSpace(int space) {
        this.space = space;
        return this;
    }

    public String buildTag() {
        if (space <= 0) {
            return "";
        }
        return "<" + NAME + ">" +
                this.space +
                "</" + NAME + ">";
    }
}
