package com.demo.main.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.demo.main.repository.HomeRepository
import com.demo.network.flow.requestFlow
import com.demo.network.manager.ApiManager
import com.demo.network.viewmodel.BaseViewModel
import com.demo.room.entity.DemoDataInfo
import com.demo.room.manager.DemoDataManager
import kotlinx.coroutines.launch

/**
 * @Description:
 * @Date: 2024/9/3 17:21
 * @author:  zhaoyudong
 * @version: 1.0
 */
class HomeViewModel : BaseViewModel() {

    private val dataListLiveData = MutableLiveData<MutableList<String>?>()

    private val homeRepository by lazy { HomeRepository() }


    fun getDataList(): LiveData<MutableList<String>?> {
        viewModelScope.launch {
            //通过flow来请求
            val data = requestFlow(requestCall = {
                ApiManager.api.getDataList()
            }, errorBlock = { code, error ->
                dataListLiveData.value = null
            })
            dataListLiveData.value = data
        }
        return dataListLiveData
    }

    fun getDataList2(): LiveData<MutableList<String>?> {
        return liveData {
            val response = safeApiCall(errorBlock = { code, errorMsg ->
                //error
            }) {
                homeRepository.getDataList()
            }
            emit(response)
        }
    }

    fun getDataList3(): LiveData<MutableList<DemoDataInfo>?> {
        return liveData {
            val response = safeApiCall(errorBlock = { code, errorMsg ->

            }) {
                var list = homeRepository.getDemoDataListCache()
                //缓存为空则创建
                if (list.isNullOrEmpty()) {
                    list = mutableListOf()
                    val demoDataInfo = DemoDataInfo()
                    list.add(demoDataInfo)
                    DemoDataManager.saveDemoDataInfoList(list)
                }
                list
            }
            emit(response)
        }
    }


}