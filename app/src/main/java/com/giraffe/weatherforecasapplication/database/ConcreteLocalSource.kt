package com.giraffe.weatherforecasapplication.database

import android.content.Context

class ConcreteLocalSource (context: Context) : LocalSource {
    private val dao: FavoritesDao = AppDataBase.getInstance(context).getFavoritesDao()
}