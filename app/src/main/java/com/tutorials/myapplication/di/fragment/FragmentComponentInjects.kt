package com.tutorials.myapplication.di.fragment

import com.tutorials.myapplication.ui.AddPlaceFragment
import com.tutorials.myapplication.ui.DetailsFragment
import com.tutorials.myapplication.ui.HomeFragment
import com.tutorials.myapplication.ui.QRGeneratorFragment

interface FragmentComponentInjects {
    fun inject(fragment: HomeFragment)
    fun inject(fragment: AddPlaceFragment)
    fun inject(fragment: DetailsFragment)
    fun inject(fragment: QRGeneratorFragment)

}