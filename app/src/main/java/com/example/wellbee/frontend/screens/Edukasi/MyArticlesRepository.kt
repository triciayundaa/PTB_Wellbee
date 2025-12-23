package com.example.wellbee.frontend.screens.Edukasi

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.util.UUID
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


enum class MyArticleStatus {
    DRAFT,
    UPLOADED,
    CANCELED
}

data class MyArticle(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val readTime: String,
    val tag: String,
    val content: String,
    val status: MyArticleStatus,
    val uploadedDate: String? = null
)

object MyArticleRepository {

    private val _articles: SnapshotStateList<MyArticle> = mutableStateListOf()
    val articles: SnapshotStateList<MyArticle> get() = _articles

    fun getUploaded(): List<MyArticle> =
        _articles.filter { it.status == MyArticleStatus.UPLOADED }

    fun findById(id: String): MyArticle? =
        _articles.find { it.id == id }


    fun upsertFromPreview(
        category: String,
        readTime: String,
        tag: String,
        title: String,
        content: String,
        status: MyArticleStatus
    ) {

        val existingIndex = _articles.indexOfFirst {
            it.title == title &&
                    it.category == category &&
                    it.tag == tag
        }

        if (existingIndex >= 0) {
            val old = _articles[existingIndex]

            val newUploadedDate =
                if (status == MyArticleStatus.UPLOADED && old.uploadedDate == null) {
                    getTodayFormatted()
                } else {
                    old.uploadedDate
                }

            _articles[existingIndex] = old.copy(
                readTime = readTime,
                content = content,
                status = status,
                uploadedDate = newUploadedDate
            )
        } else {
            _articles.add(
                MyArticle(
                    title = title,
                    category = category,
                    readTime = readTime,
                    tag = tag,
                    content = content,
                    status = status,
                    uploadedDate = if (status == MyArticleStatus.UPLOADED) {
                        getTodayFormatted()
                    } else {
                        null
                    }
                )
            )
        }
    }

    fun updateStatus(articleId: String, newStatus: MyArticleStatus) {
        val idx = _articles.indexOfFirst { it.id == articleId }
        if (idx >= 0) {
            val old = _articles[idx]

            val newUploadedDate =
                if (newStatus == MyArticleStatus.UPLOADED && old.uploadedDate == null) {
                    // kalau baru di-upload dari draft / canceled → isi tanggal sekarang
                    getTodayFormatted()
                } else {
                    old.uploadedDate
                }

            _articles[idx] = old.copy(
                status = newStatus,
                uploadedDate = newUploadedDate
            )
        }
    }

    fun delete(articleId: String) {
        _articles.removeAll { it.id == articleId }
    }

    private fun getTodayFormatted(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
        return today.format(formatter)
    }
}
