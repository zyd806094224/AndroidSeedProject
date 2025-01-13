package com.demo.main.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.style.ReplacementSpan

class NonBreakingSpan(private val textColor: Int = Color.BLACK) : ReplacementSpan() {

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        if (fm != null) {
            // 保持字体的原始行高一致
            val paintFm = paint.fontMetricsInt
            fm.ascent = paintFm.ascent
            fm.descent = paintFm.descent
            fm.top = paintFm.top
            fm.bottom = paintFm.bottom
        }
        // 测量文字宽度
        return paint.measureText(text, start, end).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        // 保存原始 Paint 的颜色
        val originalColor = paint.color

        // 设置文字颜色
        paint.color = textColor

//        val baselineOffset = (paint.fontMetricsInt.ascent + paint.fontMetricsInt.descent) / 2f
//        val adjustedY = y - baselineOffset

//        val adjustedY = y.toFloat() - paint.fontMetricsInt.descent + 6

//        val adjustedY = bottom.toFloat() - (paint.fontMetricsInt.descent - paint.fontMetricsInt.ascent)/2

//        val fm = paint.fontMetricsInt
//        val adjustedY = (fm.descent - fm.ascent) + (y - (fm.descent - fm.ascent)) / 2f

        val adjustedY = y.toFloat() -2
        // 绘制文字
        canvas.drawText(text, start, end, x, adjustedY, paint)

        // 恢复原始颜色
        paint.color = originalColor
    }
}


