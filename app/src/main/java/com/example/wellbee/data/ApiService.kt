package com.example.wellbee.data

import com.example.wellbee.data.model.CategoriesResponse
import com.example.wellbee.data.model.PublicArticlesResponse
import com.example.wellbee.data.model.SportRequest
import com.example.wellbee.data.model.SportResponse
import com.example.wellbee.data.model.CreateArticleRequest
import com.example.wellbee.data.model.UploadImageResponse
import com.example.wellbee.data.model.BookmarkListResponse
import com.example.wellbee.data.model.AddBookmarkRequest
import com.example.wellbee.data.model.MessageResponse
import com.example.wellbee.data.model.MyArticlesResponse
import com.example.wellbee.data.model.ResetPasswordRequest
import com.example.wellbee.data.model.UpdateArticleStatusRequest
import com.example.wellbee.data.model.UpdateMyArticleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
    val email: String,
    val phone: String?
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


// ==========================
// API SERVICE
// ==========================
interface ApiService {

    // ===== AUTH =====
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/auth/me")
    suspend fun getProfile(): Response<UserData>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<MessageResponse>


    // ===== FISIK / OLAHRAGA =====
    @POST("api/fisik/olahraga")
    fun catatOlahraga(@Body request: SportRequest): Call<SportResponse>

    // ===== EDUKASI - ARTIKEL =====

    // Get artikel publik (static + user uploaded)
    @GET("api/edukasi/articles")
    suspend fun getPublicArticles(
        @Query("search") search: String? = null
    ): PublicArticlesResponse

    // Get kategori edukasi (daftar tetap dari backend)
    @GET("api/edukasi/categories")
    suspend fun getCategories(): CategoriesResponse

    // Create artikel user
    @POST("api/edukasi/my-articles")
    suspend fun createMyArticle(
        @Body request: CreateArticleRequest
    ): Response<MessageResponse>

    // Upload gambar artikel
    @Multipart
    @POST("api/upload/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>

    // ===== EDUKASI - BOOKMARK =====

    // Ambil semua bookmark milik user
    @GET("api/edukasi/bookmarks")
    suspend fun getBookmarks(): BookmarkListResponse

    // Tambah bookmark baru
    @POST("api/edukasi/bookmarks")
    suspend fun addBookmark(
        @Body body: AddBookmarkRequest
    ): MessageResponse

    // Hapus bookmark
    @DELETE("api/edukasi/bookmarks/{id}")
    suspend fun deleteBookmark(
        @Path("id") bookmarkId: Int
    ): MessageResponse

    // Tandai bookmark sudah dibaca
    @PATCH("api/edukasi/bookmarks/{id}/read")
    suspend fun markBookmarkAsRead(
        @Path("id") bookmarkId: Int
    ): MessageResponse

    // ===== EDUKASI - ARTIKEL SAYA =====

    // Ambil artikel milik user login
    @GET("api/edukasi/my-articles")
    suspend fun getMyArticles(): MyArticlesResponse

    // Ubah status artikel saya (draft / uploaded / canceled)
    @PATCH("api/edukasi/my-articles/{id}/status")
    suspend fun updateMyArticleStatus(
        @Path("id") id: Int,
        @Body request: UpdateArticleStatusRequest
    ): Response<MessageResponse>

    // Hapus artikel saya
    @DELETE("api/edukasi/my-articles/{id}")
    suspend fun deleteMyArticle(
        @Path("id") id: Int
    ): Response<MessageResponse>

    // Update artikel saya (termasuk opsi ganti gambar)
    @Multipart
    @PUT("api/edukasi/my-articles/{id}")
    suspend fun updateMyArticle(
        @Path("id") id: Int,
        @Part("judul") judul: RequestBody,
        @Part("isi") isi: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("waktu_baca") waktuBaca: RequestBody,
        @Part("tag") tag: RequestBody,
        @Part gambar: MultipartBody.Part?
    ): UpdateMyArticleResponse
}
