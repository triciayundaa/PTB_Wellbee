package com.example.wellbee.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mental_jurnal")
data class MentalJournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val triggerLabel: String?,
    val isiJurnal: String,
    val fotoPath: String? = null,
    val audioPath: String? = null,
    val tanggal: String,
    val isSynced: Boolean = false,
    val serverId: Int? = null
)
