package com.tutorials.myapplication.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.tutorials.myapplication.ui.Place
import com.tutorials.myapplication.ui.PlaceScreenModel
import io.reactivex.disposables.CompositeDisposable

interface PlaceViewModel {
    fun addPlaceToDatabase(place: Place)

    fun getPlaces(): CompositeDisposable

    val places: MutableLiveData<List<PlaceScreenModel>>

    fun updatePlace(id: Int, titlePlace: String, image: String, description: String, location: String, date: String)
}