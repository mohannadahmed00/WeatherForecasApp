package com.giraffe.weatherforecasapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.giraffe.weatherforecasapplication.model.ForecastModel

@Database(entities = [ForecastModel::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getFavoritesDao(): FavoritesDao
    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null
        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDataBase::class.java, "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}