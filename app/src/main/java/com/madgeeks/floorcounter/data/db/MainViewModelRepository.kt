package com.madgeeks.floorcounter.data.db

import androidx.lifecycle.LiveData

interface MainViewModelRepository {
    fun getAllHours(): LiveData<List<Hour>>
    fun getHours(date: String): LiveData<List<Hour>>
    suspend fun insert(hour: Hour)
    suspend fun insertOrReplace(date: String, floors: Float)
    suspend fun delete(hour: Hour)
}

class MainViewModelRepositoryImpl(private val hourDao: HourDao): MainViewModelRepository {
    override fun getAllHours(): LiveData<List<Hour>> = hourDao.getHours()
    override fun getHours(date: String): LiveData<List<Hour>> = hourDao.getAllHours("%$date%")
    override suspend fun insert(hour: Hour) = hourDao.insert(hour)
    override suspend fun insertOrReplace(date: String, floors: Float) = hourDao.insertOrUpdate(date, floors)
    override suspend fun delete(hour: Hour) = hourDao.delete(hour)
}