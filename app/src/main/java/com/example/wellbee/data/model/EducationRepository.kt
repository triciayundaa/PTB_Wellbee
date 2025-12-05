package com.example.wellbee.data.model

import android.content.Context
import android.net.Uri
import com.example.wellbee.data.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class EducationRepository(private val context: Context) {

    private val api = RetrofitClient.getInstance(context)

    // BASE URL backend (tanpa / di belakang, biar concat rapi)
    private val baseUrl = "http://10.180.186.27:3000"

    // 🔹 Ambil artikel publik (static + user uploaded)
    suspend fun getPublicArticles(): List<PublicArticleDto> {
        val response = api.getPublicArticles()

        return response.articles.map { article ->
            val fixedUrl = article.gambarUrl?.let { url ->
                when {
                    url.startsWith("http") -> url
                    url.startsWith("/") -> baseUrl + url        // "/uploads/xxx.jpg"
                    else -> "$baseUrl/$url"                     // "uploads/xxx.jpg"
                }
            }

            println("FINAL GAMBAR URL = $fixedUrl")
            article.copy(gambarUrl = fixedUrl)
        }
    }

    // 🔹 Ambil kategori dari backend
    suspend fun getCategories(): List<String> {
        val response = api.getCategories()
        return response.categories
    }

    // 🔹 Upload gambar ke backend, balikan URL (biasanya "/uploads/xxx.jpg")
    suspend fun uploadImage(imageUri: Uri): String {
        val contentResolver = context.contentResolver
        val stream = contentResolver.openInputStream(imageUri)
            ?: throw Exception("Tidak bisa membuka gambar")

        val bytes = stream.readBytes()
        stream.close()

        val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())

        val part = MultipartBody.Part.createFormData(
            name = "image",
            filename = "artikel_${System.currentTimeMillis()}.jpg",
            body = requestBody
        )

        val response = api.uploadImage(part)
        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Upload gambar gagal: ${response.code()}")
        }

        // contoh: "/uploads/artikel_123.jpg"
        return response.body()!!.url
    }

    // 🔹 Create artikel user (opsional pakai url gambar)
    suspend fun createMyArticle(
        kategori: String,
        readTime: String,
        tag: String,
        title: String,
        content: String,
        gambarUrl: String?
    ): Boolean {

        val body = CreateArticleRequest(
            kategori = kategori,
            waktu_baca = readTime,
            tag = tag,
            judul = title,
            isi = content,
            gambar_url = gambarUrl
        )

        val response = api.createMyArticle(body)
        return response.isSuccessful
    }
}
