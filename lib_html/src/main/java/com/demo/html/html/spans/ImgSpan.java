package com.demo.html.html.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ImgSpan extends ImageSpan {
    private int offset;
    private int leftPad, rightPad;

    public ImgSpan(@NonNull Drawable drawable, int v) {
        super(drawable, v);
    }

    public ImgSpan(@NonNull Drawable drawable) {
        super(drawable);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLeftPad() {
        return leftPad;
    }

    public void setLeftPad(int leftPad) {
        this.leftPad = leftPad;
    }

    public int getRightPad() {
        return rightPad;
    }

    public void setRightPad(int rightPad) {
        this.rightPad = rightPad;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return super.getSize(paint, text, start, end, fm) + leftPad + rightPad;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text,
                     @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x,
                     int top, int y, int bottom, @NonNull Paint paint) {
        Drawable b = getDrawable();
        canvas.save();
        int transY = y + paint.getFontMetricsInt().ascent + offset;
        canvas.translate(x + leftPad, transY);
        if (b instanceof BitmapDrawable && ((BitmapDrawable) b).getBitmap() != null && !((BitmapDrawable) b).getBitmap().isRecycled())
            b.draw(canvas);
        canvas.restore();
    }
}
