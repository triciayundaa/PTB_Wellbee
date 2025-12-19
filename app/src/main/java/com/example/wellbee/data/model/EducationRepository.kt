package com.example.wellbee.data.model

import android.content.Context
import android.net.Uri
import com.example.wellbee.data.RetrofitClient
import com.example.wellbee.data.local.AppDatabase
import com.example.wellbee.data.local.SearchHistoryEntity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class EducationRepository(private val context: Context) {

    private val api = RetrofitClient.getInstance(context)

    // 🔹 MENGGUNAKAN BASE_URL DARI PUSAT (RetrofitClient)
    private val baseServerUrl = RetrofitClient.BASE_URL.removeSuffix("/")

    private val db = AppDatabase.getInstance(context)
    private val artikelDao = db.artikelDao()
    private val bookmarkDao = db.bookmarkDao()
    private val searchHistoryDao = db.searchHistoryDao()

    /**
     * Fungsi pembantu untuk memastikan URL gambar selalu lengkap (Full URL)
     */
    private fun fixImageUrl(url: String?): String? {
        if (url == null) return null
        return when {
            url.startsWith("http") -> url
            url.startsWith("/") -> "$baseServerUrl$url"
            else -> "$baseServerUrl/$url"
        }
    }

    // ==========================
    // ARTIKEL PUBLIK
    // ==========================
    suspend fun getPublicArticles(search: String? = null): List<PublicArticleDto> {
        val keyword = search?.trim().orEmpty()

        return try {
            val response = api.getPublicArticles()

            val fixed = response.articles.map { article ->
                article.copy(gambarUrl = fixImageUrl(article.gambarUrl))
            }

            artikelDao.clearAndInsert(fixed.map { it.toEntity() })

            val result = if (keyword.isNotBlank()) {
                fixed.filter {
                    it.judul.contains(keyword, true) ||
                            it.isi.contains(keyword, true) ||
                            (it.tag?.contains(keyword, true) == true)
                }
            } else fixed

            result.sortedByDescending { it.tanggal }
        } catch (e: Exception) {
            val cached = if (keyword.isNotBlank()) {
                artikelDao.searchOnce(keyword)
            } else {
                artikelDao.getAllOnce()
            }
            cached.map { it.toDto() }
        }
    }

    suspend fun getCategories(): List<String> {
        return api.getCategories().categories
    }

    // ==========================
    // BOOKMARK
    // ==========================
    private suspend fun syncPendingDeletes() {
        val pendingIds = bookmarkDao.getPendingDeleteIds()
        if (pendingIds.isEmpty()) return

        for (id in pendingIds) {
            try {
                api.deleteBookmark(id)
                bookmarkDao.deleteById(id)
            } catch (_: Exception) {}
        }
    }

    suspend fun getBookmarks(): List<BookmarkDto> {
        return try {
            // 🔹 Sinkronisasi data yang sempat dihapus saat offline
            syncPendingDeletes()

            val response = api.getBookmarks()

            val fixed = response.bookmarks.map { bookmark ->
                bookmark.copy(gambarUrl = fixImageUrl(bookmark.gambarUrl))
            }

            // 🔹 PERBAIKAN: Bersihkan database lokal agar sinkron dengan data server terbaru
            bookmarkDao.deleteAll()

            // Simpan data baru dengan status isDeleted = 0
            bookmarkDao.upsertAll(fixed.map { it.toEntity(isDeleted = 0) })

            // Kembalikan data dari lokal yang sudah bersih
            bookmarkDao.getAllOnce().map { it.toDto() }
        } catch (e: Exception) {
            // Jika offline, tampilkan data lokal yang tersedia (yang isDeleted = 0)
            bookmarkDao.getAllOnce().map { it.toDto() }
        }
    }

    suspend fun addBookmark(artikelId: Int, jenis: String): String {
        val res = api.addBookmark(AddBookmarkRequest(artikelId, jenis))
        // Langsung sinkronkan ulang setelah menambah
        getBookmarks()
        return res.message
    }

    suspend fun deleteBookmark(bookmarkId: Int): String {
        // Soft delete lokal dulu agar UI langsung update (Offline-first)
        bookmarkDao.softDeleteById(bookmarkId)
        return try {
            val res = api.deleteBookmark(bookmarkId)
            // Jika sukses di server, hapus permanen di lokal
            bookmarkDao.deleteById(bookmarkId)
            res.message
        } catch (e: Exception) {
            "Offline: bookmark dihapus lokal dan akan disinkronkan saat online."
        }
    }

    suspend fun markBookmarkAsRead(bookmarkId: Int): String {
        bookmarkDao.markAsReadLocal(bookmarkId)
        return try {
            val res = api.markBookmarkAsRead(bookmarkId)
            res.message
        } catch (e: Exception) {
            "Offline: status dibaca tersimpan lokal dan akan disinkronkan saat online."
        }
    }

    // ==========================
    // UPLOAD ARTIKEL USER
    // ==========================
    suspend fun uploadImage(imageUri: Uri): String {
        val stream = context.contentResolver.openInputStream(imageUri)
            ?: throw Exception("Tidak bisa membuka gambar")

        val bytes = stream.readBytes()
        stream.close()

        val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData(
            name = "image",
            filename = "artikel_${System.currentTimeMillis()}.jpg",
            body = requestBody
        )

        val response = api.uploadImage(part)
        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Upload gambar gagal: ${response.code()}")
        }

        return response.body()!!.url
    }

    suspend fun createMyArticle(
        kategori: String,
        readTime: String,
        tag: String,
        title: String,
        content: String,
        gambarUrl: String?,
        status: String
    ): Boolean {
        val body = CreateArticleRequest(
            kategori = kategori,
            waktu_baca = readTime,
            tag = tag,
            judul = title,
            isi = content,
            gambar_url = gambarUrl,
            status = status
        )

        val response = api.createMyArticle(body)
        if (!response.isSuccessful) {
            throw Exception("Upload artikel gagal: ${response.code()} ${response.message()}")
        }
        return true
    }

    // ==========================
    // ARTIKEL SAYA
    // ==========================
    suspend fun getMyArticles(): List<MyArticleDto> {
        val response = api.getMyArticles()
        return response.articles.map { article ->
            article.copy(gambarUrl = fixImageUrl(article.gambarUrl))
        }
    }

    suspend fun updateMyArticleStatus(articleId: Int, newStatus: String): Boolean {
        val response = api.updateMyArticleStatus(
            id = articleId,
            request = UpdateArticleStatusRequest(newStatus)
        )
        return response.isSuccessful
    }

    suspend fun deleteMyArticle(articleId: Int): Boolean {
        val response = api.deleteMyArticle(articleId)
        return response.isSuccessful
    }

    suspend fun updateMyArticle(
        id: Int,
        judul: String,
        isi: String,
        kategori: String,
        waktuBaca: String,
        tag: String,
        imageUri: Uri?
    ): MyArticleDto {

        fun String.toPart() = this.toRequestBody("text/plain".toMediaTypeOrNull())

        val judulPart = judul.toPart()
        val isiPart = isi.toPart()
        val kategoriPart = kategori.toPart()
        val waktuBacaPart = waktuBaca.toPart()
        val tagPart = tag.toPart()

        val gambarPart: MultipartBody.Part? = when {
            imageUri == null -> null
            imageUri.toString().startsWith("http", true) -> null
            else -> {
                val stream = context.contentResolver.openInputStream(imageUri)
                    ?: throw Exception("Tidak bisa membuka gambar")
                val bytes = stream.readBytes()
                stream.close()

                val reqBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    name = "gambar",
                    filename = "artikel_${System.currentTimeMillis()}.jpg",
                    body = reqBody
                )
            }
        }

        val response = api.updateMyArticle(
            id = id,
            judul = judulPart,
            isi = isiPart,
            kategori = kategoriPart,
            waktuBaca = waktuBacaPart,
            tag = tagPart,
            gambar = gambarPart
        )

        val result = response.data
        return result.copy(gambarUrl = fixImageUrl(result.gambarUrl))
    }

    // ==========================
    // SEARCH HISTORY (ROOM)
    // ==========================
    suspend fun recordSearch(query: String) {
        val q = query.trim()
        if (q.isBlank()) return

        searchHistoryDao.upsert(
            SearchHistoryEntity(
                query = q,
                lastUsedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun getRecentSearches(limit: Int = 8): List<String> {
        return searchHistoryDao.getRecent(limit).map { it.query }
    }

    suspend fun clearSearchHistory() {
        searchHistoryDao.clearAll()
    }
}