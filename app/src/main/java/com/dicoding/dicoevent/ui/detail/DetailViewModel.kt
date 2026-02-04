package com.dicoding.dicoevent.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicoevent.data.response.DetailEventResponse
import com.dicoding.dicoevent.data.response.EventDetail
import com.dicoding.dicoevent.data.retrofit.ApiConfig
import com.dicoding.dicoevent.ui.upcoming.UpcomingViewModel
import com.dicoding.dicoevent.utils.EventUtil
import com.dicoding.dicoevent.utils.UiState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class DetailViewModel : ViewModel() {

    private val _eventDetail = MutableLiveData<EventDetail>()
    val eventDetail: LiveData<EventDetail> = _eventDetail

    //TODO: Implementasikan loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<EventUtil<String>>()
    val snackbarText: LiveData<EventUtil<String>> = _snackbarText

    companion object {
        private const val TAG = "DetailViewModel"
    }

    fun getDetailEvent(id: Int) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getDetailEvent(id)

//                _upcomingState.value = UiState.Success(response.listEvents)

                _isLoading.value = false

            } catch (e: Exception) {

                val errorMessage = when (e) {
                    is UnknownHostException -> "Tidak ada koneksi internet"
                    is SocketTimeoutException -> "Koneksi timeout, silakan coba lagi"
                    else -> e.message ?: "Terjadi kesalahan yang tidak diketahui"
                }
                Log.e(TAG, "getListUpcomingEvents Failure: ${e.message}")
//                _upcomingState.value = UiState.Error(errorMessage)
                _isLoading.value = false
                _snackbarText.value = EventUtil(errorMessage)

            }
        }

    }
}