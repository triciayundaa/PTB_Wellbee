package com.example.wellbee.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
// Pastikan Import untuk Entity Nailah ada (biasanya auto-import nanti kalau merah)
//import com.example.wellbee.data.local.entity.MentalMoodEntity
//import com.example.wellbee.data.local.entity.MentalJournalEntity
//import com.example.wellbee.data.local.dao.MentalDao

@Database(
    entities = [
        // === PUNYA KAMU (FATHIYA) ===
        ArtikelEntity::class,
        BookmarkEntity::class,
        SearchHistoryEntity::class,
        SportEntity::class,
        SleepEntity::class,
        WeightEntity::class,

        // === PUNYA NAILAH (GABUNGAN) ===
        MentalMoodEntity::class,
        MentalJournalEntity::class
    ],


    version = 8,

    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // === DAO KAMU ===
    abstract fun artikelDao(): ArtikelDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun fisikDao(): FisikDao

    // === DAO NAILAH ===
    abstract fun mentalDao(): MentalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wellbee.db" // Kita pakai nama DB utama kamu saja
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}