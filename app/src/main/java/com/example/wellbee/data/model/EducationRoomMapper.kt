package com.example.wellbee.data.model

import com.example.wellbee.data.local.ArtikelEntity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private fun parseTanggalToEpoch(tanggal: String?): Long {
    if (tanggal.isNullOrBlank()) return 0L
    val formats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd"
    )

    for (pattern in formats) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = sdf.parse(tanggal)
            if (date != null) return date.time
        } catch (_: Exception) {}
    }
    return tanggal.hashCode().toLong()
}

fun PublicArticleDto.toEntity(): ArtikelEntity = ArtikelEntity(
    id = id,
    judul = judul,
    isi = isi,
    kategori = kategori,
    waktuBaca = waktuBaca,
    tag = tag,
    gambarUrl = gambarUrl,
    tanggal = tanggal,
    tanggalEpoch = parseTanggalToEpoch(tanggal),
    jenis = jenis,
    userId = userId,
    authorName = authorName
)

fun ArtikelEntity.toDto(): PublicArticleDto = PublicArticleDto(
    id = id,
    judul = judul,
    isi = isi,
    kategori = kategori,
    waktuBaca = waktuBaca,
    tag = tag,
    gambarUrl = gambarUrl,
    tanggal = tanggal,
    jenis = jenis,
    userId = userId,
    authorName = authorName
)