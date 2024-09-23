package com.demo.html.html;

import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.demo.html.html.ctrl.DelTagHandler;
import com.demo.html.html.ctrl.base.AbstractHtmlTagHandler;
import com.demo.html.html.listener.OnSpanSetListener;
import com.demo.html.html.listener.OnTagLoadListener;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HtmlTagCtrlFactory implements Serializable {

    private HashMap<String, Class<? extends AbstractHtmlTagHandler>> tagHandler = new HashMap<>();

    public <T extends AbstractHtmlTagHandler> void addHandlerCtrl(String tag, Class<T> tClass) {
        tagHandler.put(tag, tClass);
    }

    // 防止反射创建
    private HtmlTagCtrlFactory() {
        if (getInstance() != null) {
            throw new RuntimeException();
        }
        this.addHandlerCtrl(DelTagHandler.TAG, DelTagHandler.class);
    }


    public HtmlTagHandler getTagHandler(TextView textView) {
        return new HtmlTagHandler(textView);
    }

    public void setText(TextView textView, String charSequence) {
        this.setText(textView, charSequence, null);
    }

    public void setText(TextView textView, String charSequence, OnSpanSetListener spanSetListener) {
        HtmlTagHandler htmlTagHandler = getTagHandler(textView);
        htmlTagHandler.setOnTagListener(new OnTagLoadListener() {
            @Override
            public void refreshView() {
                textView.setText(Html.fromHtml(charSequence, null, htmlTagHandler));
                callSpanListener(spanSetListener, textView);
            }
        });
        textView.setText(Html.fromHtml(charSequence, null, htmlTagHandler));
        callSpanListener(spanSetListener, textView);
    }

    private void callSpanListener(OnSpanSetListener spanSetListener, TextView textView){
        if(spanSetListener != null && textView.getText() instanceof Spanned) {
            spanSetListener.onSpanSet((Spanned) textView.getText());
        }
    }

    public List<AbstractHtmlTagHandler> buildTagHandlers(TextView textView) {
        ArrayList<AbstractHtmlTagHandler> tagHandlers = new ArrayList<>();
        Set<String> keySet = tagHandler.keySet();
        try {
            for (String tag : keySet) {
                Class<? extends AbstractHtmlTagHandler> tagHandleCtrl = tagHandler.get(tag);
                if (tagHandleCtrl != null) {
                    try {
                        Constructor<? extends AbstractHtmlTagHandler> declaredConstructor = tagHandleCtrl.getDeclaredConstructor(TextView.class);
                        tagHandlers.add(declaredConstructor.newInstance(textView));
                    } catch (NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return tagHandlers;
    }

    public static class SingletonHolder {
        private static final HtmlTagCtrlFactory INSTANCE = new HtmlTagCtrlFactory();

    }

    public static HtmlTagCtrlFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // 防止序列化
    private Object readResolve() {
        return SingletonHolder.INSTANCE;
    }


}
