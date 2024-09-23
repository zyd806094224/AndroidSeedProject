package com.demo.html.html;

import android.text.Editable;
import android.text.Html;
import android.widget.TextView;

import com.demo.html.html.HtmlTagCtrlFactory;
import com.demo.html.html.ctrl.base.AbstractHtmlTagHandler;
import com.demo.html.html.ctrl.base.IHtmlTagHandler;
import com.demo.html.html.listener.OnTagLoadListener;

import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.List;

public class HtmlTagHandler implements Html.TagHandler {

    private List<AbstractHtmlTagHandler> tagHandlers = new ArrayList<>();


    public HtmlTagHandler(TextView textView) {
        this.tagHandlers = HtmlTagCtrlFactory.getInstance().buildTagHandlers(textView);
    }


    protected OnTagLoadListener onTagLoadListener;

    public void setOnTagListener(OnTagLoadListener onTagLoadListener) {
        this.onTagLoadListener = onTagLoadListener;
        for (AbstractHtmlTagHandler tagHandler : tagHandlers) {
            tagHandler.setOnTagListener(onTagLoadListener);
        }
    }

    public OnTagLoadListener getOnTagListener() {
        return onTagLoadListener;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        IHtmlTagHandler tagHandler = null;
        for (int i = 0; i < this.tagHandlers.size(); i++) {
            if (tagHandlers.get(i).processAble(tag)) {
                tagHandler = tagHandlers.get(i);
                break;
            }
        }
        if (tagHandler != null) {
            if (opening) {
                tagHandler.handleStartTag(tag, output, xmlReader);
            } else {
                tagHandler.handleEndTag(tag, output, xmlReader);
            }
        }
    }
}
