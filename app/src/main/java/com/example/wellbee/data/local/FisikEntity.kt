package com.example.wellbee.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sport_history")
data class SportEntity(
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
    val date: String,
    val total: Int
)

@Entity(tableName = "sleep_history")
data class SleepEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    val userId: Int,
    val jamTidur: String,
    val jamBangun: String,
    val durasiTidur: Double,
    val kualitasTidur: Int,
    val tanggal: String
)

@Entity(tableName = "weight_history")
data class WeightEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    val userId: Int,
    val beratBadan: Double,
    val tinggiBadan: Double,
    val bmi: Double,
    val kategori: String,
    val tanggal: String
)