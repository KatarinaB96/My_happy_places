package com.tutorials.myapplication.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tutorials.myapplication.base.domain.PlaceRepository
import com.tutorials.myapplication.ui.Place
import com.tutorials.myapplication.ui.PlaceScreenModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PlaceViewModelImpl(private val repository: PlaceRepository) : PlaceViewModel, ViewModel() {
    override fun addPlaceToDatabase(place: Place) {
        CompositeDisposable(
            repository.addPlaceToDatabase(place)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                }, { it.printStackTrace() })
        )
    }

    override val places: MutableLiveData<List<PlaceScreenModel>> = MutableLiveData()

    override fun getPlaces(): CompositeDisposable {
        return CompositeDisposable(
            repository.getPlaces()
                .map {
                    it.map { PlaceScreenModel(it.id, it.title, it.description, it.date, it.location, it.image) }
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ onGetPlacesSuccess(it) }, { it.printStackTrace() })

        )
    }

    private fun onGetPlacesSuccess(placeScreenModel: List<PlaceScreenModel>) {
        places.postValue(placeScreenModel)
    }

    override fun updatePlace(id: Int, titlePlace: String, image: String, description: String, location: String, date: String) {
        CompositeDisposable(
            repository.updatePlace(id, titlePlace, image, description, location, date)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe()
        )
    }
}


