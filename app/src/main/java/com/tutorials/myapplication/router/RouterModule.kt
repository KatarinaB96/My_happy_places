package com.tutorials.myapplication.router

import androidx.fragment.app.FragmentManager
import dagger.Module
import dagger.Provides

@Module
class RouterModule {
    @Provides
    internal fun provideRouter(fragmentManager: FragmentManager):Router{
        return RouterImpl(fragmentManager)
    }

    interface Exposes{
        fun router():Router
    }
}