package com.dicoding.dicoevent.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.dicoevent.data.response.EventResponse
import com.dicoding.dicoevent.data.response.ListEventsItem
import com.dicoding.dicoevent.data.retrofit.ApiConfig
import com.dicoding.dicoevent.utils.EventUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _searchEvents = MutableLiveData<List<ListEventsItem>>()
    val searchEvents: LiveData<List<ListEventsItem>> = _searchEvents

    private val _isLoadingFinished = MutableLiveData<Boolean>()
    val isLoadingFinished: LiveData<Boolean> = _isLoadingFinished

    private val _isLoadingUpcoming = MutableLiveData<Boolean>()
    val isLoadingUpcoming: LiveData<Boolean> = _isLoadingUpcoming

    private val _isLoadingSearch = MutableLiveData<Boolean>(false)
    val isLoadingSearch: LiveData<Boolean> = _isLoadingSearch

    private val _snackbarText = MutableLiveData<EventUtil<String>>()
    val snackbarText: LiveData<EventUtil<String>> = _snackbarText

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        getListFinishedEvents()
        getListUpcomingEvents()
    }

    private fun getListFinishedEvents() {
        _isLoadingFinished.value = true
        val client = ApiConfig.getApiService().getDoneEvents()

        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoadingFinished.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _finishedEvents.value = responseBody.listEvents.take(5)
                    }
                } else {
                    _snackbarText.value = EventUtil("Failed to fetch data: ${response.message()}")
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoadingFinished.value = false

                val errorMessage = when (t) {
                    is java.net.UnknownHostException -> "No internet connection"
                    is java.net.SocketTimeoutException -> "Connection timed out"
                    else -> "onFailure: ${t.message}"
                }

                _snackbarText.value = EventUtil(errorMessage)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getListUpcomingEvents() {
        _isLoadingUpcoming.value = true
        val client = ApiConfig.getApiService().getActiveEvents()

        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _isLoadingUpcoming.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _upcomingEvents.value = responseBody.listEvents.take(5)
                    }
                } else {
                    _snackbarText.value = EventUtil("Failed to fetch upcoming events: ${response.message()}")
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoadingUpcoming.value = false

                val errorMessage = when (t) {
                    is java.net.UnknownHostException -> "No internet connection"
                    is java.net.SocketTimeoutException -> "Connection timed out"
                    else -> "onFailure: ${t.message}"
                }

                _snackbarText.value = EventUtil(errorMessage)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun searchEvents(query: String) {
        _isLoadingSearch.value = true
        val client = ApiConfig.getApiService().searchEvents(keyword = query)

        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _isLoadingSearch.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _searchEvents.value = responseBody.listEvents
                    }
                } else {
                    _snackbarText.value = EventUtil("Failed to search events: ${response.message()}")
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoadingSearch.value = false

                val errorMessage = when (t) {
                    is java.net.UnknownHostException -> "No internet connection"
                    is java.net.SocketTimeoutException -> "Connection timed out"
                    else -> "Search error: ${t.message}"
                }

                _snackbarText.value = EventUtil(errorMessage)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}