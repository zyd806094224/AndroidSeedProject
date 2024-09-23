package com.demo.html.html.ctrl.base;

import android.text.Editable;

import org.xml.sax.XMLReader;

public interface IHtmlTagHandler {


    boolean processAble(String tag);

    void handleStartTag(String tag, Editable output, XMLReader xmlReader);

    void handleEndTag(String tag, Editable output, XMLReader xmlReader);
}
