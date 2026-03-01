package com.dicoding.dicoevent.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.dicoevent.data.local.room.EventDao
import com.dicoding.dicoevent.data.local.entity.EventEntity
import com.dicoding.dicoevent.data.remote.retrofit.ApiService

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
) {
    fun getUpcomingEvents(): LiveData<Result<List<EventEntity>>> {
        return fetchEvents(1)
    }

    fun getFinishedEvents(): LiveData<Result<List<EventEntity>>> {
        return fetchEvents(0)
    }

    fun getEventDetail(id: Int): LiveData<Result<EventEntity>> = liveData {
        emit(Result.Loading)

        try {
            val response = apiService.getDetailEvent(id)
            val event = response.eventDetail

            if (event != null) {
                val eventEntity = EventEntity(
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

                eventDao.insertEvents(listOf(eventEntity))
                eventDao.updateEventDetail(
                    id = eventEntity.id,
                    name = eventEntity.name,
                    summary = eventEntity.summary,
                    mediaCover = eventEntity.mediaCover,
                    imageLogo = eventEntity.imageLogo,
                    description = eventEntity.description,
                    ownerName = eventEntity.ownerName,
                    cityName = eventEntity.cityName,
                    category = eventEntity.category,
                    beginTime = eventEntity.beginTime,
                    endTime = eventEntity.endTime,
                    registrants = eventEntity.registrants,
                    quota = eventEntity.quota,
                    link = eventEntity.link
                )
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }

        val localData: LiveData<Result<EventEntity>> = eventDao.getEventDetail(id).map { Result.Success(it) }
        emitSource(localData)
    }

    fun getFavoriteEvents(): LiveData<List<EventEntity>> {
        return eventDao.getFavoriteEvents()
    }

    suspend fun setFavoriteEvent(id: Int, isFavorite: Boolean) {
        eventDao.setFavoriteEvent(id, isFavorite)
    }

    private fun fetchEvents(activeStatusParam: Int): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getEvents(activeStatusParam)
            val events = response.listEvents

            val eventList = events.map { event ->
                EventEntity(
                    event.id ?: 0,
                    event.name ?: "",
                    event.summary,
                    event.mediaCover,
                    event.imageLogo,
                    event.description,
                    event.ownerName,
                    event.cityName,
                    event.category,
                    event.beginTime,
                    event.endTime,
                    event.quota,
                    event.registrants,
                    event.link,
                    activeStatusParam,
                    false,
                )
            }

            eventDao.upsertEvents(eventList, activeStatusParam)
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }

        val localData: LiveData<Result<List<EventEntity>>> =
            eventDao.getEvents(activeStatusParam).map { Result.Success(it) }
        emitSource(localData)
    }


    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
        ): EventRepository = instance ?: synchronized(this) {
            instance ?: EventRepository(apiService, eventDao)
        }.also { instance = it }
    }
}
