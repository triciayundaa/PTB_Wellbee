package com.example.wellbee.data.model

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.IOException

class EducationViewModel(context: Context) : ViewModel() {

    private val repo = EducationRepository(context)

    // ==========================================
    // 🔹 DRAFT STATE
    // ==========================================
    var draftCategory by mutableStateOf("")
    var draftReadTime by mutableStateOf("")
    var draftTag by mutableStateOf("")
    var draftTitle by mutableStateOf("")
    var draftContent by mutableStateOf("")
    var draftImageUri by mutableStateOf<Uri?>(null)

    fun clearDraft() {
        draftCategory = ""
        draftReadTime = ""
        draftTag = ""
        draftTitle = ""
        draftContent = ""
        draftImageUri = null
    }

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
                // Sorting sekarang dihandle oleh Repository (Room DESC) dan UI (sortedWith)
                articles = repo.getPublicArticles()
            } catch (e: IOException) {
                errorMessage = "Tidak ada koneksi internet. Silakan coba lagi."
            } catch (e: Exception) {
                errorMessage = e.message ?: "Gagal memuat artikel"
            } finally {
                isLoading = false
            }
        }
    }

    fun searchArticles(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadArticles()
                return@launch
            }

            isLoading = true
            errorMessage = null
            try {
                // 🔹 PERBAIKAN: Hapus sorting string manual di sini agar konsisten dengan loadArticles
                articles = repo.getPublicArticles(query)
            } catch (e: IOException) {
                errorMessage = "Pencarian gagal: Periksa koneksi internet Anda."
            } catch (e: Exception) {
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
    // UPLOAD ARTIKEL
    // ==========================
    fun uploadArticleWithImage(
        imageUri: Uri?,
        kategori: String,
        readTime: String,
        tag: String,
        title: String,
        content: String,
        status: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            uploadStatus = null
            errorMessage = null
            try {
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
                    clearDraft()
                    loadMyArticles()
                    loadArticles()
                    onSuccess()
                } else {
                    uploadStatus = "FAILED"
                    errorMessage = "Gagal menyimpan artikel ke server."
                }
            } catch (e: IOException) {
                uploadStatus = "ERROR_CONNECTION"
                errorMessage = "Gagal upload: Koneksi internet terputus."
            } catch (e: Exception) {
                uploadStatus = "ERROR"
                errorMessage = e.message ?: "Terjadi kesalahan tidak terduga."
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
            } catch (e: IOException) {
                bookmarkError = "Koneksi terputus. Mengambil data lokal."
            } catch (e: Exception) {
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
                bookmarkError = "Gagal menambah bookmark: Cek internet Anda."
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
                bookmarkError = "Gagal menghapus bookmark."
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
                bookmarkError = "Gagal update status baca."
            }
        }
    }

    // ==========================
    // ARTIKEL SAYA
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
                myArticles = repo.getMyArticles().sortedByDescending { it.id }
            } catch (e: IOException) {
                myArticleError = "Koneksi internet terputus. Silakan coba lagi."
            } catch (e: Exception) {
                myArticleError = e.message ?: "Gagal memuat artikel saya"
            } finally {
                isLoadingMyArticles = false
            }
        }
    }

    fun changeMyArticleStatus(articleId: Int, newStatus: String) {
        viewModelScope.launch {
            try {
                myArticleError = null
                val success = repo.updateMyArticleStatus(articleId, newStatus)
                if (success) {
                    myArticles = myArticles.map {
                        if (it.id == articleId) it.copy(status = newStatus) else it
                    }
                    loadArticles()
                }
            } catch (e: IOException) {
                myArticleError = "Gagal mengubah status: Cek koneksi internet."
            } catch (e: Exception) {
                myArticleError = "Gagal memperbarui status artikel."
            }
        }
    }

    fun deleteMyArticle(articleId: Int) {
        viewModelScope.launch {
            try {
                myArticleError = null
                val success = repo.deleteMyArticle(articleId)
                if (success) {
                    myArticles = myArticles.filter { it.id != articleId }
                    loadArticles()
                }
            } catch (e: IOException) {
                myArticleError = "Gagal menghapus: Koneksi internet terputus."
            } catch (e: Exception) {
                myArticleError = "Terjadi kesalahan saat menghapus artikel."
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
            isLoadingMyArticles = true
            myArticleError = null
            errorMessage = null
            try {
                repo.updateMyArticle(
                    id = id,
                    judul = title,
                    isi = content,
                    kategori = kategori,
                    waktuBaca = readTime,
                    tag = tag,
                    imageUri = imageUri
                )

                myArticles = repo.getMyArticles().sortedByDescending { it.id }
                clearDraft()
                loadArticles()
                onSuccess()
            } catch (e: IOException) {
                val msg = "Gagal update: Koneksi internet terputus."
                myArticleError = msg
                errorMessage = msg
                onError(msg)
            } catch (e: Exception) {
                val msg = e.message ?: "Gagal mengupdate artikel"
                myArticleError = msg
                errorMessage = msg
                onError(msg)
            } finally {
                isLoadingMyArticles = false
            }
        }
    }
}