package com.tutorials.myapplication.di.fragment.module

import androidx.lifecycle.ViewModelProvider
import com.tutorials.myapplication.base.BaseFragment
import com.tutorials.myapplication.base.domain.PlaceRepository
import com.tutorials.myapplication.ui.ViewModelProviderFactory
import com.tutorials.myapplication.ui.viewmodel.PlaceViewModel
import com.tutorials.myapplication.ui.viewmodel.PlaceViewModelImpl
import dagger.Module
import dagger.Provides

@Module
class FragmentViewModelModule(private val fragment: BaseFragment) {

    @Provides
    internal fun provideViewModelCourseCategory(
        placeRepository: PlaceRepository
    ): PlaceViewModel {
        return ViewModelProvider(fragment, ViewModelProviderFactory(PlaceViewModelImpl::class) {
            PlaceViewModelImpl(placeRepository)
        })[PlaceViewModelImpl::class.java]
    }

    interface Exposes {
        fun provideViewModel(): PlaceViewModel
    }

}