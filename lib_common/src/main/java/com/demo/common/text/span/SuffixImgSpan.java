package com.demo.common.text.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Description:
 * @Date: 2023/8/2 11:49 AM
 * 
 * @version: 1.0
 */
public class SuffixImgSpan extends ImageSpan {

    private int leftPad;
    private int mWidth;
    private int mHeight;
    private Rect mBounds;
    private Drawable mDrawable;
    private int mAlignment;
    private final Paint.FontMetricsInt mFontMetricsInt = new Paint.FontMetricsInt();


    public SuffixImgSpan(@NonNull Drawable drawable, @ImageSpanAlignment int verticalAlignment) {
        super(drawable, verticalAlignment == ALIGN_CENTER ? DynamicDrawableSpan.ALIGN_BASELINE : verticalAlignment);
        this.mAlignment = verticalAlignment;
        this.mDrawable = drawable;
        this.updateBounds();
    }

    public SuffixImgSpan(@NonNull Drawable drawable) {
        this(drawable, ImgSpan.ALIGN_CENTER);
    }

    @ImageSpanAlignment
    public static int normalizeAlignment(int alignment) {
        switch (alignment) {
            case DynamicDrawableSpan.ALIGN_BOTTOM:
                return ImgSpan.ALIGN_BOTTOM;
            case ALIGN_CENTER:
                return ImgSpan.ALIGN_CENTER;
            case DynamicDrawableSpan.ALIGN_BASELINE:
            default:
                return ImgSpan.ALIGN_BASELINE;
        }
    }

    public void updateBounds() {
        if (mDrawable == null) {
            return;
        }
        mBounds = this.mDrawable.getBounds();
        mWidth = mBounds.width();
        mHeight = mBounds.height();
    }


    public int getLeftPad() {
        return leftPad;
    }

    public void setLeftPad(int leftPad) {
        this.leftPad = leftPad;
    }


    private int getOffsetAboveBaseline(Paint.FontMetricsInt fm) {
        switch (mAlignment) {
            case ALIGN_BOTTOM:
                return fm.descent - mHeight;
            case ALIGN_CENTER:
                int textHeight = fm.descent - fm.ascent - fm.leading;
                int offset = (textHeight - mHeight) / 2;
                return fm.ascent + offset - (fm.ascent - fm.top - (fm.bottom - fm.descent)) / 2;
            case ALIGN_BASELINE:
            default:
                return -mHeight;
        }
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fontMetrics) {
        int width = super.getSize(paint, text, start, end, fontMetrics) + leftPad;
        this.updateBounds();
        if (fontMetrics != null) {
            int offsetAbove = getOffsetAboveBaseline(fontMetrics);
            int offsetBelow = mHeight + offsetAbove;
            if (offsetAbove < fontMetrics.ascent) {
                fontMetrics.ascent = offsetAbove;
            }

            if (offsetAbove < fontMetrics.top) {
                fontMetrics.top = offsetAbove;
            }

            if (offsetBelow > fontMetrics.descent) {
                fontMetrics.descent = offsetBelow;
            }

            if (offsetBelow > fontMetrics.bottom) {
                fontMetrics.bottom = offsetBelow;
            }
        }
        return width;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text,
                     @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x,
                     int top, int y, int bottom, @NonNull Paint paint) {
        paint.getFontMetricsInt(mFontMetricsInt);
        int iconTop = y + getOffsetAboveBaseline(paint.getFontMetricsInt());
        int iconBottom = iconTop + mHeight;
        float translateX = x;
        if (x > 0f) {
            translateX = x + this.leftPad;
        }
        if (iconBottom > bottom) { //高度越界了，则需要调整
            iconTop = Math.max(0, iconTop - (iconBottom - bottom));
        }
        float translateY = iconTop;
        canvas.save();
        canvas.translate(translateX, translateY);
        mDrawable.draw(canvas);
        canvas.restore();
    }
}
