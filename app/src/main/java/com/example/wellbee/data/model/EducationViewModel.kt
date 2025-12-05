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

    var articles by mutableStateOf<List<PublicArticleDto>>(emptyList())
        private set

    // daftar kategori dari backend (tanpa "Semua")
    var categories by mutableStateOf<List<String>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // status upload artikel: null / SUCCESS / FAILED / ERROR
    var uploadStatus by mutableStateOf<String?>(null)
        private set

    fun loadArticles() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                articles = repo.getPublicArticles()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Gagal memuat artikel"
            } finally {
                isLoading = false
            }
        }
    }

    // panggil endpoint GET /api/edukasi/categories
    fun loadCategories() {
        viewModelScope.launch {
            try {
                val result = repo.getCategories()
                categories = result
            } catch (e: Exception) {
                e.printStackTrace()
                // kalau gagal, biarkan saja: UI tetap jalan
            }
        }
    }

    /**
     * Upload artikel:
     * - kalau imageUri != null → upload gambar dulu → dapat url → kirim bareng artikel
     * - kalau imageUri == null → kirim artikel tanpa gambar_url
     */
    fun uploadArticleWithImage(
        imageUri: Uri?,
        kategori: String,
        readTime: String,
        tag: String,
        title: String,
        content: String
    ) {
        viewModelScope.launch {
            try {
                uploadStatus = null

                // 1. upload gambar kalau ada
                val gambarUrl: String? = if (imageUri != null) {
                    repo.uploadImage(imageUri)
                } else {
                    null
                }

                // 2. kirim artikel ke backend
                val success = repo.createMyArticle(
                    kategori = kategori,
                    readTime = readTime,
                    tag = tag,
                    title = title,
                    content = content,
                    gambarUrl = gambarUrl
                )

                uploadStatus = if (success) "SUCCESS" else "FAILED"
            } catch (e: Exception) {
                e.printStackTrace()
                uploadStatus = "ERROR"
            }
        }
    }

    // OPTIONAL: supaya kode lama uploadArticle masih bisa dipakai
    fun uploadArticle(
        kategori: String,
        readTime: String,
        tag: String,
        title: String,
        content: String
    ) {
        uploadArticleWithImage(
            imageUri = null,
            kategori = kategori,
            readTime = readTime,
            tag = tag,
            title = title,
            content = content
        )
    }
}
