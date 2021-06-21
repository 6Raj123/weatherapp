package com.raj.weather.network

import com.raj.weather.model.ServerWeatherList


interface ApiHelper {
    suspend fun getWeatherList(cityName: String): ServerWeatherList
}