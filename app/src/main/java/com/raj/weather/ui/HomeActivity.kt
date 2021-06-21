package com.raj.weather.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raj.weather.R
import com.raj.weather.adapter.DisplayWeatherListAdapter
import com.raj.weather.model.DisplayWeatherList
import com.raj.weather.network.ApiHelperImpl
import com.raj.weather.network.NetworkCheck
import com.raj.weather.network.RetrofitBuilder
import com.raj.weather.utils.Status
import com.raj.weather.utils.ViewModelFactory
import com.raj.weather.viewModel.HomeActivityViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeActivityViewModel
    private lateinit var displayWeatherLists: List<DisplayWeatherList>
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchCity: EditText
    private var networkCheck: NetworkCheck = NetworkCheck()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
        setupViewModel()
        setupObserver()
    }


    fun searchButtonClicked(view: View?) {
        checkInternetAvailable()
    }

    private fun setupUI() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        searchCity = findViewById(R.id.searchItemEditView)
        searchCity.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    checkInternetAvailable()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupViewModel() {
        homeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService)
            )
        ).get(HomeActivityViewModel::class.java)
    }


    private fun setupObserver() {
        val weatherListObserver = Observer<List<DisplayWeatherList>> { displayWeatherList ->
            if (displayWeatherList.isEmpty()) {
                clearAdapter()
                Toast.makeText(this@HomeActivity, "No Data Found", Toast.LENGTH_LONG).show()
            } else
                showWeatherList(displayWeatherList)
        }
        homeViewModel.displayWeatherLists.observe(this, weatherListObserver)
    }



    private fun checkInternetAvailable() {
        if (networkCheck.isInternetAvailable(this@HomeActivity)) {
            searchCity.hideKeyboard()
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
        homeViewModel.getServerDataListObserver().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { weather -> homeViewModel.convertToDisplayData(it.data) }
                    recyclerView.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    showProgress()
                }
                Status.ERROR -> {
                    recyclerView.visibility = View.GONE
                    clearAdapter()
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
            dismissProgress()
        })

        homeViewModel.fetchWeatherData(searchCity.text.toString())
    }


    private fun showWeatherList(displayWeatherList: List<DisplayWeatherList>) {

        displayWeatherLists = displayWeatherList
        recyclerView.layoutManager =
            LinearLayoutManager(this@HomeActivity, RecyclerView.VERTICAL, false)
        val adapter = DisplayWeatherListAdapter(displayWeatherList)
        recyclerView.adapter = adapter

    }

    private fun clearAdapter() {
        displayWeatherLists = listOf()
        recyclerView.adapter?.notifyDataSetChanged()
    }


    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        searchCity.isEnabled = false
    }

    private fun dismissProgress() {
        progressBar.visibility = View.GONE
        searchCity.isEnabled = true
    }
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}