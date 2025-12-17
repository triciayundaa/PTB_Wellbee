package com.example.wellbee.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// =========================================
//  TABEL SPORT (LOCAL CACHE)
// =========================================
@Entity(tableName = "sport_history")
data class SportEntity(
    // 🔥 PERBAIKAN: Gunakan ID dari server sebagai Primary Key
    // Hapus 'localId' agar tidak terjadi duplikasi data saat sync
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    val userId: Int,
    val jenisOlahraga: String,
    val durasiMenit: Int,
    val kaloriTerbakar: Int,
    val tanggal: String,
    val foto: String?
)

data class DailySportSum(
    val date: String,   // format: yyyy-MM-dd
    val total: Int      // total durasiMenit per hari
)

// =========================================
//  TABEL SLEEP
// =========================================
@Entity(tableName = "sleep_history")
data class SleepEntity(
    // 🔥 PERBAIKAN: ID server sebagai Primary Key
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    val userId: Int,
    val jamTidur: String,
    val jamBangun: String,
    val durasiTidur: Double,
    val kualitasTidur: Int,
    val tanggal: String
)

// =========================================
//  TABEL WEIGHT
// =========================================
@Entity(tableName = "weight_history")
data class WeightEntity(
    // 🔥 PERBAIKAN: ID server sebagai Primary Key
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    val userId: Int,
    val beratBadan: Double,
    val tinggiBadan: Double,
    val bmi: Double,
    val kategori: String,
    val tanggal: String
)