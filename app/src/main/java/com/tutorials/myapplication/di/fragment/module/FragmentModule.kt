package com.tutorials.myapplication.di.fragment.module

import androidx.fragment.app.Fragment
import com.tutorials.myapplication.base.BaseFragment
import dagger.Module
import dagger.Provides


@Module
class FragmentModule(private val fragment: BaseFragment) {

    @Provides
    internal fun provideFragment(): Fragment {
        return fragment
    }

    interface Exposes {
       fun fragment(): Fragment
    }
}