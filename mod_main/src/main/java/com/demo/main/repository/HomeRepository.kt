package com.demo.main.repository

import com.demo.network.manager.ApiManager
import com.demo.network.repository.BaseRepository
import com.demo.room.entity.DemoDataInfo
import com.demo.room.manager.DemoDataManager

/**
 * @Description:
 * @Date: 2024/9/4 11:02
 * @author:  zhaoyudong
 * @version: 1.0
 */
class HomeRepository : BaseRepository() {

    suspend fun getDataList(): MutableList<String>? {
        return requestResponse {
            ApiManager.api.getDataList()
        }
    }

    suspend fun getDemoDataListCache(): MutableList<DemoDataInfo>? {
        return DemoDataManager.getDemoDataList()
    }

}