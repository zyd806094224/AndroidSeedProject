package com.demo.main.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.demo.main.R
import com.demo.main.widget.VerticalScrollTextView.OnItemClickListener

/**
 * 自定义搜索框
 */
class CustomSearchBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var iv_left: ImageView? = null
    var tv_right: TextView? = null
    var edittext: AppCompatEditText? = null
    var tv_vertical_scroll: VerticalScrollTextView? = null
    var root: ConstraintLayout? = null

    init {
        View.inflate(context, R.layout.view_custom_search_bar, this)
        iv_left = findViewById(R.id.iv_left)
        tv_right = findViewById(R.id.tv_right)
        edittext = findViewById(R.id.edittext)
        tv_vertical_scroll = findViewById(R.id.tv_vertical_scroll)
        root = findViewById(R.id.root)
    }

    fun setBackgroundStyle(
        @ColorInt color: Int,
        radius: Float? = 0f
    ) {
        // 创建 GradientDrawable 并设置属性
        val backgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            if (radius != null) {
                cornerRadius = radius
            } // 设置圆角半径（单位：像素）
            setColor(color) // 设置背景色
        }
        root?.background = backgroundDrawable
    }

    /**
     * 设置数据源
     */
    fun setTextList(titles: List<String>) {
        tv_vertical_scroll?.setTextList(titles)
    }

    /**
     * 开始滚动
     */
    fun startAutoScroll() {
        tv_vertical_scroll?.startAutoScroll()
    }

    /**
     * 停止滚动
     */
    fun stopAutoScroll() {
        tv_vertical_scroll?.stopAutoScroll()
    }

    /**
     * 设置点击监听
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        tv_vertical_scroll?.setOnItemClickListener(listener)
    }

    fun setRightTextStyle(textSize: Float,@ColorInt color: Int){
        tv_right?.setTextColor(color)
        tv_right?.textSize = textSize
    }

    fun setVerticalScrollTextStyle(textSize: Float, padding: Int, @ColorInt textColor: Int){
        tv_vertical_scroll?.setTextStyle(textSize, padding, textColor)
    }

    /**
     * 设置轮播间隔时间
     */
    fun setTextStillTime(time: Long) {
        tv_vertical_scroll?.setTextStillTime(time)
    }

}