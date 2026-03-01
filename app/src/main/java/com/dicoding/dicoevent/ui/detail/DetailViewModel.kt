package com.dicoding.dicoevent.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicoevent.data.EventRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: EventRepository) : ViewModel() {

    fun getDetailEvent(id: Int) = repository.getEventDetail(id)

    fun setFavoriteEvent(id: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.setFavoriteEvent(id, isFavorite)
        }
    }
}