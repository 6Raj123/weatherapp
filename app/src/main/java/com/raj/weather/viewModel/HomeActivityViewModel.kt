package com.raj.weather.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raj.weather.model.DisplayWeatherList
import com.raj.weather.model.ServerWeatherList
import com.raj.weather.network.ApiHelper
import com.raj.weather.utils.Resource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class HomeActivityViewModel(private val apiHelper: ApiHelper) : ViewModel() {

    private var serverDataList: MutableLiveData<Resource<ServerWeatherList>> = MutableLiveData()
    val displayWeatherLists = MutableLiveData<List<DisplayWeatherList>>()

    fun getServerDataListObserver(): MutableLiveData<Resource<ServerWeatherList>> {
        return serverDataList
    }

    fun fetchWeatherData(query: String) {
        viewModelScope.launch {
            serverDataList.postValue(Resource.loading(null))
            try {
                val weatherFromApi = apiHelper.getWeatherList(query)
                serverDataList.postValue(Resource.success(weatherFromApi))
            } catch (e: Exception) {
                serverDataList.postValue(Resource.error(e.toString(), null))
            }
        }
    }

    fun convertToDisplayData(serverWeatherList: ServerWeatherList) {
        val weatherDisplayList = ArrayList<DisplayWeatherList>()
        serverWeatherList.list?.forEach()
        {
            val weatherDisplay = DisplayWeatherList()
            weatherDisplay.dateString = getFormattedDate(it.dt_txt)
            weatherDisplay.temp = convertKelvinToCelsius(it.main?.temp)
            weatherDisplayList.add(weatherDisplay)

        }
        displayWeatherLists.value = weatherDisplayList
    }


    private fun getFormattedDate(dateStr: String?): String {
        try {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val displayFormatter = SimpleDateFormat("dd/MMM, HH:mm aaa", Locale.getDefault())
            val date = formatter.parse(dateStr)
            displayFormatter.timeZone = Calendar.getInstance().timeZone
            return displayFormatter.format(date).toString()
        } catch (e: Exception) {
        }
        return ""
    }

    private fun convertKelvinToCelsius(degrees: Float?): String {
        if (degrees != null)
            return (degrees - 273.15).roundToInt().toString()
        else
            return "0"
    }


}
