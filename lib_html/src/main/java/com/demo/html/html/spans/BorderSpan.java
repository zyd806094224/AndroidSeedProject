package com.demo.html.html.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;
import android.util.Log;

import com.demo.html.html.style.Style;
import com.demo.html.html.style.StyleValue;
import com.demo.html.html.utils.HtmlSpanner;


/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 6/23/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class BorderSpan implements LineBackgroundSpan {

    private int start;
    private int end;

    private Style style;

    private HtmlSpanner spanner;

    public BorderSpan(Style style, int start, int end, HtmlSpanner spanner ) {
        this.start = start;
        this.end = end;

        this.style = style;
        this.spanner = spanner;
    }


    @Override
    public void drawBackground(Canvas c, Paint p,
                               int left, int right,
                               int top, int baseline, int bottom,
                               CharSequence text, int start, int end,
                               int lnum) {

        int baseMargin = 0;

        if ( style.getMarginLeft() != null ) {
            StyleValue styleValue = style.getMarginLeft();

            if ( styleValue.getUnit() == StyleValue.Unit.PX ) {
                if ( styleValue.getIntValue() > 0 ) {
                    baseMargin = styleValue.getIntValue();
                }
            } else if ( styleValue.getFloatValue() > 0f ) {
                baseMargin = (int) (styleValue.getFloatValue() * HtmlSpanner.HORIZONTAL_EM_WIDTH);
            }

            //Leave a little bit of room
            baseMargin--;
        }

        if ( baseMargin > 0 ) {
            left = left + baseMargin;
        }

        int originalColor = p.getColor();
        float originalStrokeWidth = p.getStrokeWidth();

        Integer backgroundColor = spanner.getContrastPatcher().patchBackgroundColor(style);
        if ( spanner.isUseColoursFromStyle() && backgroundColor != null ) {
            p.setColor(backgroundColor);
            p.setStyle(Paint.Style.FILL);

            c.drawRect(left,top,right,bottom,p);
        }

        if ( spanner.isUseColoursFromStyle() && style.getBorderColor() != null ) {
            p.setColor( style.getBorderColor() );
        }

        int strokeWidth;

        if ( style.getBorderWidth() != null && style.getBorderWidth().getUnit() == StyleValue.Unit.PX ) {
            strokeWidth = style.getBorderWidth().getIntValue();
        } else {
            strokeWidth = 1;
        }

        p.setStrokeWidth( strokeWidth );
        right -= strokeWidth;

        p.setStyle(Paint.Style.STROKE);

        if ( start <= this.start ) {
            Log.d("BorderSpan", "Drawing first line");
            c.drawLine(left, top, right, top, p);
        }

        if ( end >= this.end ) {
            Log.d("BorderSpan", "Drawing last line");
            c.drawLine(left, bottom, right, bottom, p);
        }

        c.drawLine(left,top,left,bottom, p);
        c.drawLine(right,top,right,bottom, p);


        p.setColor(originalColor);
        p.setStrokeWidth(originalStrokeWidth);
    }


}
