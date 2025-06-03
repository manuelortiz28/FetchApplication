package com.manuel.fetch.di

import com.google.gson.Gson
import com.manuel.fetch.service.HiringsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
// This can be in the SingletonComponent if you want it available app-wide
@InstallIn(ActivityRetainedComponent::class)
class ServiceModule {
    @Provides
    fun provideHiringsService(gson: Gson): HiringsService {
        return Retrofit.Builder()
            .baseUrl("https://hiring.fetch.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(HiringsService::class.java)
    }

    @Provides
    fun providesGson(): Gson = Gson()
}
