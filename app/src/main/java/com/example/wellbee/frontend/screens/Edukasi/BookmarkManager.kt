package com.example.wellbee.frontend.screens.Edukasi

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class BookmarkItem(
    val articleId: String,
    val isRead: Boolean = false
)

object BookmarkManager {

    val bookmarks: SnapshotStateList<BookmarkItem> = mutableStateListOf()

    fun isBookmarked(articleId: String): Boolean {
        return bookmarks.any { it.articleId == articleId }
    }

    fun toggleBookmark(articleId: String) {
        val index = bookmarks.indexOfFirst { it.articleId == articleId }
        if (index >= 0) {
            bookmarks.removeAt(index)
        } else {
            bookmarks.add(BookmarkItem(articleId = articleId, isRead = false))
        }
    }


    fun markAsRead(articleId: String) {
        val index = bookmarks.indexOfFirst { it.articleId == articleId }
        if (index >= 0) {
            val current = bookmarks[index]
            if (!current.isRead) {
                bookmarks[index] = current.copy(isRead = true)
            }
        }

    }
}
