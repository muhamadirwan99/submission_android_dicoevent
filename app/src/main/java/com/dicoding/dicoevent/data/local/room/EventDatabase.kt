package com.dicoding.dicoevent.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.dicoevent.data.local.entity.EventEntity


@Database(entities = [EventEntity::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var instance: NewsDatabase? = null

        fun getInstance(context: Context): NewsDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java, "Event.db"
                ).build()
            }
    }
}