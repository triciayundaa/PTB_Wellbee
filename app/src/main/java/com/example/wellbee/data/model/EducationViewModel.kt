package com.example.wellbee.data.model

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EducationViewModel(context: Context) : ViewModel() {

    private val repo = EducationRepository(context)

    // ==========================
    // ARTIKEL PUBLIK
    // ==========================

    var articles by mutableStateOf<List<PublicArticleDto>>(emptyList())
        private set

    var categories by mutableStateOf<List<String>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var uploadStatus by mutableStateOf<String?>(null)
        private set

    fun loadArticles() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Memuat artikel publik dan diurutkan terbaru di atas
                articles = repo.getPublicArticles()
                    .sortedByDescending { it.tanggal }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Gagal memuat artikel"
            } finally {
                isLoading = false
            }
        }
    }

    fun searchArticles(query: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                articles = repo.getPublicArticles(query.takeIf { it.isNotBlank() })
                    .sortedByDescending { it.tanggal }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Gagal mencari artikel"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                categories = repo.getCategories()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ==========================
    // SEARCH HISTORY
    // ==========================

    var recentSearches by mutableStateOf<List<String>>(emptyList())
        private set

    fun loadRecentSearches(limit: Int = 8) {
        viewModelScope.launch {
            recentSearches = repo.getRecentSearches(limit)
        }
    }

    fun recordSearch(query: String) {
        viewModelScope.launch {
            repo.recordSearch(query)
            recentSearches = repo.getRecentSearches(8)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            repo.clearSearchHistory()
            recentSearches = emptyList()
        }
    }

    // ==========================
    // UPLOAD ARTIKEL (BACKEND)
    // ==========================

    // PERBAIKAN: Ditambahkan parameter onSuccess: () -> Unit
    fun uploadArticleWithImage(
        imageUri: Uri?,
        kategori: String,
        readTime: String,
        tag: String,
        title: String,
        content: String,
        status: String,
        onSuccess: () -> Unit = {} // Parameter baru untuk sinkronisasi navigasi
    ) {
        viewModelScope.launch {
            try {
                isLoading = true // Aktifkan loading saat upload
                uploadStatus = null

                val imageUrl = when {
                    imageUri == null -> null
                    imageUri.toString().startsWith("http", true) -> imageUri.toString()
                    else -> repo.uploadImage(imageUri)
                }

                val success = repo.createMyArticle(
                    kategori = kategori,
                    readTime = readTime,
                    tag = tag,
                    title = title,
                    content = content,
                    gambarUrl = imageUrl,
                    status = status
                )

                if (success) {
                    uploadStatus = "SUCCESS"
                    // Paksa memuat ulang data terbaru dari server agar list sinkron
                    val updatedMyArticles = repo.getMyArticles()
                    myArticles = updatedMyArticles.sortedByDescending { it.id }

                    val updatedPublic = repo.getPublicArticles()
                    articles = updatedPublic.sortedByDescending { it.tanggal }

                    onSuccess() // Navigasi dilakukan HANYA setelah data berhasil di-refresh
                } else {
                    uploadStatus = "FAILED"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                uploadStatus = "ERROR"
            } finally {
                isLoading = false
            }
        }
    }

    // ==========================
    // BOOKMARK
    // ==========================

    var bookmarks by mutableStateOf<List<BookmarkDto>>(emptyList())
        private set

    var isLoadingBookmarks by mutableStateOf(false)
        private set

    var bookmarkError by mutableStateOf<String?>(null)
        private set

    fun loadBookmarks() {
        viewModelScope.launch {
            isLoadingBookmarks = true
            bookmarkError = null
            try {
                bookmarks = repo.getBookmarks()
            } catch (e: Exception) {
                e.printStackTrace()
                bookmarkError = e.message ?: "Gagal memuat bookmark"
            } finally {
                isLoadingBookmarks = false
            }
        }
    }

    fun addBookmark(artikelId: Int, jenis: String) {
        viewModelScope.launch {
            try {
                bookmarkError = null
                repo.addBookmark(artikelId, jenis)
                bookmarks = repo.getBookmarks()
            } catch (e: Exception) {
                e.printStackTrace()
                bookmarkError = e.message ?: "Gagal menambah bookmark"
            }
        }
    }

    fun deleteBookmark(bookmarkId: Int) {
        viewModelScope.launch {
            try {
                bookmarkError = null
                repo.deleteBookmark(bookmarkId)
                bookmarks = repo.getBookmarks()
            } catch (e: Exception) {
                e.printStackTrace()
                bookmarkError = e.message ?: "Gagal menghapus bookmark"
            }
        }
    }

    fun markBookmarkAsRead(bookmarkId: Int) {
        viewModelScope.launch {
            try {
                bookmarkError = null
                repo.markBookmarkAsRead(bookmarkId)
                bookmarks = repo.getBookmarks()
            } catch (e: Exception) {
                e.printStackTrace()
                bookmarkError = e.message ?: "Gagal menandai terbaca"
            }
        }
    }

    // ==========================
    // ARTIKEL SAYA (MY ARTICLES)
    // ==========================

    var myArticles by mutableStateOf<List<MyArticleDto>>(emptyList())
        private set

    var isLoadingMyArticles by mutableStateOf(false)
        private set

    var myArticleError by mutableStateOf<String?>(null)
        private set

    fun loadMyArticles() {
        viewModelScope.launch {
            isLoadingMyArticles = true
            myArticleError = null
            try {
                // Selalu urutkan berdasarkan ID terbesar (terbaru) agar muncul teratas
                myArticles = repo.getMyArticles().sortedByDescending { it.id }
            } catch (e: Exception) {
                e.printStackTrace()
                myArticleError = e.message ?: "Gagal memuat artikel saya"
            } finally {
                isLoadingMyArticles = false
            }
        }
    }

    fun changeMyArticleStatus(articleId: Int, newStatus: String) {
        viewModelScope.launch {
            try {
                val success = repo.updateMyArticleStatus(articleId, newStatus)
                if (success) {
                    myArticles = myArticles.map {
                        if (it.id == articleId) it.copy(status = newStatus) else it
                    }
                    // Refresh artikel publik juga jika status berubah ke "uploaded"
                    loadArticles()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteMyArticle(articleId: Int) {
        viewModelScope.launch {
            try {
                val success = repo.deleteMyArticle(articleId)
                if (success) {
                    myArticles = myArticles.filter { it.id != articleId }
                    loadArticles() // Sinkronkan halaman publik
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateMyArticle(
        id: Int,
        kategori: String,
        readTime: String,
        tag: String,
        title: String,
        content: String,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                isLoadingMyArticles = true
                myArticleError = null

                val updated = repo.updateMyArticle(
                    id = id,
                    judul = title,
                    isi = content,
                    kategori = kategori,
                    waktuBaca = readTime,
                    tag = tag,
                    imageUri = imageUri
                )

                // Refresh seluruh list agar urutan tetap benar setelah update
                myArticles = repo.getMyArticles().sortedByDescending { it.id }
                loadArticles() // Sinkronkan ke publik jika artikel sudah terupload

                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                val msg = e.message ?: "Gagal mengupdate artikel"
                myArticleError = msg
                onError(msg)
            } finally {
                isLoadingMyArticles = false
            }
        }
    }
}