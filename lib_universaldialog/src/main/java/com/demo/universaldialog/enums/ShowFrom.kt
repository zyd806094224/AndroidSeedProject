package com.demo.universaldialog.enums

/**
 * 弹窗动画显示出现方向，消失则是反方向
 */
enum class ShowFrom {
    /**
     * 默认值，普通的弹窗则是系统动画，悬浮弹窗则没有动画
     */
    DEFAULT,
    /**
     * 从底部出现
     */
    BOTTOM,
    /**
     * 从顶部出现
     */
    TOP,
    /**
     * 从左边部出现
     */
    LEFT,
    /**
     * 从右边出现
     */
    RIGHT
}