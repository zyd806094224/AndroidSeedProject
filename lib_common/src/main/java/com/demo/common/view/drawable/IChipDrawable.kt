package com.demo.common.view.drawable

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import androidx.annotation.Px

/**
 * @Description:
 * @Date: 2024/9/10 19:44
 * @author:  zhaoyudong
 * @version: 1.0
 */
interface IChipDrawable {
    /**
     * @Description:  设置左上角圆角
     * @DateTime 2023-05-9, 周二, 10:53
     
     * @param chipCornerRadius
     */
    fun setChipTopLeftCornerRadius(chipCornerRadius: Float)

    /**
     * @Description:  设置左下角圆角
     * @DateTime 2023-05-9, 周二, 10:53
     
     * @param chipCornerRadius
     */
    fun setChipTopRightCornerRadius(chipCornerRadius: Float)

    /**
     * @Description:  设置左下角圆角
     * @DateTime 2023-05-9, 周二, 10:53
     
     * @param chipCornerRadius
     */
    fun setChipBottomLeftCornerRadius(chipCornerRadius: Float)

    /**
     * @Description:  设置右下角圆角
     * @DateTime 2023-05-9, 周二, 10:53
     
     * @param chipCornerRadius
     */
    fun setChipBottomRightCornerRadius(chipCornerRadius: Float)

    /**
     * 设置边框颜色
     * @param color 边框色
     */
    fun setChipStrokeColor(@ColorInt color: Int)

    /**
     * 设置边框颜色
     * 会根据状态进行修改
     * @param color 边框色
     */
    fun setChipStrokeColor(state: Array<IntArray>, @ColorInt color: IntArray)

    /**
     * 设置边框颜色
     * 会根据状态进行修改
     * @param strokeColor 边框色
     */
    fun setChipStrokeColor(strokeColor: ColorStateList?)

    /**
     * 添加边框颜色状态色
     * 会根据状态进行修改
     * @param state 状态数组
     * @param strokeColor 边框色
     */
    fun addChipStrokeStateColor(state: IntArray, @ColorInt strokeColor: Int)

    /**
     * 圆角角度
     * @param chipCornerRadius 圆角
     */
    fun setChipCornerRadius(@Px chipCornerRadius: Float)

    /**
     * 边框宽度
     * @param chipStrokeWidth 边框宽度
     */
    fun setChipStrokeWidth(@Px chipStrokeWidth: Float)

    /**
     * 设置背景颜色
     * @param color 背景色
     */
    fun setChipBackgroundColor(@ColorInt color: Int)

    /**
     * 设置背景颜色
     * 会根据状态进行修改
     * @param backgroundColor 边框色
     */
    fun setChipBackgroundColor(backgroundColor: ColorStateList?)

    /**
     * 设置背景颜色
     * 会根据状态进行修改
     * @param state 状态数组
     * @param color 背景色
     */
    fun setChipBackgroundStateColor(state: Array<IntArray>, @ColorInt color: IntArray)

    /**
     * 添加背景颜色状态色
     * 会根据状态进行修改
     * @param state 状态数组
     * @param color 背景色
     */
    fun addChipBackgroundStateColor(state: IntArray, @ColorInt color: Int)

    /**
     * 设置背景开始颜色 渐变时生效
     * @param color 背景开始色
     */
    fun setChipBackgroundStartColor(@ColorInt color: Int)

    /**
     * 设置背景开始颜色 渐变时生效
     * 会根据状态进行修改
     * @param backgroundColor 边框色
     */
    fun setChipBackgroundStartColor(backgroundColor: ColorStateList?)

    /**
     * 设置背景开始颜色 渐变时生效
     * 会根据状态进行修改
     * @param state 状态数组
     * @param color 背景色
     */
    fun setChipBackgroundStateStartColor(state: Array<IntArray>, @ColorInt color: IntArray)

    /**
     * 添加背景开始颜色 渐变时生效
     * 会根据状态进行修改
     * @param state 状态数组
     * @param color 背景色
     */
    fun addChipBackgroundStateStartColor(state: IntArray, @ColorInt color: Int)

    /**
     * 设置背景结束颜色 渐变时生效
     * @param color 背景开始色
     */
    fun setChipBackgroundEndColor(@ColorInt color: Int)

    /**
     * 设置背景结束颜色 渐变时生效
     * 会根据状态进行修改
     * @param backgroundColor 边框色
     */
    fun setChipBackgroundEndColor(backgroundColor: ColorStateList?)

    /**
     * 设置背景结束颜色 渐变时生效
     * 会根据状态进行修改
     * @param state 状态数组
     * @param color 背景色
     */
    fun setChipBackgroundStateEndColor(state: Array<IntArray>, @ColorInt color: IntArray)

    /**
     * 添加背景结束颜色 渐变时生效
     * 会根据状态进行修改
     * @param state 状态数组
     * @param color 背景色
     */
    fun addChipBackgroundStateEndColor(state: IntArray, @ColorInt color: Int)

    /**
     * 设置背景渐变色方向 0:从左到右渐变 1:从上到下渐变 默认从左到右
     * @param angle Int
     */
    fun setChipBackgroundGradientAngle(angle:Int)

}