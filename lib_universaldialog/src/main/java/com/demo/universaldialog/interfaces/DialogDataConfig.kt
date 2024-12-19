package com.demo.universaldialog.interfaces

import com.demo.universaldialog.enums.ShowFrom
import com.demo.universaldialog.enums.XLocation
import com.demo.universaldialog.enums.YLocation

/**
 * 通用弹窗显示位置、动画、展示时间、是否是悬浮窗、四个方向的Margin、以及宽高配置
 * 参考https://docs.58corp.com/#/space/1580860469965549568中的通用弹框交互字段
 */
interface DialogDataConfig {
    /**
     * 水平方向的位置,参考[YLocation]
     */
    fun getShowX():XLocation
    /**
     * 垂直方向的位置,参考[YLocation]
     */
    fun getShowY():YLocation
    /**
     * 动画从从哪个方向出现,参考[ShowFrom]
     */
    fun getShowFrom(): ShowFrom
    /**
     * 显示时长，<= 0：表示不消失，其它按照配置时长消失，单位秒
     */
    fun getShowTime():Int
    /**
     * 非悬浮弹窗的背景的透明度，范围0-1，默认0.8
     */
    fun getOpacity():Float

    /**
     * true: 普通弹窗，背景不能点击
     * false：悬浮弹窗，背景可以点击
     * 不需要悬浮窗权限，当然可以扩展成需要权限的，体验更好
     */
    fun isModal():Boolean

    /**
     * 顶部边距
     */
    fun getMarginTop():Int
    /**
     * 低部边距
     */
    fun getMarginBottom():Int
    /**
     * 左边边距
     */
    fun getMarginLeft():Int
    /**
     * 右边边距
     */
    fun getMarginRight():Int
    /**
     * 弹窗宽度，不包含边距，几种方式
     * 1.百分百：0%-100% 去掉边距过后剩余宽度所占比值
     * 2.固定值，如100，换算成单位是dp
     * 3.空则是根据实际所占宽度设置，相当于wrap_content
     */
    fun getContentWidth():String?
    /**
     * 弹窗宽度，不包含边距，几种方式
     * 1.百分百：0%-100% 去掉边距和状态栏过后剩余高度所占比值
     * 2.固定值，如100，换算成单位是dp
     * 3.空则是根据实际所占高度设置，相当于wrap_content
     */
    fun getContentHeight():String?

    /**
     * 是否有背景，背景边缘有阴影，会占用10dp长宽
     */
    fun isCardBackground():Boolean

    /**
     * 当悬浮弹窗模式下，是不是全局都可以看到，是则全局展现，否则当前页面展示
     */
    fun isGlobal():Boolean

    /**
     * 是否能滑动关闭
     */
    fun canSlideClose():Boolean

    /**
     * 是否使用系统悬浮窗
     */
    fun isUseSystemFloatingWindow(): Boolean
}