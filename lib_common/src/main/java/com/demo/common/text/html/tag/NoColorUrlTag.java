package com.demo.common.text.html.tag;

public class NoColorUrlTag {

    /**
     * 解析富文本终  <hy_a href='xxxx'></hy_a> 的元素，支持自定义点击
     * HtmlTagCtrlFactory.setText的时候最好增加OnSpanSetListener，因为span可能会刷新
     */

    public static final String NAME = "hy_a";

    private String href;

    private String text;

    public NoColorUrlTag(String href, String text) {
        this.href = href;
        this.text = text;
    }


    public String buildTag() {
        return "<" + NAME + " " +
                "href='" + href + "' " +
                ">" + text +
                "</" + NAME + ">";
    }

}
