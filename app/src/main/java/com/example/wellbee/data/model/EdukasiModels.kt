package com.example.wellbee.data.model

import com.google.gson.annotations.SerializedName

// ======================
// ARTIKEL PUBLIK
// ======================

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

// ======================
// KATEGORI
// ======================

data class CategoriesResponse(
    val categories: List<String>
)

// ======================
// BOOKMARK
// ======================

/**
 * 1 item bookmark dari GET /api/edukasi/bookmarks
 *
 * Kolom yang dikirim backend:
 * bookmarkId, artikelId, jenis, sudah_dibaca,
 * judul, isi, kategori, waktuBaca, tag, gambarUrl, tanggal, userId
 */
data class BookmarkDto(
    @SerializedName("bookmarkId")   val bookmarkId: Int,
    @SerializedName("artikelId")    val artikelId: Int,
    @SerializedName("jenis")        val jenis: String,          // "static" / "user"
    @SerializedName("sudah_dibaca") val sudahDibaca: Int,       // 0 / 1

    @SerializedName("judul")        val judul: String,
    @SerializedName("isi")          val isi: String,
    @SerializedName("kategori")     val kategori: String?,
    @SerializedName("waktuBaca")    val waktuBaca: String?,
    @SerializedName("tag")          val tag: String?,
    @SerializedName("gambarUrl")    val gambarUrl: String?,
    @SerializedName("tanggal")      val tanggal: String?,
    @SerializedName("userId")       val userId: Int?            // null untuk static
)

/** Response dari GET /api/edukasi/bookmarks */
data class BookmarkListResponse(
    @SerializedName("bookmarks") val bookmarks: List<BookmarkDto>
)

/** Request body untuk POST /api/edukasi/bookmarks */
data class AddBookmarkRequest(
    @SerializedName("artikelId") val artikelId: Int,
    @SerializedName("jenis")     val jenis: String
)

/** Response sederhana message dari POST/DELETE/PATCH bookmark */
data class MessageResponse(
    @SerializedName("message") val message: String
)

// ======================
// ARTIKEL SAYA
// ======================

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

// ======================
// REQUEST / RESPONSE LAIN
// ======================

/**
 * Body untuk POST /api/edukasi/my-articles
 */
data class CreateArticleRequest(
    @SerializedName("kategori")    val kategori: String,
    @SerializedName("waktu_baca")  val waktu_baca: String,
    @SerializedName("tag")         val tag: String,
    @SerializedName("judul")       val judul: String,
    @SerializedName("isi")         val isi: String,
    @SerializedName("gambar_url")  val gambar_url: String?,
    @SerializedName("status")      val status: String
)

/**
 * Body untuk PATCH/PUT status artikel:
 * { "status": "draft" / "uploaded" / "canceled" }
 */
data class UpdateArticleStatusRequest(
    @SerializedName("status") val status: String
)

/**
 * Response untuk PUT /api/edukasi/my-articles/{id}
 * {
 *   "message": "Artikel berhasil diperbarui",
 *   "data": { ...MyArticleDto }
 * }
 */
data class UpdateMyArticleResponse(
    val message: String,
    val data: MyArticleDto
)
