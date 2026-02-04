package com.dicoding.dicoevent.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.dicoevent.data.response.DetailEventResponse
import com.dicoding.dicoevent.data.response.EventDetail
import com.dicoding.dicoevent.data.retrofit.ApiConfig
import com.dicoding.dicoevent.utils.EventUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class DetailViewModel : ViewModel() {

    private val _eventDetail = MutableLiveData<EventDetail>()
    val eventDetail: LiveData<EventDetail> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<EventUtil<String>>()
    val snackbarText: LiveData<EventUtil<String>> = _snackbarText

    companion object {
        private const val TAG = "DetailViewModel"
    }

    fun getDetailEvent(id: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvent(id)

        client.enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _eventDetail.value = responseBody.eventDetail ?: EventDetail()
                    }
                } else {
                    _snackbarText.value = EventUtil("Failed to fetch data: ${response.message()}")
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                _isLoading.value = false

                val errorMessage = when (t) {
                    is UnknownHostException -> "No internet connection"
                    is SocketTimeoutException -> "Connection timed out"
                    else -> "onFailure: ${t.message}"
                }

                _snackbarText.value = EventUtil(errorMessage)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}