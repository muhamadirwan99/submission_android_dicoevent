package com.dicoding.dicoevent.ui.home

import com.dicoding.dicoevent.data.Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.dicoding.dicoevent.data.EventRepository
import com.dicoding.dicoevent.data.local.entity.EventEntity
import com.dicoding.dicoevent.data.remote.retrofit.ApiConfig
import com.dicoding.dicoevent.utils.toUserFriendlyMessage
import kotlinx.coroutines.launch

class HomeViewModel(repository: EventRepository) : ViewModel() {

    private val _refreshTrigger = MutableLiveData<Unit>()

    val upcomingEvents: LiveData<Result<List<EventEntity>>> = _refreshTrigger.switchMap {
        repository.getUpcomingEvents().map { result ->
            if (result is Result.Success) {
                Result.Success(result.data.take(5))
            } else {
                result
            }
        }
    }

    val finishedEvents: LiveData<Result<List<EventEntity>>> = _refreshTrigger.switchMap {
        repository.getFinishedEvents().map { result ->
            if (result is Result.Success) {
                Result.Success(result.data.take(5))
            } else {
                result
            }
        }
    }

    init {
        refresh()
    }

    fun refresh() {
        _refreshTrigger.value = Unit
    }

    private val _searchState = MutableLiveData<Result<List<EventEntity>>>()
    val searchState: LiveData<Result<List<EventEntity>>> = _searchState

    fun searchEvents(query: String) {
        if (query.isEmpty()) {
            _searchState.value = Result.Success(emptyList())
            return
        }

        _searchState.value = Result.Loading

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().searchEvents(keyword = query)
                val searchResults = response.listEvents.map { event ->
                    EventEntity(
                        id = event.id ?: 0,
                        name = event.name ?: "",
                        summary = event.summary,
                        mediaCover = event.mediaCover,
                        imageLogo = event.imageLogo,
                        description = event.description,
                        ownerName = event.ownerName,
                        cityName = event.cityName,
                        category = event.category,
                        beginTime = event.beginTime,
                        endTime = event.endTime,
                        quota = event.quota,
                        registrants = event.registrants,
                        link = event.link,
                        activeStatus = -1,
                        isFavorite = false
                    )
                }
                _searchState.value = Result.Success(searchResults)

            } catch (e: Exception) {
                val userMessage = e.toUserFriendlyMessage()

                _searchState.value = Result.Error(userMessage)
            }
        }
    }

    fun clearSearch() {
        _searchState.value = Result.Success(emptyList())
    }
}