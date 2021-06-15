package com.raj.weather.di

import com.raj.weather.network.Url
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class RetrofitModule {

@Singleton
@Provides
fun provideRetrofit(): Retrofit{
    return Retrofit.Builder()
        .baseUrl(Url.url)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}
}