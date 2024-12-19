package com.demo.universaldialog.impl

import com.demo.universaldialog.enums.ShowFrom
import com.demo.universaldialog.enums.XLocation
import com.demo.universaldialog.enums.YLocation
import com.demo.universaldialog.interfaces.DialogDataConfig

/**
 * 样例配置，各个含义查看[DialogDataConfig]
 */
open class SimpleDialogDataConfig : DialogDataConfig, java.io.Serializable {

    private var isModal = true
    var height : String?="200"
    var width : String?="80%"
    private var marginBottom = 0
    private var marginTop = 0
    private var marginRight = 0
    private var marginLeft = 0
    private var opacity = 0.8f
    private var showFrom = ShowFrom.DEFAULT
    private var showTime = 0
    private var showX = XLocation.CENTER
    private var showY = YLocation.CENTER
    private var isCardBackground = false
    private var isGlobal = false
    private var canSlideClose = false

    override fun getContentHeight(): String? {
        return height
    }

    fun setContentHeight(height:String?) {
        this.height = height
    }

    override fun getContentWidth(): String? {
        return width
    }

    fun setContentWidth(width:String?) {
        this.width = width
    }

    override fun getMarginBottom(): Int {
        return marginBottom
    }

    fun setMarginBottom(marginBottom:Int) {
        this.marginBottom = marginBottom
    }

    override fun getMarginLeft(): Int {
        return marginLeft
    }

    fun setMarginLeft(marginLeft:Int) {
        this.marginLeft = marginLeft
    }

    override fun getMarginRight(): Int {
        return marginRight
    }

    fun setMarginRight(marginRight:Int) {
        this.marginRight = marginRight
    }

    override fun getMarginTop(): Int {
        return marginTop
    }

    fun setMarginTop(marginTop:Int) {
        this.marginTop = marginTop
    }

    override fun getOpacity(): Float {
        return opacity
    }

    fun setOpacity(opacity:Float) {
        this.opacity = opacity
    }

    override fun getShowFrom(): ShowFrom {
        return showFrom
    }

    fun setShowFrom(showFrom: ShowFrom) {
        this.showFrom = showFrom
    }

    override fun getShowTime(): Int {
        return showTime
    }

    fun setShowTime(showTime:Int) {
        this.showTime = showTime
    }

    override fun getShowX(): XLocation {
        return showX
    }

    fun setShowX(showX: XLocation) {
        this.showX = showX
    }

    override fun getShowY(): YLocation {
        return showY
    }

    fun setShowY(showY: YLocation) {
        this.showY = showY
    }

    override fun isModal(): Boolean {
        return isModal
    }

    fun setIsModal(isModal:Boolean) {
        this.isModal = isModal
    }

    override fun isCardBackground() : Boolean{
        return isCardBackground
    }

    fun setIsCardBackground(isCardBackground:Boolean){
        this.isCardBackground = isCardBackground
    }

    override fun isGlobal(): Boolean {
        return isGlobal
    }

    fun setIsGlobal(isGlobal:Boolean){
        this.isGlobal = isGlobal
    }

    override fun canSlideClose(): Boolean {
        return canSlideClose
    }

    override fun isUseSystemFloatingWindow(): Boolean {
        return true
    }

    fun setCanSlideClose(canSlideClose:Boolean){
        this.canSlideClose = canSlideClose
    }

}