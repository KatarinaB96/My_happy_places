package com.tutorials.myapplication.di.application.module

import android.content.Context
import androidx.room.Room
import com.tutorials.myapplication.base.TemplateApplication
import com.tutorials.myapplication.data.Crudder
import com.tutorials.myapplication.data.CrudderImpl
import com.tutorials.myapplication.data.PlaceDao
import com.tutorials.myapplication.data.PlaceDatabase
import com.tutorials.myapplication.ui.Place
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val templateApplication: TemplateApplication) {

    @Provides
    @Singleton
    internal fun provideTemplateApplication(): TemplateApplication {
        return templateApplication
    }

    @Provides
    @Singleton
    internal fun provideContext(): Context {
        return templateApplication
    }

    @Provides
    @Singleton
    internal fun provideCrudder(
        placeDao: PlaceDao
    ): Crudder {
        return CrudderImpl(placeDao)
    }

    @Provides
    @Singleton
    internal fun provideDao(placeDatabase: PlaceDatabase): PlaceDao {
        return placeDatabase.placeDao()
    }

    @Provides
    @Singleton
    internal fun provideDatabase(context: Context): PlaceDatabase {
        return Room.databaseBuilder(
            context,
            PlaceDatabase::class.java, "place_database"
        ).build()
    }

    interface Exposes {
        //        fun templateApplication(): TemplateApplication
        fun context(): Context
        fun crudder(): Crudder
        fun provideDatabase(): PlaceDatabase

    }
}