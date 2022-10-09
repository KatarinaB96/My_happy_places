package com.tutorials.myapplication.base.domain

import androidx.lifecycle.MutableLiveData
import com.tutorials.myapplication.data.Crudder
import com.tutorials.myapplication.ui.Place
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class PlaceRepositoryImpl(private val crudder: Crudder) : PlaceRepository {
    override fun addPlaceToDatabase(list: Place): Completable {
        return crudder.addPlaceToDatabase(list)
    }

    override fun getPlaces(): Observable<List<Place>> {
        return crudder.getPlaces()
    }

    override fun updatePlace(id: Int, titlePlace: String, image: String, description: String, location: String, date: String): Completable {
        return crudder.updatePlace(id, titlePlace, image, description, location, date)
    }

}