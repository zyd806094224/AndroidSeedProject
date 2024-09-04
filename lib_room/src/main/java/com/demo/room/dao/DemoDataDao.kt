package com.demo.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.demo.room.constant.TABLE_DEMO_DATA
import com.demo.room.entity.DemoDataInfo

/**
 * @Description:
 * @Date: 2024/9/4 14:59
 * @author:  zhaoyudong
 * @version: 1.0
 */
@Dao
interface DemoDataDao {

    /**
     * 插入单个数据
     * entity操作的表，OnConflictStrategy冲突策略，
     * ABORT:终止本次操作
     * IGNORE:忽略本次操作，也终止
     * REPLACE:覆盖老数据
     */
    @Insert(entity = DemoDataInfo::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(demoDataInfo: DemoDataInfo) //这条DemoDataInfo对象新的数据，id已经存在了这个表当中，此时就会发生冲突

    /**
     * 插入多个数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(demoDataInfoList: MutableList<DemoDataInfo>)

    /**
     * 根据id删除数据
     */
    @Query("DELETE FROM $TABLE_DEMO_DATA WHERE id=:id")
    fun deleteById(id: Long)

    /**
     * 删除指定item
     * 使用主键将传递的实体实例与数据库中的行进行匹配。如果没有具有相同主键的行，则不会进行任何更改
     */
    @Delete
    fun delete(demoDataInfo: DemoDataInfo): Int

    /**
     * 删除表中所有数据
     */
    @Query("DELETE FROM $TABLE_DEMO_DATA")
    suspend fun deleteAll()

    /**
     * 更新某个item
     * 不指定的entity也可以，会根据你传入的参数对象来找到你要操作的那张表
     */
    @Update
    fun update(dataInfo: DemoDataInfo): Int


    /**
     * 根据id更新数据
     */
    @Query("UPDATE $TABLE_DEMO_DATA SET title=:title WHERE id=:id")
    fun updateById(id: Long, title: String)

    /**
     * 查询所有数据
     */
    @Query("SELECT * FROM $TABLE_DEMO_DATA")
    fun queryAll(): MutableList<DemoDataInfo>?

    /**
     * 根据id查询某个数据
     */
    @Query("SELECT * FROM $TABLE_DEMO_DATA WHERE id=:id")
    fun query(id: Long): DemoDataInfo?

    /**
     * 通过LiveData以观察者的形式获取数据库数据，可以避免不必要的NPE，
     * 可以监听数据库表中的数据的变化，也可以和RXJava的Observer使用
     * 一旦发生了insert，update，delete，room会自动读取表中最新的数据，发送给UI层，刷新页面
     * 不要使用MutableLiveData和suspend 会报错
     */
    @Query("SELECT * FROM $TABLE_DEMO_DATA")
    fun queryAllLiveData(): LiveData<List<DemoDataInfo>>

}