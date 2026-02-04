package com.dicoding.dicoevent.ui.upcoming

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicoevent.data.response.EventResponse
import com.dicoding.dicoevent.data.response.ListEventsItem
import com.dicoding.dicoevent.data.retrofit.ApiConfig
import com.dicoding.dicoevent.utils.EventUtil
import com.dicoding.dicoevent.utils.UiState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class UpcomingViewModel : ViewModel() {

    private val _upcomingState = MutableLiveData<UiState<List<ListEventsItem>>>(UiState.Loading)
    val upcomingState: LiveData<UiState<List<ListEventsItem>>> = _upcomingState

    private val _searchState = MutableLiveData<UiState<List<ListEventsItem>>>(UiState.Success(emptyList()))
    val searchState: LiveData<UiState<List<ListEventsItem>>> = _searchState

    companion object {
        private const val TAG = "UpcomingViewModel"
    }

    init {
        getListUpcomingEvents()
    }

     fun getListUpcomingEvents() {
        _upcomingState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getActiveEvents()

                _upcomingState.value = UiState.Success(response.listEvents)

            } catch (e: Exception) {

                val errorMessage = when (e) {
                    is UnknownHostException -> "Tidak ada koneksi internet"
                    is SocketTimeoutException -> "Koneksi timeout, silakan coba lagi"
                    else -> e.message ?: "Terjadi kesalahan yang tidak diketahui"
                }
                Log.e(TAG, "getListUpcomingEvents Failure: ${e.message}")
                _upcomingState.value = UiState.Error(errorMessage)
            }
        }
    }

    fun searchEvents(query: String) {
        if (query.isEmpty()) {
            _searchState.value = UiState.Success(emptyList())
            return
        }

        _searchState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().searchEvents(active = 1, keyword = query)
                _searchState.value = UiState.Success(response.listEvents)

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is UnknownHostException -> "Tidak ada koneksi internet"
                    is SocketTimeoutException -> "Koneksi timeout saat mencari"
                    else -> "Gagal mencari: ${e.message}"
                }
                Log.e(TAG, "searchEvents Failure: ${e.message}")
                _searchState.value = UiState.Error(errorMessage)
            }
        }
    }
}