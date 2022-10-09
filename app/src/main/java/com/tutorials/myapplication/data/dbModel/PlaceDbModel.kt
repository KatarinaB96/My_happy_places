package com.tutorials.myapplication.data.dbModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place_table")
data class PlaceDbModel(
    @PrimaryKey(autoGenerate = true)
    val idPlace: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "image") val image: String

)