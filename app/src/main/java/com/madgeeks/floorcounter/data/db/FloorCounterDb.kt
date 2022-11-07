package com.madgeeks.floorcounter.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Hour::class],
    version = 2,
    exportSchema = true,
)
abstract class FloorCounterDb: RoomDatabase() {
    abstract fun hourDao(): HourDao
}