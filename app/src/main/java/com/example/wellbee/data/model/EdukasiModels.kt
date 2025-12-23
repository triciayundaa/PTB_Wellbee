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
    @SerializedName("tag")
    val tag: String?,
    @SerializedName("gambarUrl")
    val gambarUrl: String?,
    val tanggal: String,
    val jenis: String,
    val userId: Int?,
    @SerializedName("authorName")
    val authorName: String?
)

data class CategoriesResponse(
    val categories: List<String>
)

data class BookmarkDto(
    @SerializedName("bookmarkId")   val bookmarkId: Int,
    @SerializedName("artikelId")    val artikelId: Int,
    @SerializedName("jenis")        val jenis: String,
    @SerializedName("sudah_dibaca") val sudahDibaca: Int,

    @SerializedName("judul")        val judul: String,
    @SerializedName("isi")          val isi: String,
    @SerializedName("kategori")     val kategori: String?,
    @SerializedName("waktuBaca")    val waktuBaca: String?,
    @SerializedName("tag")          val tag: String?,
    @SerializedName("gambarUrl")    val gambarUrl: String?,
    @SerializedName("tanggal")      val tanggal: String?,
    @SerializedName("userId")       val userId: Int?
)

data class BookmarkListResponse(
    @SerializedName("bookmarks") val bookmarks: List<BookmarkDto>
)

data class AddBookmarkRequest(
    @SerializedName("artikelId") val artikelId: Int,
    @SerializedName("jenis")     val jenis: String
)

data class MessageResponse(
    @SerializedName("message") val message: String
)

data class MyArticlesResponse(
    val articles: List<MyArticleDto>
)

data class MyArticleDto(
    val id: Int,
    val judul: String,
    val isi: String,
    val kategori: String?,
    @SerializedName("waktuBaca")
    val waktuBaca: String?,
    val tag: String?,
    @SerializedName("gambarUrl")
    val gambarUrl: String?,
    val status: String,          // "draft" / "uploaded" / "canceled"
    @SerializedName("tanggalUpload")
    val tanggalUpload: String?,  // bisa null
    @SerializedName("authorName")
    val authorName: String?      // <-- baru
)

data class CreateArticleRequest(
    @SerializedName("kategori")    val kategori: String,
    @SerializedName("waktu_baca")  val waktu_baca: String,
    @SerializedName("tag")         val tag: String,
    @SerializedName("judul")       val judul: String,
    @SerializedName("isi")         val isi: String,
    @SerializedName("gambar_url")  val gambar_url: String?,
    @SerializedName("status")      val status: String
)

data class UpdateArticleStatusRequest(
    @SerializedName("status") val status: String
)

data class UpdateMyArticleResponse(
    val message: String,
    val data: MyArticleDto
)
