package com.raj.weather.model

import lombok.Data

@Data
class ServerWeatherList {

    var cod: Int =0
    var list: List<ListWeatherDetails>? = null
    var city: City? = null
}

class ListWeatherDetails {

    var main: Main? = null
    var dt_txt: String? = null

}
class Main {
    var temp : Float = 0F

}
class City {
    var name : String? = null

}



