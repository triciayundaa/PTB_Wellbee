package com.example.wellbee.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookmarkDao {

    // ✅ yang tampil di UI (yang belum di-delete)
    @Query("SELECT * FROM bookmark WHERE isDeleted = 0 ORDER BY bookmarkId DESC")
    suspend fun getAllOnce(): List<BookmarkEntity>

    // ✅ ambil semua (termasuk yang pending delete)
    @Query("SELECT * FROM bookmark ORDER BY bookmarkId DESC")
    suspend fun getAllIncludingDeletedOnce(): List<BookmarkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<BookmarkEntity>)

    // ✅ soft delete (offline-first)
    @Query("UPDATE bookmark SET isDeleted = 1 WHERE bookmarkId = :id")
    suspend fun softDeleteById(id: Int)

    // ✅ undo delete (opsional)
    @Query("UPDATE bookmark SET isDeleted = 0 WHERE bookmarkId = :id")
    suspend fun undoSoftDeleteById(id: Int)

    // ✅ daftar id yang pending delete
    @Query("SELECT bookmarkId FROM bookmark WHERE isDeleted = 1")
    suspend fun getPendingDeleteIds(): List<Int>

    // ✅ hapus permanen (setelah server sukses)
    @Query("DELETE FROM bookmark WHERE bookmarkId = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE bookmark SET sudahDibaca = 1 WHERE bookmarkId = :id")
    suspend fun markAsReadLocal(id: Int)

    @Query("DELETE FROM bookmark")
    suspend fun clearAll()
}
