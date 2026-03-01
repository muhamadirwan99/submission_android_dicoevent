package com.dicoding.dicoevent.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.dicoevent.data.EventRepository
import com.dicoding.dicoevent.data.local.entity.EventEntity

class FavoriteViewModel(private val repository: EventRepository) : ViewModel() {

    val favoriteEvents: LiveData<List<EventEntity>> = repository.getFavoriteEvents()
}