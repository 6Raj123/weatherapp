package com.raj.weather.network

import com.raj.weather.model.ServerWeatherList


class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {

    override suspend fun getWeatherList(cityName : String) : ServerWeatherList = apiService.getWeatherList(cityName)
}