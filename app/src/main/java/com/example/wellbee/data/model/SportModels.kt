package com.example.wellbee.data.model

// ===============================
// 1️⃣ Model untuk REQUEST ke Backend
// ===============================
data class SportRequest(
    val userId: Int,
    val jenisOlahraga: String,
    val durasiMenit: Int,
    val kaloriTerbakar: Int
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
    val tanggal: String   // Pastikan backend mengirim ini!
)

data class SportHistory(
    val id: Int,
    val userId: Int,
    val jenisOlahraga: String,
    val durasiMenit: Int,
    val kaloriTerbakar: Int,
    val tanggal: String? = null   // opsional
)

