package com.example.wellbee.data.model

data class CreateArticleRequest(
    val kategori: String,
    val waktu_baca: String,
    val tag: String,
    val judul: String,
    val isi: String,
    val gambar_url: String? = null
)

data class UploadImageResponse(
    val message: String,
    val url: String
)
