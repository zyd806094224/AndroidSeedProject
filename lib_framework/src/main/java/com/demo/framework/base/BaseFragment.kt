package com.demo.framework.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.demo.framework.R
import com.demo.framework.utils.LoadingUtils

/**
 * @Description:
 * @Date: 2024/8/29 16:00
 * @author:  zhaoyudong
 * @version: 1.0
 */
abstract class BaseFragment : Fragment() {

    protected var TAG: String? = this::class.java.simpleName

    protected var mIsViewCreate = false

    private val dialogUtils by lazy {
        LoadingUtils(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return getContentView(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mIsViewCreate = true
        initView(view, savedInstanceState)
        initData()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        //手动切换首页tab时，先调用此方法设置fragment的可见性
        if (mIsViewCreate) {
            onFragmentVisible(isVisibleToUser)
        }
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint) {
            onFragmentVisible(true)
        }
    }

    override fun onStop() {
        super.onStop()
        if (userVisibleHint) {
            onFragmentVisible(false)
        }
    }

    open fun onFragmentVisible(isVisibleToUser: Boolean) {

    }

    /**
     * 设置布局View
     */
    open fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(getLayoutResId(), null)
    }

    /**
     * 初始化视图
     * @return Int 布局id
     * 仅用于不继承BaseDataBindFragment类的传递布局文件
     */
    abstract fun getLayoutResId(): Int

    /**
     * 初始化布局
     * @param savedInstanceState Bundle?
     */
    abstract fun initView(view: View, savedInstanceState: Bundle?)

    /**
     * 初始化数据
     */
    open fun initData() {}

    /**
     * 加载中……弹框
     */
    fun showLoading() {
        showLoading(getString(R.string.default_loading))
    }

    /**
     * 加载提示框
     */
    fun showLoading(msg: String?) {
        dialogUtils.showLoading(msg)
    }

    /**
     * 加载提示框
     */
    fun showLoading(@StringRes res: Int) {
        showLoading(getString(res))
    }

    /**
     * 关闭提示框
     */
    fun dismissLoading() {
        dialogUtils.dismissLoading()
    }
}