package com.demo.main.widget

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher

/**
 * 垂直滚动文本
 */
class VerticalScrollTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TextSwitcher(context, attrs), ViewSwitcher.ViewFactory {

    companion object {
        private const val FLAG_START_AUTO_SCROLL = 0
        private const val FLAG_STOP_AUTO_SCROLL = 1
        private const val FLAG_START_FIRST_SCROLL = 2 // 第一次滚动不用间隔
    }

    private var mTextSize = 15f
    private var mPadding = 5
    private var textColor = Color.BLACK
    private var maxLines = 1 // 默认最多一行
    private var animInterval: Long = 3000 // 默认3秒间隔

    private var itemClickListener: OnItemClickListener? = null
    private var currentId = -1
    private val textList = ArrayList<String>()
    private lateinit var handler: Handler

    init {
        setAnimTime(1000)
        handler = Handler(Looper.getMainLooper()) { msg ->
            when (msg.what) {
                FLAG_START_AUTO_SCROLL -> {
                    if (textList.isNotEmpty()) {
                        currentId++
                        setText(textList[currentId % textList.size])
                    }
                    handler.sendEmptyMessageDelayed(FLAG_START_AUTO_SCROLL, animInterval)
                    true
                }

                FLAG_STOP_AUTO_SCROLL -> {
                    handler.removeMessages(FLAG_START_AUTO_SCROLL)
                    true
                }

                FLAG_START_FIRST_SCROLL -> {
                    handler.sendEmptyMessageDelayed(FLAG_START_AUTO_SCROLL, 0)
                    true
                }

                else -> false
            }
        }
    }

    /**
     * 设置文本样式
     */
    fun setTextStyle(textSize: Float, padding: Int, textColor: Int) {
        mTextSize = textSize
        mPadding = padding
        this.textColor = textColor
    }

    /**
     * 设置最大行数
     */
    fun setMaxLines(maxLines: Int) {
        this.maxLines = maxLines
    }

    /**
     * 设置动画时间
     */
    fun setAnimTime(animDuration: Long) {
        setFactory(this)
        // 进入动画：从下往上 + 淡入
        val inAnim = AnimationSet(true).apply {
            addAnimation(AlphaAnimation(0f, 1f).apply { // 透明度从0到1
                duration = animDuration
            })
            addAnimation(TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,  // 从底部开始
                Animation.RELATIVE_TO_SELF, 0f   // 移动到原始位置
            ).apply {
                duration = animDuration
            })
            interpolator = AccelerateInterpolator()
        }

        // 退出动画：向上消失 + 淡出
        val outAnim = AnimationSet(true).apply {
            addAnimation(AlphaAnimation(1f, 0f).apply { // 透明度从1到0
                duration = animDuration
            })
            addAnimation(TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1f  // 向上移动消失
            ).apply {
                duration = animDuration
            })
            interpolator = AccelerateInterpolator()
        }
        inAnimation = inAnim
        outAnimation = outAnim
    }

    /**
     * 设置轮播间隔时间
     */
    fun setTextStillTime(time: Long) {
        animInterval = time
    }

    /**
     * 设置数据源
     */
    fun setTextList(titles: List<String>) {
        textList.clear()
        textList.addAll(titles)
        currentId = -1
    }

    /**
     * 开始滚动
     */
    fun startAutoScroll() {
        handler.sendEmptyMessage(FLAG_START_FIRST_SCROLL)
    }

    /**
     * 停止滚动
     */
    fun stopAutoScroll() {
        handler.sendEmptyMessage(FLAG_STOP_AUTO_SCROLL)
    }

    override fun makeView(): View {
        return TextView(context).apply {
            gravity = Gravity.START
            maxLines = this@VerticalScrollTextView.maxLines
            if (maxLines == 1) {
                ellipsize = TextUtils.TruncateAt.END
            }
            setPadding(mPadding, mPadding, mPadding, mPadding)
            setTextColor(textColor)
            textSize = mTextSize
            isClickable = true
            setOnClickListener {
                if (itemClickListener != null && textList.isNotEmpty() && currentId != -1) {
                    itemClickListener?.onItemClick(currentId % textList.size)
                }
            }
        }
    }

    /**
     * 设置点击监听
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    /**
     * 轮播文本点击监听器
     */
    fun interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
