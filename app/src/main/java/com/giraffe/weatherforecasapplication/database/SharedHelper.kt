package com.giraffe.weatherforecasapplication.database

import android.content.Context
import android.content.SharedPreferences

class SharedHelper {
    companion object {
        private var INSTANCE: SharedHelper? = null
        private var shared: SharedPreferences? = null
        fun getInstance(context: Context): SharedHelper {
            return INSTANCE ?: synchronized(this) {
                val  instance = SharedHelper()
                shared = context.getSharedPreferences("local_shared", Context.MODE_PRIVATE)
                INSTANCE = instance
                instance
            }
        }
    }

    fun store(key: String, value: String?) {
        val editor = shared!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun read(key: String): String? {
        return shared!!.getString(key.toString(), null)
    }
}