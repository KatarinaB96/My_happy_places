package com.tutorials.myapplication.base.domain

import com.tutorials.myapplication.ui.Place
import io.reactivex.Completable
import io.reactivex.Observable

interface PlaceRepository {
    fun addPlaceToDatabase(list: Place): Completable
    fun getPlaces(): Observable<List<Place>>

    fun updatePlace(id: Int, titlePlace: String, image: String, description: String, location: String, date: String): Completable
}