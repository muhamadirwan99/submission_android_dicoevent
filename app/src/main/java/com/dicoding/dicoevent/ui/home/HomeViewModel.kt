package com.dicoding.dicoevent.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.dicoevent.data.response.EventResponse
import com.dicoding.dicoevent.data.response.ListEventsItem
import com.dicoding.dicoevent.data.retrofit.ApiConfig
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
            override fun onResponse(
                call: Call<EventResponse?>,
                response: Response<EventResponse?>
            ) {
                _isLoadingFinished.value = false

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody.listEvents.size > 5) {
                            _finishedEvents.value = responseBody.listEvents.subList(0, 5)
                        } else {
                            _finishedEvents.value = responseBody.listEvents
                        }
                    }
                } else {
                    _isLoadingFinished.value = false
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(
                call: Call<EventResponse?>,
                t: Throwable
            ) {
                _isLoadingFinished.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getListUpcomingEvents() {
        _isLoadingUpcoming.value = true

        val client = ApiConfig.getApiService().getActiveEvents()

        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse?>,
                response: Response<EventResponse?>
            ) {
                _isLoadingUpcoming.value = false

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody.listEvents.size > 5) {
                            _upcomingEvents.value = responseBody.listEvents.subList(0, 5)
                        } else {
                            _upcomingEvents.value = responseBody.listEvents
                        }
                    }
                } else {
                    _isLoadingUpcoming.value = false
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(
                call: Call<EventResponse?>,
                t: Throwable
            ) {
                _isLoadingUpcoming.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

     fun searchEvents(query: String) {
        _isLoadingSearch.value = true

        val client = ApiConfig.getApiService().searchEvents(keyword = query)

        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse?>,
                response: Response<EventResponse?>
            ) {
                _isLoadingSearch.value = false

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _searchEvents.value = responseBody.listEvents
                    }
                } else {
                    _isLoadingSearch.value = false
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(
                call: Call<EventResponse?>,
                t: Throwable
            ) {
                _isLoadingSearch.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }


}