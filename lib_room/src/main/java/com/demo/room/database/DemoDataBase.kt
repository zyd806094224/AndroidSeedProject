package com.demo.room.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.demo.framework.helper.AppHelper
import com.demo.room.dao.DemoDataDao
import com.demo.room.entity.DemoDataInfo

/**
 * @Description:
 * @Date: 2024/9/4 14:56
 * @author:  zhaoyudong
 * @version: 1.0
 */
@Database(entities = [DemoDataInfo::class], version = 1, exportSchema = false)
abstract class DemoDataBase : RoomDatabase(){

    //抽象方法或者抽象类标记
    abstract fun demoDataDao(): DemoDataDao

    companion object {
        private var dataBase: DemoDataBase? = null

        //数据库版本升级
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE table_demo_data " + " ADD COLUMN bbb TEXT ")
                database.execSQL("ALTER TABLE table_demo_data " + " ADD COLUMN ccc TEXT ")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE table_demo_data " + " ADD COLUMN ddd TEXT ")
                database.execSQL("ALTER TABLE table_demo_data " + " ADD COLUMN eee INTEGER ")
            }
        }

        //同步锁，可能在多个线程中同时调用
        @Synchronized
        fun getInstance(): DemoDataBase {
            return dataBase ?: Room.databaseBuilder(AppHelper.getApplication(), DemoDataBase::class.java, "DemoData_DB")
                //是否允许在主线程查询，默认是false
                .allowMainThreadQueries()
                //数据库被创建或者被打开时的回调
                //.addCallback(callBack)
                //指定数据查询的线程池，不指定会有个默认的
                //.setQueryExecutor {  }
                //任何数据库有变更版本都需要升级，升级的同时需要指定migration，如果不指定则会报错
                //数据库升级 1-->2， 怎么升级，以什么规则升级
//                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                //设置数据库工厂，用来链接room和SQLite，可以利用自行创建SupportSQLiteOpenHelper，来实现数据库加密
                //.openHelperFactory()
                .build()
        }
    }

}