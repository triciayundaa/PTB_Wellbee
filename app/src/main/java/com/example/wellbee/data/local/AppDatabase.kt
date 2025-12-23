package com.example.wellbee.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ArtikelEntity::class,
        BookmarkEntity::class,
        SearchHistoryEntity::class,
        SportEntity::class,
        SleepEntity::class,
        WeightEntity::class,

        MentalMoodEntity::class,
        MentalJournalEntity::class
    ],


    version = 8,

    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun artikelDao(): ArtikelDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun fisikDao(): FisikDao

    abstract fun mentalDao(): MentalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wellbee.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}