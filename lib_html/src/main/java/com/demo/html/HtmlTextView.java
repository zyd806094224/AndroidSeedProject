package com.demo.html;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.demo.html.html.HtmlTagCtrlFactory;


/**
 * 富文本的 TextView
 */
public class HtmlTextView extends AppCompatTextView {
    private boolean supportHtml;


    public HtmlTextView(Context context) {
        this(context, null);
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.loadFromAttributes(context, attrs);
    }


    private void loadFromAttributes(Context context, AttributeSet attrs) {
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HtmlTextView, 0, 0);
        this.supportHtml = array.getBoolean(R.styleable.HtmlTextView_supportHtml, true);
        array.recycle();

    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (supportHtml) {
            if (text instanceof String) {
                HtmlTagCtrlFactory.getInstance().setText(this, (String) text);
                return;
            }
        }
        super.setText(text, type);
    }

    public boolean isSupportHtml() {
        return supportHtml;
    }

    public void setSupportHtml(boolean supportHtml) {
        this.supportHtml = supportHtml;
    }
}