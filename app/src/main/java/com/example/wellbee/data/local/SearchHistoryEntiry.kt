package com.example.wellbee.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,     // unique per query
    val lastUsedAt: Long = System.currentTimeMillis()
)
