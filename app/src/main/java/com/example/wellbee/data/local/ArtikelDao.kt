package com.example.wellbee.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArtikelDao {

    @Query("SELECT * FROM artikel ORDER BY tanggalEpoch DESC")
    suspend fun getAllOnce(): List<ArtikelEntity>

    @Query("""
        SELECT * FROM artikel
        WHERE (judul LIKE '%' || :keyword || '%'
            OR isi LIKE '%' || :keyword || '%'
            OR IFNULL(tag,'') LIKE '%' || :keyword || '%')
        ORDER BY tanggalEpoch DESC
    """)
    suspend fun searchOnce(keyword: String): List<ArtikelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ArtikelEntity>)

    @Query("DELETE FROM artikel")
    suspend fun clearAll()

    @androidx.room.Transaction
    suspend fun clearAndInsert(items: List<ArtikelEntity>) {
        clearAll()
        upsertAll(items)
    }
}
