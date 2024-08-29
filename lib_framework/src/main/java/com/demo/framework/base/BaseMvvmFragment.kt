package com.demo.framework.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

/**
 * @Description:
 * @Date: 2024/8/29 16:38
 * @author:  zhaoyudong
 * @version: 1.0
 */
abstract class BaseMvvmFragment<DB : ViewDataBinding, VM : ViewModel> : BaseDataBindFragment<DB>() {

    lateinit var mViewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewModel()
        super.onViewCreated(view, savedInstanceState)
    }

    open fun initViewModel() {
        val argument = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        mViewModel = ViewModelProvider(this).get(argument[1] as Class<VM>)
    }

}