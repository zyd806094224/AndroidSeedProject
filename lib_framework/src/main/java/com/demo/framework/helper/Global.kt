package com.demo.framework.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.demo.framework.ext.dataStore

/**
 * 获取DataStore实例。
 */
val dataStore: DataStore<Preferences> = AppHelper.getApplication().dataStore