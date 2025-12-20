package com.example.wellbee.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mental_mood")
data class MentalMoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serverId: Int? = null,
    val userId: Int,
    val emoji: String,
    val moodLabel: String,
    val moodScale: Int,
    val tanggal: String,
    val createdAt: String? = null,
    val isSynced: Boolean = false
)
