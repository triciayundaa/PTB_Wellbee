package com.example.wellbee.data

import com.example.wellbee.data.model.CategoriesResponse
import com.example.wellbee.data.model.PublicArticlesResponse
import com.example.wellbee.data.model.SportRequest
import com.example.wellbee.data.model.SportResponse
import com.example.wellbee.data.model.CreateArticleRequest
import com.example.wellbee.data.model.UploadImageResponse
import retrofit2.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import okhttp3.MultipartBody


// ==========================
// AUTH REQUEST/RESPONSE
// ==========================
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val user: UserData?
)

data class UserData(
    val id: Int,
    val username: String,
    val email: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val phone: String
)

data class RegisterResponse(
    val message: String
)

data class CategoriesResponse(
    val categories: List<String>
)


// ==========================
// API SERVICE
// ==========================
interface ApiService {

    // ===== AUTH =====
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    // ===== FISIK / OLAHRAGA =====
    @POST("api/fisik/olahraga")
    fun catatOlahraga(@Body request: SportRequest): Call<SportResponse>

    // ===== EDUKASI =====

    // Get artikel publik (static + user uploaded)
    @GET("api/edukasi/articles")
    suspend fun getPublicArticles(): PublicArticlesResponse

    // Get kategori edukasi (daftar tetap dari backend)
    @GET("api/edukasi/categories")
    suspend fun getCategories(): CategoriesResponse

    @POST("api/edukasi/my-articles")
    suspend fun createMyArticle(
        @Body request: CreateArticleRequest
    ): Response<Any>

    @Multipart
    @POST("api/upload/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>
}
