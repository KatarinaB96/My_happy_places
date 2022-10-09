package com.tutorials.myapplication.data

import com.tutorials.myapplication.data.dbModel.PlaceDbModel
import com.tutorials.myapplication.ui.Place
import io.reactivex.Completable
import io.reactivex.Observable

class CrudderImpl(private val placeDao: PlaceDao) : Crudder {

    override fun addPlaceToDatabase(place: Place): Completable {
        return Completable.fromAction {
            placeDao.addPlace(PlaceDbModel(0, place.title, place.description, place.date, place.location, place.image ?: ""))
        }
    }

    override fun getPlaces(): Observable<List<Place>> {
        return placeDao.getPlaces().map {
            it.map { Place(it.idPlace, it.title, it.description, it.date, it.location, it.image) }
        }
    }

    override fun updatePlace(id: Int, titlePlace: String, image: String, description: String, location: String, date: String): Completable {
        return Completable.fromAction {
            placeDao.updatePlace(id, titlePlace, image, description, location, date)
        }
    }
}