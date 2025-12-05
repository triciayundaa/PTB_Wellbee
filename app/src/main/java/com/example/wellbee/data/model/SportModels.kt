package com.example.wellbee.data.model

// Model untuk Data yang DIKIRIM ke Backend (Request)
data class SportRequest(
    val userId: String,
    val jenisOlahraga: String,
    val durasiMenit: Int,
    val kaloriTerbakar: Int
)

// Model untuk Data yang DITERIMA dari Backend (Response)
data class SportResponse(
    val status: String,
    val message: String,
    val data: Any? = null // Bisa null jika backend tidak mengembalikan data spesifik
)