package com.example.wellbee.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SportEntity::class, SleepEntity::class, WeightEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fisikDao(): FisikDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wellbee_fisik_db"
                ).fallbackToDestructiveMigration() // mencegah crash saat update schema
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
