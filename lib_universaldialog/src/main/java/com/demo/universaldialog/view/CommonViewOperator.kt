package com.demo.universaldialog.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.demo.universaldialog.DialogConfig
import com.demo.universaldialog.interfaces.DialogDataConfig
import com.demo.universaldialog.interfaces.ContentViewCreator
import com.demo.universaldialog.R
import com.demo.universaldialog.enums.ShowFrom
import com.demo.universaldialog.enums.XLocation
import com.demo.universaldialog.enums.YLocation
import com.demo.universaldialog.utils.StatusBarUtil
import com.demo.universaldialog.utils.Utils
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.min

/**
 * 弹窗View生成、出现动画、消息动画、拖动等统一处理类
 */
abstract class CommonViewOperator<T>(
    act: Context,
    val dialogConfig: DialogConfig,
    private val onDismissFromTouch: OnDismissFromTouch?
) {
    /**
     * 用于刷新或者更新layout使用
     */
    protected val handler = Handler(Looper.getMainLooper())

    /**
     * 不直接依赖Activity
     */
    protected val context: Context = act.applicationContext

    /**
     * 弹窗真正的根View
     */
    protected var mRootContentView: ContentView? = null

    /**
     * 当配置需要背景的时候，则会有这个view，参考[DialogDataConfig.isCardBackground]
     */
    protected var mCardView : CardView?=null

    /**
     * 接入方真正的弹窗内容，由[ContentViewCreator.createContentView]生成
     */
    protected var createView: View? = null

    /**
     * 根据Activity是否通顶的情况，决定是否需要增加这个值，在[createView]的顶部增加这个margin
     */
    protected var topBarMargin = 0

    /**
     * 配置的顶部边距，参考[DialogDataConfig.getMarginTop]
     */
    protected var topConfigMargin = 0
    /**
     * 当配置需要背景的时候，则会有值，参考[DialogDataConfig.isCardBackground]
     */
    protected var cardMinMargin = 0

    /**
     * 是否已经附着展示了
     */
    protected var isAttach = false

    /**
     * 滑动或者动画时候X方向的偏移距离，相对出现位置
     */
    private var moveX = 0f
    /**
     * 滑动或者动画时候Y方向的偏移距离，相对出现位置
     */
    private var moveY = 0f

    init {
        createContentView()
    }

    /**
     * 创建要显示的View
     * 根View为[ContentView]，处理滑动事件
     * 有背景的情况下会创建一层[CardView]和正在内容的包裹View，否则由根包裹
     * 处理margin、长宽等等
     */
    protected fun createContentView() {
        if (mRootContentView != null) return
        mRootContentView = ContentView(context, dialogConfig.dialogDataConfig.getShowFrom())
        var contentView = mRootContentView as LinearLayout
        //创建背景层
        if(dialogConfig.dialogDataConfig.isCardBackground()){
            cardMinMargin = Utils.dip2px(context, 10f)
            mCardView = CardView(context)
            mCardView?.radius = cardMinMargin.toFloat()
            mCardView?.elevation = cardMinMargin.toFloat()/2
            mCardView?.maxCardElevation = cardMinMargin.toFloat()/2
            mCardView?.preventCornerOverlap = true
            mCardView?.useCompatPadding = false
            mRootContentView?.addView(mCardView, -1, -1)
            val lp = mCardView?.layoutParams as LinearLayout.LayoutParams
            lp.rightMargin = cardMinMargin
            lp.leftMargin = cardMinMargin
            lp.topMargin = cardMinMargin
            lp.bottomMargin = cardMinMargin

            contentView = LinearLayout(context)
            mCardView?.addView(contentView, -1, -1)
            mCardView?.id = R.id.universal_card_view

        }
        contentView.isClickable = true
        //创建真正的显示视图View
        val createView = dialogConfig.contentViewCreator.createContentView(context, contentView!!)
        if (createView.parent == null) {
            contentView.addView(
                createView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        //处理Margin
        val lp = (if(mCardView != null) mCardView!!.layoutParams  else createView.layoutParams)
                as LinearLayout.LayoutParams
        topConfigMargin = Utils.dip2px(
            context,
            dialogConfig.dialogDataConfig.getMarginTop().toFloat()
        )
        lp.bottomMargin = cardMinMargin +
            Utils.dip2px(context, dialogConfig.dialogDataConfig.getMarginBottom().toFloat())
        lp.leftMargin = cardMinMargin +
                Utils.dip2px(context, dialogConfig.dialogDataConfig.getMarginLeft().toFloat())
        lp.rightMargin = cardMinMargin +
                Utils.dip2px(context, dialogConfig.dialogDataConfig.getMarginRight().toFloat())

        this.createView = createView

        dialogConfig.dialogCallback?.onCreateContentView(createView)

        //初始化拖动相关
        if(canMove())initMove()
    }

    /**
     * 能否进行拖动
     */
    protected open fun canMove():Boolean{
        return true
    }

    /**
     * 执行动画进场或者退场动画，动画根据配置有四个方法：左上右下，默认没有动画；同时附加透明度变化
     * 入场动画：从屏幕外到当前位置
     * 退场动画：从当前位置到屏幕外
     * [animIn]是否是进场动画
     */
    protected fun executeAnim(animIn: Boolean){
        var isX = false
        var from = 0
        var to = 0

        val maxHeight = Utils.getScreenHeight(context)
        val maxWidth = Utils.getScreenWidth(context)

        //获取动画开始和结束的位置
        when (dialogConfig.dialogDataConfig.getShowFrom()) {
            ShowFrom.BOTTOM -> {
                isX = false
                if(animIn){
                    from = maxHeight
                    to = 0
                }else{
                    from = moveY.toInt()
                    to =  moveY.toInt() + maxHeight
                }
            }
            ShowFrom.TOP -> {
                isX = false
                if(animIn){
                    from = -maxHeight
                    to = 0
                }else{
                    from = moveY.toInt()
                    to =  moveY.toInt() - maxHeight
                }
            }
            ShowFrom.LEFT -> {
                isX = true
                if(animIn){
                    from = -maxWidth
                    to = 0
                }else{
                    from = moveX.toInt()
                    to =  moveX.toInt() -maxWidth
                }
            }
            ShowFrom.RIGHT -> {
                isX = true
                if(animIn){
                    from = maxWidth
                    to = 0
                }else{
                    from = moveX.toInt()
                    to =  moveX.toInt() + maxWidth
                }
            }
            else -> null
        }
        //default情况，不需要动画
        if(from == 0 && to == 0){
            if (!animIn) {
                removeView()
            }
            return
        }
        //位移动画，只提供位置值，具体是怎么位移让子类决定
        val animXY = ValueAnimator.ofInt(from, to)
        animXY.duration = 500
        animXY.addUpdateListener {
            val current = it.animatedValue as Int
            val x = if (isX)current else 0
            val y = if (!isX)current else 0
            moveXY(x.toFloat(), y.toFloat())
        }
        //附加透明度变化
        val alphaFrom = if(animIn) 0.2f else 1f
        val alphaTo = if(!animIn) 0.2f else 1f
        val animAlpha = ObjectAnimator.ofFloat(mRootContentView, "alpha", alphaFrom, alphaTo)
        animAlpha.duration
        val anim = AnimatorSet()
        anim.playTogether(animXY, animAlpha)
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                //重置位移变量
                moveX = 0f
                moveY = 0f
                //结束情况下，需要执行remove操作
                if (!animIn) {
                    removeView()
                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
        anim.start()
    }

    /**
     * 从父视图移除
     */
    open fun removeView() {
        val par = mRootContentView?.parent as? ViewGroup
        par?.removeView(mRootContentView)
    }

    /**
     * 附加到对应的地方，根据情况决定附着对象
     */
    abstract fun onAttach(activity: T, anim: Boolean)

    /**
     *  从附着对象移除
     */
    abstract fun onDetach(anim: Boolean)

    /**
     * 根据配置得到长宽大小，注意高度会受是否通顶影响而变化，特别是悬浮弹窗情况下
     */
    protected fun getLayoutParams(): ViewGroup.LayoutParams {
        val lp = ViewGroup.LayoutParams(-2, -2)
        val marginLeftRight = Utils.dip2px(
            context, (dialogConfig.dialogDataConfig.getMarginLeft()
                    + dialogConfig.dialogDataConfig.getMarginRight()).toFloat()
        )
        val maxWidth = Utils.getScreenWidth(context) - marginLeftRight - cardMinMargin*2
        val marginTopBottom = Utils.dip2px(
            context, (dialogConfig.dialogDataConfig.getMarginBottom()
                    + dialogConfig.dialogDataConfig.getMarginTop()).toFloat()
        )
        val maxHeight = Utils.getScreenHeight(context) - marginTopBottom -
                StatusBarUtil.getStatusBarHeight(context) - cardMinMargin*2
        lp.width = getSize(maxWidth, dialogConfig.dialogDataConfig.getContentWidth())
        lp.height = getSize(maxHeight, dialogConfig.dialogDataConfig.getContentHeight())
        if (lp.height >= 0) {
            lp.height += topBarMargin + marginTopBottom + cardMinMargin*2
        }else {
            mCardView?.layoutParams?.height = -2
            createView?.layoutParams?.height = -2
        }
        if (lp.width >= 0) {
            lp.width += marginLeftRight + cardMinMargin*2
        }else {
            mCardView?.layoutParams?.width = -2
            createView?.layoutParams?.width = -2
        }
        return lp
    }

    /**
     * 对齐方式获取
     */
    protected fun getLocation():Int {
        return when (dialogConfig.dialogDataConfig.getShowX()) {
            XLocation.LEFT -> Gravity.LEFT
            XLocation.CENTER -> Gravity.CENTER_HORIZONTAL
            XLocation.RIGHT -> Gravity.RIGHT
        } or when (dialogConfig.dialogDataConfig.getShowY()) {
            YLocation.TOP -> Gravity.TOP
            YLocation.CENTER -> Gravity.CENTER_VERTICAL
            YLocation.BOTTOM -> Gravity.BOTTOM
        }
    }

    /**
     * 根据配置获取大小
     */
    protected fun getSize(max: Int, cof: String?): Int {
        try {
            return if (cof.isNullOrBlank()) {
                ViewGroup.LayoutParams.WRAP_CONTENT
            } else if (cof.contains("%")) {
                val p = min(abs(cof!!.replace("%", "").toFloat() / 100f), 1f)
                (max * p).toInt()
            } else {
                val s1 = abs(Utils.dip2px(context, cof.toFloat()))
                if (s1 <= max) s1 else max
            }
        } catch (e: Exception) {
            if (Utils.isDebug(context)) e.printStackTrace()
        }
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    /**
     * 根据传入的View判断是否增加顶部的状态栏高度
     */
    protected fun setCreateViewToMargin(decorView: View?){
        topBarMargin = 0
        if (dialogConfig.dialogDataConfig.getShowY() == YLocation.TOP ) {
            val flg = (decorView?.systemUiVisibility?:0) and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if(flg != 0){
                topBarMargin = StatusBarUtil.getStatusBarHeight(context)
            }
        }
        val lp = (if(mCardView != null) mCardView!!.layoutParams  else createView?.layoutParams)
                as? LinearLayout.LayoutParams
        lp?.topMargin = topConfigMargin + topBarMargin + cardMinMargin
        createView?.requestLayout()
    }


    /**
     * 初始化拖动回调，分正则拖动和释放；
     * 拖动和释放会过滤其他方向的值
     */
    private fun initMove() {
        mRootContentView?.onTranslationChangeListener = object :
            ContentView.OnTranslationChangeListener {
            override fun onChange(changX: Float, changY: Float) {
                moveY += changY
                moveX += changX
                //过滤反方向的值，传过来的只有偏移量
                when (dialogConfig.dialogDataConfig.getShowFrom()) {
                    ShowFrom.TOP -> {
                        moveY = 0f.coerceAtMost(moveY)
                    }
                    ShowFrom.BOTTOM -> {
                        moveY = 0f.coerceAtLeast(moveY)
                    }
                    ShowFrom.LEFT -> {
                        moveX = 0f.coerceAtMost(moveX)
                    }
                    ShowFrom.RIGHT -> {
                        moveX = 0f.coerceAtLeast(moveX)
                    }
                    else -> {
                        moveX = 0f
                        moveY = 0f
                    }
                }
                moveXY(moveX, moveY)
            }

            override fun onRelease(speed: Float) {
                if(Utils.isDebug(context)){
                    Log.d("onRelease", "speed:" + speed)
                }
                //速度快则消失弹窗
                if (abs(speed) > 200) {
                    onDetach(true)
                    onDismissFromTouch?.onDismiss()
                } else { //否则执行回弹动画
                    executeReleaseAnim()
                }
            }

        }
    }

    /**
     * 滑动到X与Y
     */
    protected abstract fun moveXY(x:Float, y:Float)

    /**
     * 执行回弹动画
     */
    private fun executeReleaseAnim(){
        var currentValue = 0f
        var isX = false
        if(moveX != 0f){
            currentValue = moveX
            isX = true
        }else if(moveY != 0f){
            currentValue = moveY
            isX = false
        }
        if(currentValue == 0f){
            return
        }

        val anim = ValueAnimator.ofFloat(currentValue, 0f)
            .setDuration(abs(ceil(currentValue * 0.3).toLong()))

        anim.addUpdateListener {
                if(isX){
                    moveX = it.animatedValue as Float
                    moveXY(moveX, 0f)
                }else{
                    moveY = it.animatedValue as Float
                    moveXY(0f, moveY)
                }
        }
        anim.start()
    }

    /**
     * 滑动消失回调
     */
    interface OnDismissFromTouch {
        fun onDismiss()
    }
}