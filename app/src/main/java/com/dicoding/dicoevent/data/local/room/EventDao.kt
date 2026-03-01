package com.dicoding.dicoevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.dicoevent.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM event_table WHERE activeStatus = :activeStatus")
    fun getEvents(activeStatus: Int): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event_table WHERE isFavorite = 1")
    fun getFavoriteEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event_table WHERE id = :id")
    fun getEventDetail(id: Int): LiveData<EventEntity>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Query(
        """
            UPDATE event_table SET 
            name = :name, summary = :summary, mediaCover = :mediaCover, imageLogo = :imageLogo,
            description = :description, ownerName = :ownerName, cityName = :cityName, 
            category = :category, beginTime = :beginTime, endTime = :endTime, 
            registrants = :registrants, quota = :quota, link = :link, activeStatus = :activeStatus
            WHERE id = :id
        """
    )
    suspend fun updateEvent(
        id: Int,
        name: String,
        summary: String?,
        mediaCover: String?,
        imageLogo: String?,
        description: String?,
        ownerName: String?,
        cityName: String?,
        category: String?,
        beginTime: String?,
        endTime: String?,
        registrants: Int?,
        quota: Int?,
        link: String?,
        activeStatus: Int
    )

    @Query(
        """
            UPDATE event_table SET 
            name = :name, summary = :summary, mediaCover = :mediaCover, imageLogo = :imageLogo,
            description = :description, ownerName = :ownerName, cityName = :cityName, 
            category = :category, beginTime = :beginTime, endTime = :endTime, 
            registrants = :registrants, quota = :quota, link = :link 
            WHERE id = :id
        """
    )
    suspend fun updateEventDetail(
        id: Int,
        name: String,
        summary: String?,
        mediaCover: String?,
        imageLogo: String?,
        description: String?,
        ownerName: String?,
        cityName: String?,
        category: String?,
        beginTime: String?,
        endTime: String?,
        registrants: Int?,
        quota: Int?,
        link: String?
    )

    @Query("DELETE FROM event_table WHERE isFavorite = 0 AND activeStatus = :activeStatus")
    suspend fun deleteAllNonFavorite(activeStatus: Int)

    @Query("UPDATE event_table SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavoriteEvent(id: Int, isFavorite: Boolean)
}