package com.demo.framework.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * @Description:
 * @Date: 2024/8/29 16:31
 * @author:  zhaoyudong
 * @version: 1.0
 */
abstract class BaseMvvmActivity<DB : ViewBinding, VM : ViewModel> : BaseDataBindActivity<DB>() {

    lateinit var mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        initViewModel()
        super.onCreate(savedInstanceState)
    }

    private fun initViewModel() {
        val argument = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        val viewModelClass = argument[1]

        // 添加类型安全检查
        if (viewModelClass is Class<*> && ViewModel::class.java.isAssignableFrom(viewModelClass)) {
            @Suppress("UNCHECKED_CAST")
            mViewModel = ViewModelProvider(this).get(viewModelClass as Class<VM>)
        } else {
            throw IllegalArgumentException("ViewModel type must be a subclass of ViewModel, got: $viewModelClass")
        }
    }
}