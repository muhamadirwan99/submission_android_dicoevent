package com.dicoding.dicoevent.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicoevent.data.remote.response.ListEventsItem
import com.dicoding.dicoevent.data.remote.retrofit.ApiConfig
import com.dicoding.dicoevent.utils.UiState
import com.dicoding.dicoevent.utils.toUserFriendlyMessage
import kotlinx.coroutines.launch

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
                val userMessage = e.toUserFriendlyMessage()

                _upcomingState.value = UiState.Error(userMessage)
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
                val userMessage = e.toUserFriendlyMessage()

                _searchState.value = UiState.Error(userMessage)
            }
        }
    }

    fun clearSearch() {
        _searchState.value = UiState.Success(emptyList())
    }
}