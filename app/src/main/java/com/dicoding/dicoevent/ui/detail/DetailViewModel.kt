package com.dicoding.dicoevent.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicoevent.data.response.EventDetail
import com.dicoding.dicoevent.data.retrofit.ApiConfig
import com.dicoding.dicoevent.utils.UiState
import com.dicoding.dicoevent.utils.toUserFriendlyMessage
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val _eventDetailState = MutableLiveData<UiState<EventDetail>>(UiState.Loading)
    val eventDetailState: LiveData<UiState<EventDetail>> = _eventDetailState

    companion object {
        private const val TAG = "DetailViewModel"
    }

    fun getDetailEvent(id: Int) {
        _eventDetailState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getDetailEvent(id)
                _eventDetailState.value = UiState.Success(response.eventDetail ?: EventDetail())

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "getDetailEvent Failure: ${e.localizedMessage}")

                val userMessage = e.toUserFriendlyMessage()

                _eventDetailState.value = UiState.Error(userMessage)
            }
        }

    }
}