package com.example.wellbee.frontend.screens.Edukasi

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

// 📌 Item bookmark: simpan id artikel + status baca
data class BookmarkItem(
    val articleId: String,
    val isRead: Boolean = false
)

object BookmarkManager {

    // 📌 List bookmark global (dipakai di Education, Home, Bookmark, Detail)
    val bookmarks: SnapshotStateList<BookmarkItem> = mutableStateListOf()

    // Cek apakah artikel sudah di-bookmark
    fun isBookmarked(articleId: String): Boolean {
        return bookmarks.any { it.articleId == articleId }
    }

    // Toggle bookmark: kalau sudah ada → hapus, kalau belum → tambahkan
    fun toggleBookmark(articleId: String) {
        val index = bookmarks.indexOfFirst { it.articleId == articleId }
        if (index >= 0) {
            // Sudah ada → hapus dari bookmark
            bookmarks.removeAt(index)
        } else {
            // Belum ada → tambahkan dengan status belum dibaca
            bookmarks.add(BookmarkItem(articleId = articleId, isRead = false))
        }
    }

    // Tandai artikel sebagai "sudah dibaca"
    fun markAsRead(articleId: String) {
        val index = bookmarks.indexOfFirst { it.articleId == articleId }
        if (index >= 0) {
            val current = bookmarks[index]
            if (!current.isRead) {
                bookmarks[index] = current.copy(isRead = true)
            }
        }
        // Kalau belum pernah di-bookmark, dibiarkan saja
        // (BookmarkScreen hanya menampilkan artikel yang sudah di-bookmark)
    }
}
