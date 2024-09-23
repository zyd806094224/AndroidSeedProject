package com.demo.html.html.tag;

import android.text.TextUtils;

public class DelHtmlTag {
    public static final String NAME = "del";
    private String text;

    private DelHtmlTag(String text) {
        this.text = text;
    }

    public static DelHtmlTag build(String text) {
        return new DelHtmlTag(text);
    }

    public String buildTag() {
        if (TextUtils.isEmpty(this.text)) {
            return "";
        }
        return "<" + NAME + ">" +
                this.text +
                "</" + NAME + ">";
    }

}
