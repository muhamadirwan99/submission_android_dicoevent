package com.dicoding.dicoevent.data.remote.retrofit

import com.dicoding.dicoevent.data.remote.response.DetailEventResponse
import com.dicoding.dicoevent.data.remote.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Get events
    @GET("events")
    suspend fun getEvents(@Query("active")active: Int): EventResponse

    // Get detail event by ID
    @GET("events/{id}")
    suspend fun getDetailEvent(@Path("id") id: Int): DetailEventResponse

    // Search events by name on all status events
    @GET("events")
    suspend fun searchEvents(@Query("active") active: Int = -1, @Query("q") keyword: String): EventResponse
}