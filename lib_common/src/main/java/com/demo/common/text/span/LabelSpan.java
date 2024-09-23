package com.demo.common.text.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.text.style.ReplacementSpan;

public class LabelSpan extends ReplacementSpan {
    private int mBgColor;
    private int mRadius, leftTopRadius, rightTopRadius, leftBottomRadius, rightBottomRadius;
    private int mTextColor;
    private int mTextSize;
    private int mPaddingHorizontal;
    private String mText;
    private int mMarginLeft;
    private int mMarginRight;
    private int offsetTop;

    private GradientDrawable drawable;

    public LabelSpan() {
        drawable = new GradientDrawable();
    }

    public LabelSpan(int bgColor, int radius, int textColor, int textSize, int paddingHorizontal, String text) {
        mBgColor = bgColor;
        mRadius = radius;
        mTextColor = textColor;
        mTextSize = textSize;
        mPaddingHorizontal = paddingHorizontal;
        mText = text;
        drawable = new GradientDrawable();
        drawable.setColor(mBgColor);
        setRadius();
    }

    public void setBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
        drawable.setColor(mBgColor);
    }

    public void setRadius(int mRadius) {
        this.mRadius = mRadius;
        setRadius();
    }

    public void setOffsetTop(int offsetTop) {
        this.offsetTop = offsetTop;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }

    public void setPaddingHorizontal(int mPaddingHorizontal) {
        this.mPaddingHorizontal = mPaddingHorizontal;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    private void setRadius() {
        if (mRadius == 0) drawable.setCornerRadius(0);
        if (leftTopRadius != 0 || rightTopRadius != 0 || leftBottomRadius != 0 || rightBottomRadius != 0) {
            drawable.setCornerRadii(new float[]{leftTopRadius, leftTopRadius,
                    rightTopRadius, rightTopRadius,
                    rightBottomRadius, rightBottomRadius,
                    leftBottomRadius, leftBottomRadius
            });
        }

    }

    public void setRadius(int leftTop, int rightTop, int leftBottom, int RightBottom) {
        leftTopRadius = leftTop;
        rightTopRadius = rightTop;
        leftBottomRadius = leftBottom;
        rightBottomRadius = RightBottom;
        setRadius();
    }

    public void setMarginLeft(int mMarginLeft) {
        this.mMarginLeft = mMarginLeft;
    }

    public void setMarginRight(int mMarginRight) {
        this.mMarginRight = mMarginRight;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        paint.setTextSize(mTextSize);
        return (int) paint.measureText(mText) + 2 * mPaddingHorizontal + mMarginLeft + mMarginRight;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        paint.setTextSize(mTextSize);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(false);
        RectF rect = new RectF();
        rect.left = (int) x + mMarginLeft;
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int marginVertical = (bottom - top - fontMetrics.bottom + fontMetrics.top) / 2;
        rect.top = y + fontMetrics.top - offsetTop; //视觉感觉偏下了，往上一点点
        rect.bottom = y + fontMetrics.bottom - offsetTop;
        rect.right = rect.left + (int) paint.measureText(mText) + 2 * mPaddingHorizontal;

        drawable.setBounds((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
        paint.setColor(mBgColor);
        //canvas.drawRoundRect(rect, mRadius, mRadius, paint);
        drawable.draw(canvas);

        paint.setColor(mTextColor);
        canvas.drawText(mText, x + mPaddingHorizontal + mMarginLeft, y - offsetTop, paint);
    }
}