package com.tutorials.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tutorials.myapplication.data.dbModel.PlaceDbModel
import io.reactivex.Observable

@Dao
interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlace(placeDbModel: PlaceDbModel)

    @Query("SELECT * FROM place_table")
    fun getPlaces(): Observable<List<PlaceDbModel>>

    @Query("UPDATE place_table SET  title = :titlePlace,image=:image, description = :description,  location = :location, date = :date WHERE idPlace=:id")
    fun updatePlace(id: Int, titlePlace: String, image: String, description: String, location: String, date: String)
}

//, image=image, description=description,  location=location, date=date"