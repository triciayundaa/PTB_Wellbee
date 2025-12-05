package com.example.wellbee.data.model

import com.google.gson.annotations.SerializedName

data class PublicArticlesResponse(
    val articles: List<PublicArticleDto>
)

data class PublicArticleDto(
    val id: Int,
    val judul: String,
    val isi: String,
    val kategori: String?,
    @SerializedName("waktuBaca")
    val waktuBaca: String?,
    val tag: String?,
    @SerializedName("gambarUrl")
    val gambarUrl: String?,
    val tanggal: String,
    val jenis: String,
    val userId: Int?
)

data class CategoriesResponse(
    val categories: List<String>
)
