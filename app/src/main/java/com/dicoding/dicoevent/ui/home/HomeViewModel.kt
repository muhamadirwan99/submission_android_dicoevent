package com.dicoding.dicoevent.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicoevent.data.response.EventResponse
import com.dicoding.dicoevent.data.response.ListEventsItem
import com.dicoding.dicoevent.data.retrofit.ApiConfig
import com.dicoding.dicoevent.ui.detail.DetailViewModel
import com.dicoding.dicoevent.ui.upcoming.UpcomingViewModel
import com.dicoding.dicoevent.utils.EventUtil
import com.dicoding.dicoevent.utils.UiState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getDoneEvents()
                _finishedEvents.value = response.listEvents.take(5)

                _isLoadingFinished.value = false

            } catch (e: Exception) {

                val errorMessage = when (e) {
                    is UnknownHostException -> "Tidak ada koneksi internet"
                    is SocketTimeoutException -> "Koneksi timeout, silakan coba lagi"
                    else -> e.message ?: "Terjadi kesalahan yang tidak diketahui"
                }
                Log.e(TAG, "getListUpcomingEvents Failure: ${e.message}")
                _isLoadingFinished.value = false
                _snackbarText.value = EventUtil(errorMessage)

            }
        }
    }

    private fun getListUpcomingEvents() {
        _isLoadingUpcoming.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getActiveEvents()
                _upcomingEvents.value = response.listEvents.take(5)

                _isLoadingFinished.value = false

            } catch (e: Exception) {

                val errorMessage = when (e) {
                    is UnknownHostException -> "Tidak ada koneksi internet"
                    is SocketTimeoutException -> "Koneksi timeout, silakan coba lagi"
                    else -> e.message ?: "Terjadi kesalahan yang tidak diketahui"
                }
                Log.e(TAG, "getListUpcomingEvents Failure: ${e.message}")
                _isLoadingFinished.value = false
                _snackbarText.value = EventUtil(errorMessage)

            }
        }

    }

    fun searchEvents(query: String) {
        if (query.isEmpty()) {
            _searchEvents.value = emptyList()
            return
        }

        _isLoadingSearch.value = true


//        _searchState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().searchEvents(active = 1, keyword = query)
//                _searchState.value = UiState.Success(response.listEvents)
                _searchEvents.value = response.listEvents

                _isLoadingSearch.value = false

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is UnknownHostException -> "Tidak ada koneksi internet"
                    is SocketTimeoutException -> "Koneksi timeout saat mencari"
                    else -> "Gagal mencari: ${e.message}"
                }
                Log.e(TAG, "searchEvents Failure: ${e.message}")
                _isLoadingSearch.value = false
                _snackbarText.value = EventUtil(errorMessage)
            }
        }
    }
}