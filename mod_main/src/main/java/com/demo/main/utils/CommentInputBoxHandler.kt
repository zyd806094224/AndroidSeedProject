package com.demo.main.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import com.demo.main.R

/**
 * @Description: 评论输入框处理器
 * @Date: 2025/1/3 14:25
 * @author:  zhaoyudong
 * @version: 1.0
 */
class CommentInputBoxHandler(val activity: Activity?, private val hint: String? = "") {

    private var popupWindow: PopupWindow? = null
    private var commentInputBoxView: View? = null
    private var inputBoxListener: IInputBoxListener? = null

    fun setInputBoxListener(listener: IInputBoxListener) {
        inputBoxListener = listener
    }

    fun showKeyboard() {
        commentInputBoxView =
            activity?.layoutInflater?.inflate(R.layout.custom_input_box, null)
        val editText = commentInputBoxView?.findViewById<EditText>(R.id.customEditText)
        val iv_send = commentInputBoxView?.findViewById<ImageView>(R.id.iv_send)
//        iv_send?.setImageURL("https://fhlui1001.58wos.com.cn/cDazYxWcDHJ/picasso/4lb1d2is__w196_h196.png")
        editText?.hint = hint
        editText?.requestFocus()
        popupWindow = PopupWindow(
            commentInputBoxView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow?.isFocusable = true
        popupWindow?.isTouchable = true
        observeKeyboardHeight()
        backgroundAlpha(activity, 0.8f)
        val rootView = activity?.window?.decorView?.findViewById<View>(android.R.id.content)
        popupWindow?.showAtLocation(rootView, Gravity.BOTTOM, 0, 0)
        commentInputBoxView?.postDelayed({
            // 请求键盘弹出
            editText?.requestFocus()
            val inputMethodManager = activity?.applicationContext?.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, 200)

        popupWindow?.setOnDismissListener {
            backgroundAlpha(activity, 1f)
        }

        editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { //物理返回键
                // 键盘已收起
                dismissKeyboard()
            }
        }

        iv_send?.setOnClickListener {
            inputBoxListener?.onSendClick(editText?.text.toString())
        }
    }

    private fun backgroundAlpha(activity: Activity?, bgAlpha: Float) {
        val lp: WindowManager.LayoutParams? = activity?.window?.attributes
        lp?.alpha = bgAlpha //0.0-1.0
        activity?.window?.setAttributes(lp)
    }

    private var popupWindowBottom = 0
    private fun observeKeyboardHeight() {
        commentInputBoxView?.viewTreeObserver?.addOnGlobalLayoutListener {
            // 在这里监听布局变化
            val r = Rect()
            commentInputBoxView?.getWindowVisibleDisplayFrame(r) // 获取当前窗口可见的部分
            Log.e("zzz", "bootom${r.bottom}")
            Log.e("zzz", "top${r.top}")
            if (popupWindowBottom != 0 && r.bottom > popupWindowBottom) {//收起
                Log.e("zzz", "键盘收起")
                dismissKeyboard()
            }
            popupWindowBottom = r.bottom
        }
    }

    fun dismissKeyboard() {
        popupWindowBottom = 0
        popupWindow?.dismiss()
        popupWindow = null
    }

    interface IInputBoxListener {
        fun onSendClick(content: String?)
    }
}