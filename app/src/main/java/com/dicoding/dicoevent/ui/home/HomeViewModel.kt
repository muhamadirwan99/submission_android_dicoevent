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

    private val _events = MutableLiveData<List<ListEventsItem>>()
    val events: LiveData<List<ListEventsItem>> = _events

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        getListFinishedEvents()
    }

    private fun getListFinishedEvents() {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getDoneEvents()

        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse?>,
                response: Response<EventResponse?>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody.listEvents.size > 5) {
                            _events.value = responseBody.listEvents.subList(0, 5)
                        } else {
                            _events.value = responseBody.listEvents
                        }
                    }
                } else {
                    _isLoading.value = false
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(
                call: Call<EventResponse?>,
                t: Throwable
            ) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })

    }


}