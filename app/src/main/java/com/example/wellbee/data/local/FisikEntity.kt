package com.example.wellbee.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// =========================================
//  TABEL SPORT (LOCAL CACHE)
// =========================================
@Entity(tableName = "sport_history")
data class SportEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val id: Int,                // ID dari server
    val userId: Int,
    val jenisOlahraga: String,
    val durasiMenit: Int,
    val kaloriTerbakar: Int,
    val tanggal: String?,
    val foto: String?           // base64
)

// =========================================
//  TABEL SLEEP
// =========================================
@Entity(tableName = "sleep_history")
data class SleepEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val id: Int,
    val userId: Int,
    val jamTidur: String,
    val jamBangun: String,
    val durasiTidur: Double,
    val kualitasTidur: Int,
    val tanggal: String
)

//weight
@Entity(tableName = "weight_history")
data class WeightEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val id: Int,
    val beratBadan: Double,
    val tinggiBadan: Double,
    val bmi: Double,
    val kategori: String,
    val tanggal: String
)

