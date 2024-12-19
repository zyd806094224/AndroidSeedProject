package com.demo.universaldialog

import android.app.Activity

object UniversalDialogSDK {

    private var listAct : List<String>?=null
    private var listAllowAct : List<String>? = null
    private var fontChangeListeners : ArrayList<OnFontChangeListener>?=null

    /**
     * 请求使用悬浮窗权限
     */
    var requestAlertWindowPermission : RequestAlertWindowPermission?=null

    /**
     * float弹窗不在[listAct]这些指定的类下展示
     */
    fun setFlotDialogExcludeActivity(listAct: List<String>){
        if(listAct.isNullOrEmpty()){
            this.listAct = ArrayList<String>()
        }else{
            this.listAct = listAct
        }
    }

    fun getFlotDialogExcludeActivity(): List<String>{
        if(listAct == null){
            listAct = ArrayList()
        }
        return listAct!!
    }

    /**
     * float弹窗只在[listAllowAct]这些指定的类下展示
     * **注意**：如果设置了这个，那么setFlotDialogExcludeActivity()设置的类将失效
     */
    fun setFlotDialogActivity(listAllowAct: List<String>){
        if(listAllowAct.isNullOrEmpty()){
            this.listAllowAct = ArrayList<String>()
        }else{
            this.listAllowAct = listAllowAct
        }
    }

    fun getFlotDialogActivity(): List<String>{
        if(listAllowAct == null){
            listAllowAct = ArrayList()
        }
        return listAllowAct!!
    }

    fun registerOnFountChange(listener : OnFontChangeListener?){
        if(listener == null)return
        if(fontChangeListeners == null){
            fontChangeListeners = ArrayList()
        }
        if(!fontChangeListeners!!.contains(listener)){
            fontChangeListeners!!.add(listener)
        }
    }

    fun unregisterOnFountChange(listener : OnFontChangeListener?){
        if(listener == null)return
        fontChangeListeners?.remove(listener)
    }

    /**
     * 外部通知前台切换，在使用系统全局弹窗时候，要实时remove
     */
    fun onFountChange(font: Boolean){
        if(fontChangeListeners== null)return
        for(lis in fontChangeListeners!!){
            lis.onFountChange(font)
        }
    }

    /**
     * 悬浮窗权限申请，交给接入方面处理
     */
    interface RequestAlertWindowPermission {
        fun requestPermission(activity: Activity, callBack : (Boolean) -> Unit)
    }

    /**
     * 在有权限的悬浮窗弹窗情况下，需要前后台切换监听，切换到后台弹窗隐藏，切换到前台出现
     */
    interface OnFontChangeListener {
        fun onFountChange(font: Boolean)
    }
}