package com.example.wellbee.data.model

import com.example.wellbee.data.local.BookmarkEntity

fun BookmarkDto.toEntity(isDeleted: Int = 0): BookmarkEntity = BookmarkEntity(
    bookmarkId = bookmarkId,
    artikelId = artikelId,
    jenis = jenis,
    sudahDibaca = sudahDibaca,
    judul = judul,
    isi = isi,
    kategori = kategori,
    waktuBaca = waktuBaca,
    tag = tag,
    gambarUrl = gambarUrl,
    tanggal = tanggal,
    userId = userId,
    isDeleted = isDeleted
)

fun BookmarkEntity.toDto(): BookmarkDto = BookmarkDto(
    bookmarkId = bookmarkId,
    artikelId = artikelId,
    jenis = jenis,
    sudahDibaca = sudahDibaca,
    judul = judul,
    isi = isi,
    kategori = kategori,
    waktuBaca = waktuBaca,
    tag = tag,
    gambarUrl = gambarUrl,
    tanggal = tanggal,
    userId = userId
)
