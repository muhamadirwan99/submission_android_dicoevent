package com.dicoding.dicoevent.ui.finished

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicoevent.data.response.ListEventsItem
import com.dicoding.dicoevent.data.retrofit.ApiConfig
import com.dicoding.dicoevent.utils.UiState
import com.dicoding.dicoevent.utils.toUserFriendlyMessage
import kotlinx.coroutines.launch

class FinishedViewModel : ViewModel() {

    private val _finishedState = MutableLiveData<UiState<List<ListEventsItem>>>(UiState.Loading)
    val finishedState: LiveData<UiState<List<ListEventsItem>>> = _finishedState

    private val _searchState = MutableLiveData<UiState<List<ListEventsItem>>>(UiState.Success(emptyList()))
    val searchState: LiveData<UiState<List<ListEventsItem>>> = _searchState

    companion object {
        private const val TAG = "FinishedViewModel"
    }

    init {
        getListFinishedEvents()
    }

    fun getListFinishedEvents() {
        _finishedState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getDoneEvents()
                _finishedState.value = UiState.Success(response.listEvents)

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "getListFinishedEvents Failure: ${e.localizedMessage}")

                val userMessage = e.toUserFriendlyMessage()

                _finishedState.value = UiState.Error(userMessage)
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
                val response = ApiConfig.getApiService().searchEvents(active = 0, keyword = query)
                _searchState.value = UiState.Success(response.listEvents)

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "getSearchEvents Failure: ${e.localizedMessage}")

                val userMessage = e.toUserFriendlyMessage()

                _searchState.value = UiState.Error(userMessage)
            }
        }
    }

    fun clearSearch() {
        _searchState.value = UiState.Success(emptyList())
    }
}