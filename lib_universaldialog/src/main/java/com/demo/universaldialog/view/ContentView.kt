package com.demo.universaldialog.view

import android.content.Context
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.widget.LinearLayout
import com.demo.universaldialog.enums.ShowFrom
import kotlin.math.abs

/**
 * 用于悬浮弹窗可拖动以及滑动消失，只能朝显示方法滑动及滑动消失，[ShowFrom.DEFAULT]不会有效果
 * [showFrom] 显示出现的方向
 */
class ContentView(context: Context, private val showFrom: ShowFrom) : LinearLayout(context) {
    /**
     * 上次数据X
     */
    private var lastX = 0f
    /**
     * 上次数据Y
     */
    private var lastY = 0f
    /**
     * 偏移数据X
     */
    private var offsetX = 0f
    /**
     * 偏移数据X
     */
    private var offsetY = 0f
    /**
     * 最小滑动距离，大于这个值才算滑动
     */
    private var minScroll = 0
    /**
     * 滑动速度统计工具
     */
    private var mVelocityTracker: VelocityTracker? = null
    /**
     * 最大滑动速度
     */
    private var mMaximumVelocity = 0

    /**
     * 拖动和释放回调接口
     */
    var onTranslationChangeListener: OnTranslationChangeListener? = null

    init {
        val configuration = ViewConfiguration.get(context)
        minScroll = configuration.scaledTouchSlop
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity
    }

    /**
     * 由于[ContentView]本身在移动变化，导致技术滚动速度不正确，
     * 需要修改[MotionEvent.getX] [MotionEvent.getY]，使用原生的rawX，rawY比较合适
     */
    private fun addEvent(event: MotionEvent){
        val vEv = MotionEvent.obtain(event)
        vEv.setLocation(event.rawX, event.rawY)
        mVelocityTracker?.addMovement(vEv)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!needChange()) return false
        addEvent(event)
        when (event.action) {
            //计算每次偏移量
            MotionEvent.ACTION_MOVE -> {
                offsetY = event.rawY - lastY
                offsetX = event.rawX - lastX
                lastY = event.rawY
                lastX = event.rawX

                when (showFrom) {
                    ShowFrom.BOTTOM,
                    ShowFrom.TOP -> {
                        offsetX = 0f
                    }
                    ShowFrom.RIGHT,
                    ShowFrom.LEFT -> {
                        offsetY = 0f
                    }
                    else -> {
                        offsetY = 0f
                        offsetX = 0f
                    }
                }
                onTranslationChangeListener?.onChange(offsetX, offsetY)
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                mVelocityTracker?.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                //过滤其他方向的滑动速度值
                val speed = when (showFrom) {
                    ShowFrom.BOTTOM -> {
                        val s = mVelocityTracker?.yVelocity ?: 0f
                        s.coerceAtLeast(0f)
                    }
                    ShowFrom.TOP -> {
                        val s = mVelocityTracker?.yVelocity ?: 0f
                        s.coerceAtMost(0f)
                    }
                    ShowFrom.RIGHT -> {
                        val s = mVelocityTracker?.xVelocity ?: 0f
                        s.coerceAtLeast(0f)
                    }
                    ShowFrom.LEFT -> {
                        val s = mVelocityTracker?.xVelocity ?: 0f
                        s.coerceAtMost(0f)
                    }
                    else -> {
                        0f
                    }
                }
                onTranslationChangeListener?.onRelease(speed)
                reset()
                return false
            }
        }
        return true
    }

    /**
     * 不设置回调或者出现方式为默认的情况下没有拖动效果
     */
    private fun needChange(): Boolean {
        return onTranslationChangeListener != null || showFrom != ShowFrom.DEFAULT
    }

    /**
     * 根据最小滑动距离决定是不是要拦截滚动，否则可能是点击
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!needChange()) return super.onInterceptTouchEvent(ev)
        if(mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain()
        }
        addEvent(ev)
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.rawY
                lastX = ev.rawX
                return false
            }
            MotionEvent.ACTION_MOVE ->
                if (abs(lastY - ev.rawY) > minScroll || Math.abs(lastX - ev.rawX) > minScroll)
                    return true
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                reset()
                return false
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    /**
     * 重置所有数据
     */
    private fun reset() {
        lastX = 0f
        lastY = 0f
        offsetX = 0f
        offsetY = 0f
        mVelocityTracker?.clear()
    }

    /**
     * 滑动及手指离开回调
     */
    interface OnTranslationChangeListener {
        /**
         * 每次滑动的变化量，剔除了其他方向的值，如顶部弹出，会剔除水平方向的值，已就是[changX] = 0
         * [changX] 水平方向的变化值
         * [changY] 垂直方向的变化值
         */
        fun onChange(changX: Float, changY: Float)

        /**
         * 滑动过后显示方向的速度，
         * 当[ShowFrom.TOP] [speed] <= 0
         * 当[ShowFrom.BOTTOM] [speed] >= 0
         * 当[ShowFrom.LEFT] [speed] <= 0
         * 当[ShowFrom.RIGHT] [speed] >= 0
         */
        fun onRelease(speed: Float)
    }

}