package com.demo.framework.ext

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.demo.framework.helper.AppHelper

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = AppHelper.getApplication().packageName + "_preferences",
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, AppHelper.getApplication().packageName + "_preferences"))
    })