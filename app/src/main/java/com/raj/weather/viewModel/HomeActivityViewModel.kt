package com.raj.weather.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raj.weather.model.DisplayWeatherList
import com.raj.weather.model.ServerWeatherList
import com.raj.weather.network.ApiService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class HomeActivityViewModel : ViewModel() {

    var serverDataList: MutableLiveData<ServerWeatherList> = MutableLiveData()
    var weatherError: MutableLiveData<String> = MutableLiveData()
    val displayWeatherLists = MutableLiveData<List<DisplayWeatherList>>()

    fun getServerDataListObserver(): MutableLiveData<ServerWeatherList> {
        return serverDataList
    }

    fun getWeatherErrorObserver(): MutableLiveData<String> {
        return weatherError
    }

    fun makeApiCallForWeatherData(retrofit: Retrofit, query: String) {

        retrofit.create(ApiService::class.java)
        val retroInstance = retrofit.create(ApiService::class.java)
        retroInstance.getWeatherList(query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(getServerDataObserverRx())
    }

    private fun getServerDataObserverRx(): Observer<ServerWeatherList> {
        return object : Observer<ServerWeatherList> {
            override fun onComplete() {
            }

            override fun onError(e: Throwable) {
                weatherError.postValue((e as HttpException).message())
            }

            override fun onNext(weatherList: ServerWeatherList) {
                serverDataList.postValue(weatherList)
            }

            override fun onSubscribe(d: Disposable) {
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
