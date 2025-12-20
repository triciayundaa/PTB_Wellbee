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
    private val baseServerUrl = RetrofitClient.BASE_URL.removeSuffix("/")
    private val db = AppDatabase.getInstance(context)
    private val artikelDao = db.artikelDao()
    private val bookmarkDao = db.bookmarkDao()
    private val searchHistoryDao = db.searchHistoryDao()

    private fun fixImageUrl(url: String?): String? {
        if (url == null) return null
        return when {
            url.startsWith("http") -> url
            url.startsWith("/") -> "$baseServerUrl$url"
            else -> "$baseServerUrl/$url"
        }
    }

    // ==========================
    // ARTIKEL PUBLIK (FIXED LOGIC)
    // ==========================
    suspend fun getPublicArticles(search: String? = null): List<PublicArticleDto> {
        val keyword = search?.trim().orEmpty()

        return try {
            val response = api.getPublicArticles()
            val fixed = response.articles.map { it.copy(gambarUrl = fixImageUrl(it.gambarUrl)) }

            // Simpan ke Room
            artikelDao.clearAndInsert(fixed.map { it.toEntity() })

            // AMBIL KEMBALI DARI ROOM (Agar urutan DESC dari SQL Aktif)
            val entities = if (keyword.isNotBlank()) {
                artikelDao.searchOnce(keyword)
            } else {
                artikelDao.getAllOnce()
            }
            entities.map { it.toDto() }

        } catch (e: Exception) {
            // OFFLINE MODE
            val cached = if (keyword.isNotBlank()) {
                artikelDao.searchOnce(keyword)
            } else {
                artikelDao.getAllOnce()
            }
            cached.map { it.toDto() }
        }
    }

    suspend fun getCategories(): List<String> {
        return try {
            api.getCategories().categories
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- BOOKMARK LOGIC (Tetap Sama) ---
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
            syncPendingDeletes()
            val response = api.getBookmarks()
            val fixed = response.bookmarks.map { bookmark ->
                bookmark.copy(gambarUrl = fixImageUrl(bookmark.gambarUrl))
            }
            bookmarkDao.deleteAll()
            bookmarkDao.upsertAll(fixed.map { it.toEntity(isDeleted = 0) })
            bookmarkDao.getAllOnce().map { it.toDto() }
        } catch (e: Exception) {
            bookmarkDao.getAllOnce().map { it.toDto() }
        }
    }

    suspend fun addBookmark(artikelId: Int, jenis: String): String {
        return try {
            val res = api.addBookmark(AddBookmarkRequest(artikelId, jenis))
            getBookmarks()
            res.message
        } catch (e: Exception) { "Offline" }
    }

    suspend fun deleteBookmark(bookmarkId: Int): String {
        bookmarkDao.softDeleteById(bookmarkId)
        return try {
            val res = api.deleteBookmark(bookmarkId)
            bookmarkDao.deleteById(bookmarkId)
            res.message
        } catch (e: Exception) { "Offline" }
    }

    suspend fun markBookmarkAsRead(bookmarkId: Int): String {
        bookmarkDao.markAsReadLocal(bookmarkId)
        return try {
            val res = api.markBookmarkAsRead(bookmarkId)
            res.message
        } catch (e: Exception) { "Offline" }
    }

    // --- UPLOAD & MY ARTICLES (Tetap Sama) ---
    suspend fun uploadImage(imageUri: Uri): String {
        val stream = context.contentResolver.openInputStream(imageUri) ?: throw Exception("Fail")
        val bytes = stream.readBytes()
        stream.close()
        val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image", "art_${System.currentTimeMillis()}.jpg", requestBody)
        val response = api.uploadImage(part)
        return response.body()?.url ?: throw Exception("Fail")
    }

    suspend fun createMyArticle(kategori: String, readTime: String, tag: String, title: String, content: String, gambarUrl: String?, status: String): Boolean {
        val body = CreateArticleRequest(kategori, readTime, tag, title, content, gambarUrl, status)
        return api.createMyArticle(body).isSuccessful
    }

    suspend fun getMyArticles(): List<MyArticleDto> {
        return try {
            val response = api.getMyArticles()
            response.articles.map { it.copy(gambarUrl = fixImageUrl(it.gambarUrl)) }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun updateMyArticleStatus(articleId: Int, newStatus: String): Boolean {
        return try { api.updateMyArticleStatus(articleId, UpdateArticleStatusRequest(newStatus)).isSuccessful } catch (e: Exception) { false }
    }

    suspend fun deleteMyArticle(articleId: Int): Boolean {
        return try { api.deleteMyArticle(articleId).isSuccessful } catch (e: Exception) { false }
    }

    suspend fun updateMyArticle(id: Int, judul: String, isi: String, kategori: String, waktuBaca: String, tag: String, imageUri: Uri?): MyArticleDto {
        fun String.toPart() = this.toRequestBody("text/plain".toMediaTypeOrNull())
        val gambarPart: MultipartBody.Part? = if (imageUri != null && !imageUri.toString().startsWith("http")) {
            val stream = context.contentResolver.openInputStream(imageUri)
            val bytes = stream!!.readBytes()
            stream.close()
            MultipartBody.Part.createFormData("gambar", "img.jpg", bytes.toRequestBody("image/*".toMediaTypeOrNull()))
        } else null

        val response = api.updateMyArticle(id, judul.toPart(), isi.toPart(), kategori.toPart(), waktuBaca.toPart(), tag.toPart(), gambarPart)
        return response.data.copy(gambarUrl = fixImageUrl(response.data.gambarUrl))
    }

    suspend fun recordSearch(query: String) {
        if (query.isBlank()) return
        searchHistoryDao.upsert(SearchHistoryEntity(query.trim(), System.currentTimeMillis()))
    }

    suspend fun getRecentSearches(limit: Int = 8): List<String> = searchHistoryDao.getRecent(limit).map { it.query }
    suspend fun clearSearchHistory() = searchHistoryDao.clearAll()
}