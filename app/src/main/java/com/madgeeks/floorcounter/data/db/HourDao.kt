package com.madgeeks.floorcounter.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HourDao {
    @Query("Select * from hours")
    fun getHours(): LiveData<List<Hour>>

    @Query("Select * from hours where date like :date")
    fun getAllHours(date: String): LiveData<List<Hour>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hour: Hour)

    @Query("INSERT OR REPLACE INTO hours VALUES(:date, coalesce((SELECT floors FROM hours WHERE date = :date), 0) + :floors)")
    suspend fun insertOrUpdate(date: String, floors: Float)

    @Delete
    suspend fun delete(hour: Hour)
}