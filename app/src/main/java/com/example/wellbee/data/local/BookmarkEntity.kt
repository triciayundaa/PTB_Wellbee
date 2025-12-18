package com.example.wellbee.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark")
data class BookmarkEntity(
    @PrimaryKey val bookmarkId: Int,

    val artikelId: Int,
    val jenis: String,
    val sudahDibaca: Int,

    val judul: String,
    val isi: String,
    val kategori: String?,
    val waktuBaca: String?,
    val tag: String?,
    val gambarUrl: String?,
    val tanggal: String?,
    val userId: Int?,
    val isDeleted: Int = 0
)
