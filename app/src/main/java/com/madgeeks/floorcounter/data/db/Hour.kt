package com.madgeeks.floorcounter.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hours")
data class Hour (
    @PrimaryKey var date: String,
    @ColumnInfo(name = "floors") var floors: Float
)