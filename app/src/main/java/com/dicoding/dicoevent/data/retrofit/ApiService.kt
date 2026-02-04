package com.dicoding.dicoevent.data.retrofit

import com.dicoding.dicoevent.data.response.DetailEventResponse
import com.dicoding.dicoevent.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Get active events
    @GET("events?active=1")
    fun getActiveEvents(): Call<EventResponse>

    // Get done events
    @GET("events?active=0")
    fun getDoneEvents(): Call<EventResponse>

    // Get detail event by ID
    @GET("events/{id}")
    fun getDetailEvent(@Path("id") id: Int): Call<DetailEventResponse>

    // Search events by name on all status events
    @GET("events")
    fun searchEvents(@Query("active") active: Int = -1,@Query("q") keyword: String ) : Call<EventResponse>
}