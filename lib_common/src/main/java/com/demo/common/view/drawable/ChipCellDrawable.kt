package com.demo.common.view.drawable

/**
 * @Description:
 * @Date: 2024/9/10 19:46
 * @author:  zhaoyudong
 * @version: 1.0
 */
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.StyleRes
import com.demo.common.R
import com.demo.common.utils.PlatformCompat
import java.util.Arrays

class ChipCellDrawable private constructor(
    private val context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int = 0
) : StateListDrawable(), IChipDrawable {
    private val DEFAULT_STATE: IntArray = intArrayOf(android.R.attr.state_enabled)
    private var mStrokeWidth: Float = 0f   // 边框宽度

    @ColorInt
    private var currentBackgroundColor: Int = 0 // 当前填充色

    @ColorInt
    private var currentStrokeColor: Int = 0 // 当前边框颜色

    @ColorInt
    private var currentBackgroundStartColor: Int = 0  // 当前填充色的开始颜色 -- 渐变生效

    @ColorInt
    private var currentBackgroundEndColor: Int = 0 // 当前填充色的结束颜色 -- 渐变生效

    private var mBackgroundGradientAngle:Int = 0//当前背景渐变色方向

    @IntRange(from = 0, to = 255)
    private var currentAlpha: Int = 0  // 透明度
    private var mBackgroundColors: ColorStateList? = null //  填充色
    private var mBackgroundStartColors: ColorStateList? = null // 当前填充色的开始颜色 -- 渐变生效
    private var mBackgroundEndColors: ColorStateList? = null // 当前填充色的结束颜色 -- 渐变生效
    private var mStrokeColors: ColorStateList? = null // 边框颜色
    private var cornerRadius: Float = 0f   // 圆角

    private var topLeftCornerRadius: Float = 0f // 圆角

    private var topRightCornerRadius: Float = 0f // 圆角

    private var bottomLeftCornerRadius: Float = 0f // 圆角

    private var bottomRightCornerRadius: Float = 0f // 圆角

    private val chipPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    private var linearGradient: LinearGradient? = null
    private var colorFilter: ColorFilter? = null
    private fun loadFromAttributes(
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int = 0
    ) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ChipCell,
            defStyleAttr,
            defStyleRes
        )
        setChipBackgroundColor(
            PlatformCompat.getColorStateList(
                context,
                typedArray,
                R.styleable.ChipCell_chipCellBackgroundColor
            )
        )
        setChipBackgroundStartColor(
            PlatformCompat.getColorStateList(
                context,
                typedArray,
                R.styleable.ChipCell_chipCellBackgroundStartColor
            )
        )
        setChipBackgroundEndColor(
            PlatformCompat.getColorStateList(
                context,
                typedArray,
                R.styleable.ChipCell_chipCellBackgroundEndColor
            )
        )
        setChipBackgroundGradientAngle(
            PlatformCompat.getEnum(context,typedArray,R.styleable.ChipCell_chipCellBackgroundAngle,0)
        )
        setChipStrokeColor(
            PlatformCompat.getColorStateList(
                context,
                typedArray,
                R.styleable.ChipCell_chipCellStrokeColor
            )
        )
        setChipCornerRadius(typedArray)
        setChipStrokeWidth(
            typedArray.getDimension(
                R.styleable.ChipCell_chipCellStrokeWidth,
                0f
            )
        )
        typedArray.recycle()
    }

    private fun setChipCornerRadius(typedArray: TypedArray) {
        if (typedArray.hasValue(R.styleable.ChipCell_chipCellRadius)) {
            cornerRadius = typedArray.getDimension(
                R.styleable.ChipCell_chipCellRadius,
                0f
            )
        }
        if (typedArray.hasValue(R.styleable.ChipCell_chipCellTopLeftCornerRadius)) {
            topLeftCornerRadius = typedArray.getDimension(
                R.styleable.ChipCell_chipCellTopLeftCornerRadius,
                0f
            )
        } else {
            topLeftCornerRadius = cornerRadius
        }
        if (typedArray.hasValue(R.styleable.ChipCell_chipCellTopRightCornerRadius)) {
            topRightCornerRadius = typedArray.getDimension(
                R.styleable.ChipCell_chipCellTopRightCornerRadius,
                0f
            )
        } else {
            topRightCornerRadius = cornerRadius
        }
        if (typedArray.hasValue(R.styleable.ChipCell_chipCellBottomLeftCornerRadius)) {
            bottomLeftCornerRadius = typedArray.getDimension(
                R.styleable.ChipCell_chipCellBottomLeftCornerRadius,
                0f
            )
        } else {
            bottomLeftCornerRadius = cornerRadius
        }
        if (typedArray.hasValue(R.styleable.ChipCell_chipCellBottomRightCornerRadius)) {
            bottomRightCornerRadius = typedArray.getDimension(
                R.styleable.ChipCell_chipCellBottomRightCornerRadius,
                0f
            )
        } else {
            bottomRightCornerRadius = cornerRadius
        }
        invalidateSelf()
    }

    override fun onStateChange(state: IntArray): Boolean {
        var invalidate = super.onStateChange(state)
        // 边框颜色
        val newStrokeColor = if (mStrokeColors != null) mStrokeColors!!.getColorForState(
            state,
            currentStrokeColor
        ) else 0
        if (newStrokeColor != currentStrokeColor) {
            currentStrokeColor = newStrokeColor
            invalidate = true
        }
        // solid 填充颜色
        val newBackgroundColor =
            if (mBackgroundColors != null) mBackgroundColors!!.getColorForState(
                state,
                currentBackgroundColor
            ) else 0
        if (newBackgroundColor != currentBackgroundColor) {
            currentBackgroundColor = newBackgroundColor
            invalidate = true
        }
        val newBackgroundStartColor =
            if (mBackgroundStartColors != null) mBackgroundStartColors!!.getColorForState(
                state,
                currentBackgroundStartColor
            ) else 0
        if (newBackgroundStartColor != currentBackgroundStartColor) {
            currentBackgroundStartColor = newBackgroundStartColor
            invalidate = true
        }
        val newBackgroundEndColor =
            if (mBackgroundEndColors != null) mBackgroundEndColors!!.getColorForState(
                state,
                currentBackgroundEndColor
            ) else 0
        if (newBackgroundEndColor != currentBackgroundEndColor) {
            currentBackgroundEndColor = newBackgroundEndColor
            invalidate = true
        }
        if(mBackgroundGradientAngle != 0){
            invalidate = true
        }
        return invalidate
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        if (bounds.isEmpty || alpha == 0) { // 透明直接返回不绘制即可
            return
        }
        drawChipBackground(canvas, bounds)
        drawChipStroke(canvas, bounds)
    }

    private fun drawChipBackground(canvas: Canvas, bounds: Rect) {
        rectF.set(bounds)
        val path = Path()
        val radii = FloatArray(8)
        Arrays.fill(radii, 0, 2, topLeftCornerRadius)
        Arrays.fill(radii, 2, 4, topRightCornerRadius)
        Arrays.fill(radii, 4, 6, bottomRightCornerRadius)
        Arrays.fill(radii, 6, 8, bottomLeftCornerRadius)
        path.addRoundRect(rectF, radii, Path.Direction.CCW)
        if (currentBackgroundStartColor != currentBackgroundEndColor) { // 开始颜色和结束颜色不一致，渐变色
            if(mBackgroundGradientAngle == 1){//渐变色从上向下
                linearGradient = LinearGradient(
                    0F,
                    0F,
                    0F,
                    bounds.bottom.toFloat(),
                    currentBackgroundStartColor,
                    currentBackgroundEndColor,
                    Shader.TileMode.CLAMP
                )
            } else if(mBackgroundGradientAngle == 2){//渐变色从左上到右下
                linearGradient = LinearGradient(
                    0F,
                    0F,
                    bounds.right.toFloat(),
                    bounds.bottom.toFloat(),
                    currentBackgroundStartColor,
                    currentBackgroundEndColor,
                    Shader.TileMode.CLAMP
                )
            }else if(mBackgroundGradientAngle == 3){//渐变色从左下到右上
                linearGradient = LinearGradient(
                    0F,
                    bounds.bottom.toFloat(),
                    bounds.right.toFloat(),
                    0F,
                    currentBackgroundStartColor,
                    currentBackgroundEndColor,
                    Shader.TileMode.CLAMP
                )
            }else {//默认渐变色从左向右
                linearGradient = LinearGradient(
                    0F,
                    0F,
                    bounds.right.toFloat(),
                    0F,
                    currentBackgroundStartColor,
                    currentBackgroundEndColor,
                    Shader.TileMode.CLAMP
                )
            }

            chipPaint.shader = linearGradient
            chipPaint.style = Paint.Style.FILL
            chipPaint.colorFilter = colorFilter
            canvas.drawPath(path, chipPaint)
            return
        }
        var bgColor = Color.TRANSPARENT
        bgColor =
            if (currentBackgroundStartColor != Color.TRANSPARENT) { // 非透明，且开始颜色和结束颜色一致
                currentBackgroundStartColor
            } else { // 开始和结束都为透明或者没有设置时，使用设置的 背景色
                currentBackgroundColor
            }
        if (bgColor != Color.TRANSPARENT) {
            chipPaint.color = bgColor
            chipPaint.style = Paint.Style.FILL
            chipPaint.colorFilter = colorFilter
            canvas.drawPath(path, chipPaint)
        }
    }

    /**
     * 绘制边框，且不考虑边框宽度为 0 和 边框颜色为透明色
     *
     * @param canvas 画布
     * @param bounds 边框
     */
    private fun drawChipStroke(canvas: Canvas, bounds: Rect) {
        if (mStrokeWidth > 0 && currentStrokeColor != Color.TRANSPARENT) {
            chipPaint.color = currentStrokeColor
            chipPaint.style = Paint.Style.STROKE
            chipPaint.colorFilter = colorFilter
            chipPaint.shader = null
            rectF.set(
                bounds.left + mStrokeWidth / 2f,
                bounds.top + mStrokeWidth / 2f,
                bounds.right - mStrokeWidth / 2f,
                bounds.bottom - mStrokeWidth / 2f
            )
            val path = Path()
            val radii = FloatArray(8)
            Arrays.fill(radii, 0, 2, topLeftCornerRadius - mStrokeWidth/2)
            Arrays.fill(radii, 2, 4, topRightCornerRadius - mStrokeWidth/2)
            Arrays.fill(radii, 4, 6, bottomRightCornerRadius - mStrokeWidth/2)
            Arrays.fill(radii, 6, 8, bottomLeftCornerRadius - mStrokeWidth/2)
            path.addRoundRect(rectF, radii, Path.Direction.CCW)
            canvas.drawPath(path, chipPaint)
        }
    }

    override fun setAlpha(alpha: Int) {
        if (currentAlpha != alpha) {
            currentAlpha = alpha
            invalidateSelf()
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        if (this.colorFilter !== colorFilter) {
            this.colorFilter = colorFilter
            invalidateSelf()
        }
    }

    override fun getColorFilter(): ColorFilter? {
        return colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    /**
     * 设置边框颜色
     *
     * @param color 边框色
     */
    override fun setChipStrokeColor(color: Int) {
        setChipStrokeColor(ColorStateList.valueOf(color))
    }

    /**
     * 设置边框颜色
     * 会根据状态进行修改
     *
     * @param state
     * @param color 边框色
     */
    override fun setChipStrokeColor(state: Array<IntArray>, color: IntArray) {
        setChipStrokeColor(ColorStateList(state, color))
    }

    /**
     * 设置边框颜色
     * 会根据状态进行修改
     *
     * @param strokeColor 边框色
     */
    override fun setChipStrokeColor(strokeColor: ColorStateList?) {
        if (mStrokeColors !== strokeColor) {
            mStrokeColors = strokeColor
            onStateChange(state)
        }
    }

    /**
     * 添加边框颜色状态色
     * 会根据状态进行修改
     *
     * @param state       状态数组
     * @param strokeColor 边框色
     */
    override fun addChipStrokeStateColor(state: IntArray, strokeColor: Int) {
        setChipStrokeColor(PlatformCompat.mergeStateListFront(mStrokeColors, state, strokeColor))
    }

    /**
     * 圆角角度
     *
     * @param chipCornerRadius 圆角
     */
    override fun setChipCornerRadius(chipCornerRadius: Float) {
        if (cornerRadius != chipCornerRadius) {
            cornerRadius = chipCornerRadius
            if(topLeftCornerRadius == 0F){
                this.topLeftCornerRadius = cornerRadius
            }
            if(topRightCornerRadius == 0F){
                this.topRightCornerRadius = cornerRadius
            }
            if(bottomLeftCornerRadius == 0F){
                this.bottomLeftCornerRadius = cornerRadius
            }
            if(bottomRightCornerRadius == 0F){
                this.bottomRightCornerRadius = cornerRadius
            }
            invalidateSelf()
        }
    }

    /**
     * 边框宽度
     *
     * @param chipStrokeWidth 边框宽度
     */
    override fun setChipStrokeWidth(chipStrokeWidth: Float) {
        if (mStrokeWidth != chipStrokeWidth) {
            mStrokeWidth = chipStrokeWidth
            chipPaint.strokeWidth = chipStrokeWidth
            invalidateSelf()
        }
    }

    /**
     * 设置背景颜色
     *
     * @param color 背景色
     */
    override fun setChipBackgroundColor(color: Int) {
        setChipBackgroundColor(ColorStateList.valueOf(color))
    }

    /**
     * 设置背景颜色
     * 会根据状态进行修改
     *
     * @param backgroundColor 边框色
     */
    override fun setChipBackgroundColor(backgroundColor: ColorStateList?) {
        if (mBackgroundColors !== backgroundColor) {
            mBackgroundColors = backgroundColor
            onStateChange(state)
        }
    }

    /**
     * 设置背景颜色
     * 会根据状态进行修改
     *
     * @param state 状态数组
     * @param color 背景色
     */
    override fun setChipBackgroundStateColor(state: Array<IntArray>, color: IntArray) {
        setChipBackgroundColor(ColorStateList(state, color))
    }

    /**
     * 添加背景颜色状态色
     * 会根据状态进行修改
     *
     * @param state 状态数组
     * @param color 背景色
     */
    override fun addChipBackgroundStateColor(state: IntArray, color: Int) {
        setChipBackgroundColor(PlatformCompat.mergeStateListFront(mBackgroundColors, state, color))
    }

    /**
     * 设置背景开始颜色 渐变时生效
     *
     * @param color 背景开始色
     */
    override fun setChipBackgroundStartColor(color: Int) {
        setChipBackgroundStartColor(ColorStateList.valueOf(color))
    }

    /**
     * 设置背景开始颜色 渐变时生效
     * 会根据状态进行修改
     *
     * @param backgroundColor 边框色
     */
    override fun setChipBackgroundStartColor(backgroundColor: ColorStateList?) {
        if (mBackgroundStartColors !== backgroundColor) {
            mBackgroundStartColors = backgroundColor
            onStateChange(state)
        }
    }

    /**
     * 设置背景开始颜色 渐变时生效
     * 会根据状态进行修改
     *
     * @param state 状态数组
     * @param color 背景色
     */
    override fun setChipBackgroundStateStartColor(state: Array<IntArray>, color: IntArray) {
        setChipBackgroundStartColor(ColorStateList(state, color))
    }

    /**
     * 添加背景开始颜色 渐变时生效
     * 会根据状态进行修改
     *
     * @param state 状态数组
     * @param color 背景色
     */
    override fun addChipBackgroundStateStartColor(state: IntArray, color: Int) {
        setChipBackgroundStartColor(
            PlatformCompat.mergeStateListFront(
                mBackgroundStartColors,
                state,
                color
            )
        )
    }

    /**
     * 设置背景结束颜色 渐变时生效
     *
     * @param color 背景开始色
     */
    override fun setChipBackgroundEndColor(color: Int) {
        setChipBackgroundEndColor(ColorStateList.valueOf(color))
    }

    /**
     * 设置背景结束颜色 渐变时生效
     * 会根据状态进行修改
     *
     * @param backgroundColor 边框色
     */
    override fun setChipBackgroundEndColor(backgroundColor: ColorStateList?) {
        if (mBackgroundEndColors !== backgroundColor) {
            mBackgroundEndColors = backgroundColor
            onStateChange(state)
        }
    }

    /**
     * 设置背景结束颜色 渐变时生效
     * 会根据状态进行修改
     *
     * @param state 状态数组
     * @param color 背景色
     */
    override fun setChipBackgroundStateEndColor(state: Array<IntArray>, color: IntArray) {
        setChipBackgroundEndColor(ColorStateList(state, color))
    }

    /**
     * 添加背景结束颜色 渐变时生效
     * 会根据状态进行修改
     *
     * @param state 状态数组
     * @param color 背景色
     */
    override fun addChipBackgroundStateEndColor(state: IntArray, color: Int) {
        setChipBackgroundEndColor(
            PlatformCompat.mergeStateListFront(
                mBackgroundEndColors,
                state,
                color
            )
        )
    }

    override fun setChipBackgroundGradientAngle(angle: Int) {
        if (mBackgroundGradientAngle !== angle) {
            mBackgroundGradientAngle = angle
            onStateChange(state)
        }
    }

    override fun setChipTopLeftCornerRadius(chipCornerRadius: Float) {
        if (topLeftCornerRadius != chipCornerRadius) {
            topLeftCornerRadius = chipCornerRadius
            invalidateSelf()
        }
    }

    override fun setChipTopRightCornerRadius(chipCornerRadius: Float) {
        if (topRightCornerRadius != chipCornerRadius) {
            topRightCornerRadius = chipCornerRadius
            invalidateSelf()
        }
    }

    override fun setChipBottomLeftCornerRadius(chipCornerRadius: Float) {
        if (bottomLeftCornerRadius != chipCornerRadius) {
            bottomLeftCornerRadius = chipCornerRadius
            invalidateSelf()
        }
    }

    override fun setChipBottomRightCornerRadius(chipCornerRadius: Float) {
        if (bottomRightCornerRadius != chipCornerRadius) {
            bottomRightCornerRadius = chipCornerRadius
            invalidateSelf()
        }
    }

    companion object {
        fun createFromAttributes(
            context: Context,
            attrs: AttributeSet?,
            @AttrRes defStyleAttr: Int,
            @StyleRes defStyleRes: Int
        ): ChipCellDrawable {
            val chip = ChipCellDrawable(context, attrs, defStyleAttr, defStyleRes)
            chip.loadFromAttributes(attrs, defStyleAttr, defStyleRes)
            return chip
        }

        @JvmStatic
        fun createFromAttributes(
            context: Context,
            attrs: AttributeSet?,
            @AttrRes defStyleAttr: Int
        ): ChipCellDrawable {
            val chip = ChipCellDrawable(context, attrs, defStyleAttr)
            chip.loadFromAttributes(attrs, defStyleAttr)
            return chip
        }

        @JvmStatic
        fun create(context: Context): ChipCellDrawable {
            return ChipCellDrawable(context, null, 0, 0)
        }
    }

    init {
        state = DEFAULT_STATE
    }
}