package com.demo.common.view.drawable

import android.content.res.ColorStateList

/**
 * @Description:
 * @Date: 2024/9/10 19:45
 * @author:  zhaoyudong
 * @version: 1.0
 */
class ChipStyleDelegate : IChipDrawable {
    private var chipCellDrawable: ChipCellDrawable? = null

    fun setDrawable(chipCellDrawable: ChipCellDrawable) {
        this.chipCellDrawable = chipCellDrawable
    }

    /**
     * @param chipCornerRadius
     * @Description: 设置左上角圆角
     * @DateTime 2023-05-9, 周二, 10:53
     
     */
    override fun setChipTopLeftCornerRadius(chipCornerRadius: Float) {
        chipCellDrawable?.setChipTopLeftCornerRadius(chipCornerRadius)
    }

    /**
     * @param chipCornerRadius
     * @Description: 设置左下角圆角
     * @DateTime 2023-05-9, 周二, 10:53
     */
    override fun setChipTopRightCornerRadius(chipCornerRadius: Float) {
        chipCellDrawable?.setChipTopRightCornerRadius(chipCornerRadius)
    }

    /**
     * @param chipCornerRadius
     * @Description: 设置左下角圆角
     * @DateTime 2023-05-9, 周二, 10:53
     
     */
    override fun setChipBottomLeftCornerRadius(chipCornerRadius: Float) {
        chipCellDrawable?.setChipBottomLeftCornerRadius(chipCornerRadius)
    }

    /**
     * @param chipCornerRadius
     * @Description: 设置右下角圆角
     * @DateTime 2023-05-9, 周二, 10:53
     
     */
    override fun setChipBottomRightCornerRadius(chipCornerRadius: Float) {
        chipCellDrawable?.setChipBottomRightCornerRadius(chipCornerRadius)
    }

    /**
     * 设置边框颜色
     *
     * @param color 边框色
     */
    override fun setChipStrokeColor(color: Int) {
        chipCellDrawable?.setChipStrokeColor(color)
    }

    /**
     * 设置边框颜色
     * 会根据状态进行修改
     *
     * @param state
     * @param color 边框色
     */
    override fun setChipStrokeColor(state: Array<IntArray>, color: IntArray) {
        chipCellDrawable?.setChipStrokeColor(state, color)
    }

    /**
     * 设置边框颜色
     * 会根据状态进行修改
     *
     * @param strokeColor 边框色
     */
    override fun setChipStrokeColor(strokeColor: ColorStateList?) {
        chipCellDrawable?.setChipStrokeColor(strokeColor)
    }

    /**
     * 添加边框颜色状态色
     * 会根据状态进行修改
     *
     * @param state       状态数组
     * @param strokeColor 边框色
     */
    override fun addChipStrokeStateColor(state: IntArray, strokeColor: Int) {
        chipCellDrawable?.addChipStrokeStateColor(state, strokeColor)
    }

    /**
     * 圆角角度
     *
     * @param chipCornerRadius 圆角
     */
    override fun setChipCornerRadius(chipCornerRadius: Float) {
        chipCellDrawable?.setChipCornerRadius(chipCornerRadius)
    }

    /**
     * 边框宽度
     *
     * @param chipStrokeWidth 边框宽度
     */
    override fun setChipStrokeWidth(chipStrokeWidth: Float) {
        chipCellDrawable?.setChipStrokeWidth(chipStrokeWidth)
    }

    /**
     * 设置背景颜色
     *
     * @param color 背景色
     */
    override fun setChipBackgroundColor(color: Int) {
        chipCellDrawable?.setChipBackgroundColor(color)
    }

    /**
     * 设置背景颜色
     * 会根据状态进行修改
     *
     * @param backgroundColor 边框色
     */
    override fun setChipBackgroundColor(backgroundColor: ColorStateList?) {
        chipCellDrawable?.setChipBackgroundColor(backgroundColor)
    }

    /**
     * 设置背景颜色
     * 会根据状态进行修改
     *
     * @param state 状态数组
     * @param color 背景色
     */
    override fun setChipBackgroundStateColor(state: Array<IntArray>, color: IntArray) {
        chipCellDrawable?.setChipBackgroundStateColor(state, color)
    }

    /**
     * 添加背景颜色状态色
     * 会根据状态进行修改
     *
     * @param state 状态数组
     * @param color 背景色
     */
    override fun addChipBackgroundStateColor(state: IntArray, color: Int) {
        chipCellDrawable?.addChipBackgroundStateColor(state, color)
    }

    /**
     * 设置背景开始颜色 渐变时生效
     *
     * @param color 背景开始色
     */
    override fun setChipBackgroundStartColor(color: Int) {
        chipCellDrawable?.setChipBackgroundStartColor(color)
    }

    /**
     * 设置背景开始颜色 渐变时生效
     * 会根据状态进行修改
     *
     * @param backgroundColor 边框色
     */
    override fun setChipBackgroundStartColor(backgroundColor: ColorStateList?) {
        chipCellDrawable?.setChipBackgroundStartColor(backgroundColor)
    }

    /**
     * 设置背景开始颜色 渐变时生效
     * 会根据状态进行修改
     *
     * @param state 状态数组
     * @param color 背景色
     */
    override fun setChipBackgroundStateStartColor(state: Array<IntArray>, color: IntArray) {
        chipCellDrawable?.setChipBackgroundStateStartColor(state, color)
    }

    /**
     * 添加背景开始颜色 渐变时生效
     * 会根据状态进行修改
     *
     * @param state 状态数组
     * @param color 背景色
     */
    override fun addChipBackgroundStateStartColor(state: IntArray, color: Int) {
        chipCellDrawable?.addChipBackgroundStateStartColor(state, color)
    }

    /**
     * 设置背景结束颜色 渐变时生效
     *
     * @param color 背景开始色
     */
    override fun setChipBackgroundEndColor(color: Int) {
        chipCellDrawable?.setChipBackgroundEndColor(color)
    }

    /**
     * 设置背景结束颜色 渐变时生效
     * 会根据状态进行修改
     *
     * @param backgroundColor 边框色
     */
    override fun setChipBackgroundEndColor(backgroundColor: ColorStateList?) {
        chipCellDrawable?.setChipBackgroundEndColor(backgroundColor)
    }

    /**
     * 设置背景结束颜色 渐变时生效
     * 会根据状态进行修改
     *
     * @param state 状态数组
     * @param color 背景色
     */
    override fun setChipBackgroundStateEndColor(state: Array<IntArray>, color: IntArray) {
        chipCellDrawable?.setChipBackgroundStateEndColor(state, color)
    }

    /**
     * 添加背景结束颜色 渐变时生效
     * 会根据状态进行修改
     *
     * @param state 状态数组
     * @param color 背景色
     */
    override fun addChipBackgroundStateEndColor(state: IntArray, color: Int) {
        chipCellDrawable?.addChipBackgroundStateEndColor(state, color)
    }

    override fun setChipBackgroundGradientAngle(angle: Int) {
        chipCellDrawable?.setChipBackgroundGradientAngle(angle)
    }
}