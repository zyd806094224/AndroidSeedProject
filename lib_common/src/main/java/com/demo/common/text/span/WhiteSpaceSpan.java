package com.demo.common.text.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Description: 占据空白空间的 占位 span
 * @Date: 2023/8/2 10:25 AM
 * 
 * @version: 1.0
 */
public class WhiteSpaceSpan extends ReplacementSpan {
    int width;
    public WhiteSpaceSpan(int width) {
        this.width = width;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return width;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {

    }
}
