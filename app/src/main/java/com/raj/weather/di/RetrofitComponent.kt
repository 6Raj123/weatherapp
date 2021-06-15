package com.raj.weather.di

import com.raj.weather.ui.HomeActivity
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [RetrofitModule::class])
interface RetrofitComponent {
    fun inject(activity: HomeActivity?)
}