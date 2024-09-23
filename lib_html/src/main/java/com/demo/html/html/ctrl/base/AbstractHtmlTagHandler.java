package com.demo.html.html.ctrl.base;

import android.graphics.Color;
import android.text.Spanned;
import android.widget.TextView;

import com.demo.html.html.listener.OnTagLoadListener;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractHtmlTagHandler implements IHtmlTagHandler {
    protected Map<String, String> attributes = new HashMap<>();

    protected TextView textView;

    protected OnTagLoadListener onTagLoadListener;


    public AbstractHtmlTagHandler(TextView textView) {
        this.textView = textView;
    }


    public void setOnTagListener(OnTagLoadListener onTagLoadListener) {
        this.onTagLoadListener = onTagLoadListener;
    }

    public OnTagLoadListener getOnTagListener() {
        return onTagLoadListener;
    }

    protected Map<String, String> getAttributes(XMLReader xmlReader) {
        HashMap<String, String> attributes = new HashMap<>();
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[]) dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = (Integer) lengthField.get(atts);
            for (int i = 0; i < len; i++) {
                attributes.put(data[i * 5 + 1], data[i * 5 + 4]);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attributes;
    }

    /**
     * 利用反射获取html标签的属性值
     *
     * @param xmlReader
     * @param property
     * @return
     */
    protected String getAttribute(XMLReader xmlReader, String property) {
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[]) dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = (Integer) lengthField.get(atts);

            for (int i = 0; i < len; i++) {
                // 这边的property换成你自己的属性名就可以了
                if (property.equals(data[i * 5 + 1])) {
                    return data[i * 5 + 4];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected <T> T getLast(Spanned text, Class<T> kind) {
        T[] objs = text.getSpans(0, text.length(), kind);

        if (objs.length == 0) {
            return null;
        } else {
            return objs[objs.length - 1];
        }
    }

    protected int getDipAttr(String attrName) {
        return dip2px(getFloatAttr(attrName));
    }

    private int dip2px(float dpValue) {
        float density = this.textView.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    protected int getIntAttr(String attrName) {
        try {
            String res = getAttr(attrName);
            return Integer.valueOf(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    protected float getFloatAttr(String attrName) {
        try {
            String res = getAttr(attrName);
            return Float.valueOf(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0f;
    }

    protected int getColorAttr(String attrName) {
        try {
            String res = getAttr(attrName);
            return Color.parseColor(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Color.TRANSPARENT;
    }

    protected boolean getBooleanAttr(String attrName) {
        try {
            String res = getAttr(attrName);
            return Boolean.valueOf(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected String getAttr(String name) {
        return attributes.get(name);
    }
}
