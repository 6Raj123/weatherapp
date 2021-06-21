package com.raj.weather.network

import com.raj.weather.model.ServerWeatherList
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "8c48e648a0ee1cc6fc7ec94d39baa660"

interface ApiService {

    @GET("forecast?&appid=$API_KEY")
    suspend fun getWeatherList(@Query("q") cityName: String): ServerWeatherList

}