package com.raj.weather.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raj.weather.network.ApiHelper
import com.raj.weather.viewModel.HomeActivityViewModel

class ViewModelFactory(private val apiHelper: ApiHelper) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeActivityViewModel::class.java)) {
            return HomeActivityViewModel(apiHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}