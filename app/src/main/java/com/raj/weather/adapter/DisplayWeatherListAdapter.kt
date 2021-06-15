package com.raj.weather.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raj.weather.R
import com.raj.weather.model.DisplayWeatherList

class DisplayWeatherListAdapter(private val dataSet:List<DisplayWeatherList>) :
    RecyclerView.Adapter<DisplayWeatherListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateShow : TextView = view.findViewById(R.id.dateShow)
        val temperature: TextView = view.findViewById(R.id.temperature)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_weather_list, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.dateShow.text = dataSet[position].dateString
        viewHolder.temperature.text = dataSet[position].temp

    }

    override fun getItemCount() = dataSet.size

}