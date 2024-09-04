package com.demo.room.manager

import androidx.lifecycle.LiveData
import com.demo.room.database.DemoDataBase
import com.demo.room.entity.DemoDataInfo
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * @Description:
 * @Date: 2024/9/4 15:56
 * @author:  zhaoyudong
 * @version: 1.0
 */
object DemoDataManager {

    private val demoDataDao by lazy { DemoDataBase.getInstance().demoDataDao() }

    /**
     * 保存列表数据
     */
    suspend fun saveDemoDataInfoList(list: MutableList<DemoDataInfo>) {
        demoDataDao.insertAll(list)
    }

    /**
     * 插入一个数据
     * @param demoDataInfo
     */
    fun insertDemoDataInfo(demoDataInfo: DemoDataInfo) {
        MainScope().launch {
            demoDataDao.insert(demoDataInfo)
        }
    }

    /**
     * 根据id删除Item
     * @param id
     */
    fun deleteDemoDataItem(id: Long) {
        MainScope().launch {
            demoDataDao.deleteById(id)
        }
    }

    /**
     * 根据videoInfo删除Item
     * @param demoDataInfo
     */
    fun deleteDemoDataItem(demoDataInfo: DemoDataInfo) {
        MainScope().launch {
            demoDataDao.delete(demoDataInfo)
        }
    }

    /**
     * 根据videoInfo更新Item
     * @param demoDataInfo
     */
    fun updateDemoDataItem(demoDataInfo: DemoDataInfo) {
        MainScope().launch {
            demoDataDao.update(demoDataInfo)
        }
    }

    /**
     * 根据id更新title
     * @param id
     * @param title
     */
    fun updateDemoDataItem(id: Long, title: String) {
        MainScope().launch {
            demoDataDao.updateById(id, title)
        }
    }

    /**
     * 根据id获取Item
     * @param id
     */
    fun getDemoDataItem(id: Long): DemoDataInfo? {
        return demoDataDao.query(id)
    }

    /**
     * 获取列表
     */
    fun getDemoDataList(): MutableList<DemoDataInfo>? {
        return demoDataDao.queryAll()
    }

    /**
     * 获取列表
     * @return MutableLiveData
     */
    fun getDemoDataListLiveData(): LiveData<List<DemoDataInfo>> {
        return demoDataDao.queryAllLiveData()
    }

    /**
     * 清除列表缓存
     * @param callBack
     */
    fun clearDemoDataList(callBack: (String) -> Unit) {
        MainScope().launch {
            demoDataDao.deleteAll()
            callBack("删除成功")
        }
    }
}