package com.tutorials.myapplication.di.application.module

import com.google.gson.Gson
import com.tutorials.myapplication.base.domain.PlaceRepository
import com.tutorials.myapplication.base.domain.PlaceRepositoryImpl
import com.tutorials.myapplication.data.Crudder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    //    @Singleton
    //    @Provides
    //    internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
    //        val interceptor = HttpLoggingInterceptor()
    //        interceptor.level = HttpLoggingInterceptor.Level.BODY
    //
    //        return interceptor
    //    }

    //    @Provides
    //    @Singleton
    //    internal fun provideOkhttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
    //        val builder = OkHttpClient.Builder()
    //            .addInterceptor(loggingInterceptor)
    //
    //        return builder.build()
    //    }

    //
    //    @Provides
    //    @Singleton
    //    internal fun provideNetworkService(retrofit: Retrofit): NetworkService =
    //        retrofit.create(NetworkService::class.java)

    //    @Provides
    //    @Singleton
    //    internal fun provideClient(
    //        networkService: NetworkService,
    //        apiMapper: ApiMapper,
    //        sharedPreferences: DeviceSharedPreferences,
    //        sessionCookieRequestApi: SessionCookieRequestApi
    //    ): CourseCategoryClient {
    //        return CourseCategoryClientImpl(apiMapper, networkService, sharedPreferences,sessionCookieRequestApi )
    //    }

    @Provides
    @Singleton
    internal fun provideRepository(
        crudder: Crudder
    ): PlaceRepository {
        return PlaceRepositoryImpl(crudder)
    }

    interface Exposes {

        //        fun provideClient(): Client
        fun provideRepository(): PlaceRepository

    }

}