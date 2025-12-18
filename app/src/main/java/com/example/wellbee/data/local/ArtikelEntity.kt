package com.example.wellbee.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artikel")
data class ArtikelEntity(
    @PrimaryKey val id: Int,
    val judul: String,
    val isi: String,
    val kategori: String?,
    val waktuBaca: String?,
    val tag: String?,
    val gambarUrl: String?,
    val tanggal: String,
    val tanggalEpoch: Long,
    val jenis: String,
    val userId: Int?,
    val authorName: String?
)
