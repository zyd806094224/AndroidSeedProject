package com.demo.framework.base

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.demo.framework.ext.saveAs
import com.demo.framework.ext.saveAsUnChecked
import java.lang.reflect.ParameterizedType

/**
 * @Description:
 * @Date: 2024/8/29 15:54
 * @author:  zhaoyudong
 * @version: 1.0
 */
abstract class BaseDataBindActivity<DB : ViewBinding> : BaseActivity() {

    lateinit var mBinding: DB

    override fun setContentLayout() {
//      mBinding = DataBindingUtil.setContentView(this, getLayoutResId())
        val type = javaClass.genericSuperclass
        val vbClass: Class<DB> = type!!.saveAs<ParameterizedType>().actualTypeArguments[0].saveAs()
        val method = vbClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        mBinding = method.invoke(this, layoutInflater)!!.saveAsUnChecked()
        setContentView(mBinding.root)
    }

    override fun getLayoutResId(): Int = 0

}