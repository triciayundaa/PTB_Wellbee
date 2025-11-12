package com.example.wellbee.frontend.screens.Edukasi

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.util.UUID
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Status artikel di "Artikel Saya"
enum class MyArticleStatus {
    DRAFT,      // disimpan sebagai draft
    UPLOADED,   // sudah diupload (tampil di EducationScreen)
    CANCELED    // upload pernah dibatalkan
}

// Model artikel buatan user
data class MyArticle(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val readTime: String,
    val tag: String,
    val content: String,
    val status: MyArticleStatus,
    val uploadedDate: String? = null      // 🔹 tanggal saat artikel di-upload
)

// Repository global untuk artikel buatan user
object MyArticleRepository {

    // list reaktif (bisa langsung dipakai di Compose)
    private val _articles: SnapshotStateList<MyArticle> = mutableStateListOf()
    val articles: SnapshotStateList<MyArticle> get() = _articles

    /** Artikel dengan status UPLOADED → tampil di EducationScreen */
    fun getUploaded(): List<MyArticle> =
        _articles.filter { it.status == MyArticleStatus.UPLOADED }

    /** Cari artikel user berdasarkan id (dipakai di ArticleDetailScreen dari EducationNavGraph/MainScreen) */
    fun findById(id: String): MyArticle? =
        _articles.find { it.id == id }

    /**
     * Simpan / update artikel dari PREVIEW.
     * Dipakai saat tombol:
     *  - "Tambahkan draft"  → status DRAFT
     *  - icon upload        → status UPLOADED
     *
     * Untuk status UPLOADED: kalau sebelumnya belum ada uploadedDate → isi tanggal hari ini.
     */
    fun upsertFromPreview(
        category: String,
        readTime: String,
        tag: String,
        title: String,
        content: String,
        status: MyArticleStatus
    ) {
        // cek artikel dengan kombinasi (title + category + tag)
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

    /** Ubah status (dipakai di MyArticlesScreen: Upload / Batalkan / Upload ulang) */
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

    /** Hapus artikel dari "Artikel Saya" */
    fun delete(articleId: String) {
        _articles.removeAll { it.id == articleId }
    }

    // 🔹 Helper untuk format tanggal "19 Oktober 2025" dalam bahasa Indonesia
    private fun getTodayFormatted(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
        return today.format(formatter)
    }
}
