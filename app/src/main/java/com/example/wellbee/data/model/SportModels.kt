package com.example.wellbee.data.model

import com.google.gson.annotations.SerializedName

// ===============================
// 1️⃣ Model untuk REQUEST ke Backend
// ===============================
data class SportRequest(
    val jenisOlahraga: String,
    val durasiMenit: Int,
    val kaloriTerbakar: Int,
    val foto: String? = null,
    val tanggal: String
)

// ===============================
// 2️⃣ Model untuk RESPONSE saat input (POST)
// ===============================
data class SportResponse(
    val status: String,
    val message: String,
    val data: SportModel? = null
)

// ===============================
// 3️⃣ Model untuk RIWAYAT SPORT (GET history)
// ===============================
data class SportModel(
    val id: Int,
    val userId: Int,
    val jenisOlahraga: String,
    val durasiMenit: Int,
    val kaloriTerbakar: Int,
    val tanggal: String,
    val foto: String? = null   // ← WAJIB ADA
)

data class SportHistory(
    val id: Int,
    val userId: Int,
    val jenisOlahraga: String,
    val durasiMenit: Int,
    val kaloriTerbakar: Int,
    val tanggal: String? = null,
    val foto: String? = null   // ← WAJIB ADA & pakai koma
)

data class SportDailyTotal(
    val tanggal: String,
    val total: Int
)

data class WeeklySportChartResponse(
    @SerializedName("labels")
    val labels: List<String>,

    @SerializedName("values")
    val values: List<Double>,

    @SerializedName("rangeText")
    val rangeText: String
)



// ===============================
// 4️⃣ Model untuk Sleep
// ===============================

data class SleepRequest(
    val jamTidur: String,
    val jamBangun: String,
    val durasiTidur: Double,
    val kualitasTidur: Int,
    val tanggal: String
)

data class SleepResponse(
    val message: String,
    val data: SleepData?
)

data class SleepData(
    val id: Int,
    val jamTidur: String,
    val jamBangun: String,
    val durasiTidur: Double,
    val kualitasTidur: Int,
    val tanggal: String
)

//weight
data class WeightData(
    val id: Int,
    val beratBadan: Double,
    val tinggiBadan: Double,
    val bmi: Double,
    val kategori: String,
    val tanggal: String
)
// ===== RESPONSE DARI BACKEND =====
data class WeightResponse(
    val message: String,
    val data: WeightData
)

data class WeightRequest(
    val beratBadan: Double,
    val tinggiBadan: Double,
    val bmi: Double,
    val kategori: String,
    val tanggal: String
)

data class WeeklySportPoint(
    val dayLabel: String,   // Sen (11 Jun)
    val duration: Int
)


