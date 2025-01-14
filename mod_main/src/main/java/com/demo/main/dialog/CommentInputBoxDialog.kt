package com.demo.main.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.demo.main.R

/**
 * @Description:
 * @Date: 2025/1/14 14:35
 * @author:  zhaoyudong
 */
class CommentInputBoxDialog : DialogFragment() {

    private var commentInputBoxView: View? = null
    private var hint: String? = null
    private var inputBoxListener: IInputBoxListener? = null

    fun setInputBoxListener(listener: IInputBoxListener): CommentInputBoxDialog {
        inputBoxListener = listener
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        commentInputBoxView = layoutInflater.inflate(R.layout.custom_input_box, null)
        return commentInputBoxView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeKeyboardHeight()
        val editText = commentInputBoxView?.findViewById<EditText>(R.id.customEditText)
        val iv_send = commentInputBoxView?.findViewById<ImageView>(R.id.iv_send)
//        iv_send?.setImageURL("https://fhlui1001.58wos.com.cn/cDazYxWcDHJ/picasso/4lb1d2is__w196_h196.png")
        if (!TextUtils.isEmpty(hint)) {
            editText?.hint = hint
        }
        editText?.requestFocus()
        iv_send?.setOnClickListener {
            inputBoxListener?.onSendClick(editText?.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        //背景透明
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
//        dialog?.window?.setWindowAnimations(R.style.hy_xhs_comment_bottom_dialog_anim_style)
        dialog?.window?.attributes?.dimAmount = 0.2f
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true) //默认点击外部区域 弹窗消失
        dialog?.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                super.dismissAllowingStateLoss()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        setLayoutParams()
    }

    private var popupWindowY = 0
    private fun observeKeyboardHeight() {
        commentInputBoxView?.viewTreeObserver?.addOnGlobalLayoutListener {
            val location = IntArray(2)
            commentInputBoxView?.getLocationOnScreen(location) //相对屏幕的位置坐标
            val x = location[0] // X 坐标
            val y = location[1] // Y 坐标
            Log.e("zzz", "x=${x}==y=${y}")
            if (popupWindowY != 0 && y > popupWindowY) {
                super.dismissAllowingStateLoss()
            }
            popupWindowY = y
        }
    }

    private fun setLayoutParams() {
        val lp = dialog?.window?.attributes
        lp?.let {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.BOTTOM
            dialog?.window?.attributes = lp
        }
    }

    fun showInputDialog(fragmentManager: FragmentManager, hint: String?, tag: String) {
        this.hint = hint
        //避免重复添加的异常 java.lang.IllegalStateException: Fragment already added
        val fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.remove(fragment)
            fragmentTransaction.commitAllowingStateLoss()
        }
        //避免状态丢失的异常 java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        try {
            super.show(fragmentManager, tag)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun dismissInputDialog() {
        super.dismissAllowingStateLoss()
    }

    interface IInputBoxListener {
        fun onSendClick(content: String?)
    }

}