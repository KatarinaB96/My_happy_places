package com.tutorials.myapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tutorials.myapplication.data.dbModel.PlaceDbModel

@Database(entities = [PlaceDbModel::class], version = 1, exportSchema = false)
abstract class PlaceDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
}