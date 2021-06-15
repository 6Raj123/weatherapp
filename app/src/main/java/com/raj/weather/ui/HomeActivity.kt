package com.raj.weather.ui

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raj.weather.R
import com.raj.weather.adapter.DisplayWeatherListAdapter
import com.raj.weather.di.DaggerRetrofitComponent
import com.raj.weather.di.RetrofitComponent
import com.raj.weather.di.RetrofitModule
import com.raj.weather.model.DisplayWeatherList
import com.raj.weather.model.ServerWeatherList
import com.raj.weather.network.NetworkCheck
import com.raj.weather.viewModel.HomeActivityViewModel
import retrofit2.Retrofit
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    @set:Inject
    lateinit var retrofit: Retrofit
    private lateinit var homeViewModel: HomeActivityViewModel
    lateinit var displayWeatherLists: List<DisplayWeatherList>
    lateinit var recyclerView: RecyclerView
    private lateinit var searchCity: EditText
    private var networkCheck: NetworkCheck = NetworkCheck()
    var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        searchCity = findViewById(R.id.searchItemEditView)
        homeViewModel =
            ViewModelProvider(this)[HomeActivityViewModel::class.java]

        searchCity.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    checkInternetAvailable()
                    true
                }
                else -> false
            }
        }

        val retrofitComponent: RetrofitComponent =
            DaggerRetrofitComponent.builder().retrofitModule(RetrofitModule())
                .build()
        retrofitComponent.inject(this)

        val weatherListObserver = Observer<List<DisplayWeatherList>> { displayWeatherList ->
            if (displayWeatherList.isEmpty()) {
                clearAdapter()
                Toast.makeText(this@HomeActivity, "No Data Found", Toast.LENGTH_LONG).show()
            }
            else
                showWeatherList(displayWeatherList)
        }
        homeViewModel.displayWeatherLists.observe(this, weatherListObserver)
    }

    fun searchButtonClicked(view: View?) {
        checkInternetAvailable()
    }

    private fun checkInternetAvailable() {
        if (networkCheck.isInternetAvailable(this@HomeActivity)) {
            searchCity.hideKeyboard()
            showProgress()
            apiCallForSearchCity()

        } else {
            Toast.makeText(
                this@HomeActivity,
                "Please check your internet connection",
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    private fun apiCallForSearchCity() {
        homeViewModel.getServerDataListObserver().observe(this, Observer<ServerWeatherList> {
            if (it.cod == 200) {
                homeViewModel.convertToDisplayData(it)
            } else {
                clearAdapter()
                Toast.makeText(this, "Error in fetching data", Toast.LENGTH_SHORT).show()
            }
            dialog?.dismiss()
        })

        homeViewModel.getWeatherErrorObserver().observe(this, Observer<String> {
            dialog?.dismiss()
            clearAdapter()
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })


        homeViewModel.makeApiCallForWeatherData(retrofit, searchCity.text.toString())
    }


    private fun showWeatherList(displayWeatherList: List<DisplayWeatherList>) {

        displayWeatherLists = displayWeatherList
        recyclerView.layoutManager =
            LinearLayoutManager(this@HomeActivity, RecyclerView.VERTICAL, false)
        val adapter = DisplayWeatherListAdapter(displayWeatherList)
        recyclerView.adapter = adapter

    }

    private fun clearAdapter()
    {
        displayWeatherLists = listOf()
        recyclerView.adapter?.notifyDataSetChanged()
    }


    private fun showProgress() {
        dialog = ProgressDialog(this@HomeActivity)
        dialog?.setMessage("Please wait.")
        dialog?.show()
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}