package com.giraffe.weatherforecasapplication.model.repo

import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.network.RemoteSource

class Repo private constructor(private val remoteSource: RemoteSource,private val localSource: ConcreteLocalSource):RepoInterface {
    companion object {
        @Volatile
        private var INSTANCE: Repo? = null
        fun getInstance(remoteSource: RemoteSource, localSource: ConcreteLocalSource): Repo {
            return INSTANCE ?: synchronized(this) {
                val instance = Repo(remoteSource,localSource)
                INSTANCE = instance
                instance
            }
        }
    }
}