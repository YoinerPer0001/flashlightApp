package com.example.linternapro.core.preferences

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit

class preferencesManager @Inject constructor (context: Context) {
    private val sharedPreferences:SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun saveData(key:String, value:String){
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    fun getData(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
}