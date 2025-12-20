package com.example.wellbee.data

// ==========================================
// 1. IMPORT (GABUNGAN DARI MAIN & FATHIYA & NAILAH)
// ==========================================
import com.example.wellbee.data.model.AddBookmarkRequest
import com.example.wellbee.data.model.BookmarkListResponse
import com.example.wellbee.data.model.CategoriesResponse
import com.example.wellbee.data.model.CreateArticleRequest
import com.example.wellbee.data.model.MessageResponse
import com.example.wellbee.data.model.MyArticlesResponse
import com.example.wellbee.data.model.PublicArticlesResponse
import com.example.wellbee.data.model.ResetPasswordRequest
import com.example.wellbee.data.model.SleepData
import com.example.wellbee.data.model.SleepRequest
import com.example.wellbee.data.model.SleepResponse
import com.example.wellbee.data.model.SportHistory
import com.example.wellbee.data.model.SportRequest
import com.example.wellbee.data.model.SportResponse
import com.example.wellbee.data.model.UpdateArticleStatusRequest
import com.example.wellbee.data.model.UpdateMyArticleResponse
import com.example.wellbee.data.model.UploadImageResponse
import com.example.wellbee.data.model.WeeklySportChartResponse
import com.example.wellbee.data.model.WeightData
import com.example.wellbee.data.model.WeightRequest
import com.example.wellbee.data.model.WeightResponse
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
// 2. DATA CLASSES (AUTH - GABUNGAN)
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
    val phone: String? = null // Digabungkan (Nullable biar aman)
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

// =======================
// MENTAL MODELS (PUNYA NAILAH)
// =======================
data class MoodRequest(
    val userId: Int,
    val emoji: String,
    val moodLabel: String,
    val moodScale: Int,
    val tanggal: String? = null
)

data class JournalRequest(
    val userId: Int,
    val triggerLabel: String? = null,
    val isiJurnal: String,
    val foto: String? = null,
    val audio: String? = null, // [BARU] Tambahkan ini agar audio terkirim ke backend
    val tanggal: String? = null
)

// =======================
// GENERAL API RESPONSE
// =======================
data class ApiResponse<T>(
    val status: String,
    val data: T? = null,
    val message: String? = null
)

data class InsertResponse(
    val id: Int
)

// ==========================
// 3. INTERFACE API SERVICE (FINAL)
// ==========================
interface ApiService {

    // ==========================
    // AUTHENTICATION
    // ==========================
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

    // ==========================
    // FISIK - OLAHRAGA (SPORT)
    // ==========================
    @POST("api/fisik/olahraga")
    suspend fun catatOlahraga(@Body req: SportRequest): Response<SportResponse>

    @DELETE("api/fisik/olahraga/{id}")
    suspend fun deleteSport(@Path("id") id: Int): Response<SportResponse>

    @GET("api/fisik/riwayat")
    suspend fun getSportHistory(): Response<List<SportHistory>>

    @PUT("api/fisik/olahraga/{id}")
    suspend fun updateSport(
        @Path("id") id: Int,
        @Body req: SportRequest
    ): Response<SportResponse>

    @GET("api/fisik/weekly")
    suspend fun getWeeklySport(): Response<WeeklySportChartResponse>

    // TOKEN FCM
    @POST("api/fisik/fcm-token")
    suspend fun updateFcmToken(@Body body: Map<String, String>): Response<Any>

    // ==========================
    // FISIK - TIDUR (SLEEP)
    // ==========================
    @POST("api/fisik/sleep")
    suspend fun catatTidur(@Body req: SleepRequest): Response<SleepResponse>

    @GET("api/fisik/sleep/riwayat")
    suspend fun getSleepHistory(): Response<List<SleepData>>

    @DELETE("api/fisik/sleep/{id}")
    suspend fun deleteSleep(@Path("id") id: Int): Response<SleepResponse>

    @PUT("api/fisik/sleep/{id}")
    suspend fun updateSleep(
        @Path("id") id: Int,
        @Body req: SleepRequest
    ): Response<SleepResponse>

    @GET("api/fisik/sleep/weekly")
    suspend fun getWeeklySleep(): Response<WeeklySportChartResponse>

    // ==========================
    // FISIK - BERAT BADAN (WEIGHT)
    // ==========================
    @POST("api/fisik/weight")
    suspend fun catatWeight(
        @Body req: WeightRequest
    ): Response<WeightResponse>

    @GET("api/fisik/weight/riwayat")
    suspend fun getWeightHistory(): Response<List<WeightData>>

    @DELETE("api/fisik/weight/{id}")
    suspend fun deleteWeight(@Path("id") id: Int): Response<Unit>

    @PUT("api/fisik/weight/{id}")
    suspend fun updateWeight(
        @Path("id") id: Int,
        @Body req: WeightRequest
    ): Response<Unit>

    // ==========================
    // EDUKASI - ARTIKEL (PUBLIK)
    // ==========================
    @GET("api/edukasi/articles")
    suspend fun getPublicArticles(
        @Query("search") search: String? = null
    ): PublicArticlesResponse

    @GET("api/edukasi/categories")
    suspend fun getCategories(): CategoriesResponse

    @Multipart
    @POST("api/upload/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>

    // ==========================
    // EDUKASI - ARTIKEL SAYA (MY ARTICLES)
    // ==========================
    @POST("api/edukasi/my-articles")
    suspend fun createMyArticle(
        @Body request: CreateArticleRequest
    ): Response<MessageResponse>

    @GET("api/edukasi/my-articles")
    suspend fun getMyArticles(): MyArticlesResponse

    @PATCH("api/edukasi/my-articles/{id}/status")
    suspend fun updateMyArticleStatus(
        @Path("id") id: Int,
        @Body request: UpdateArticleStatusRequest
    ): Response<MessageResponse>

    @DELETE("api/edukasi/my-articles/{id}")
    suspend fun deleteMyArticle(
        @Path("id") id: Int
    ): Response<MessageResponse>

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

    // ==========================
    // EDUKASI - BOOKMARK
    // ==========================
    @GET("api/edukasi/bookmarks")
    suspend fun getBookmarks(): BookmarkListResponse

    @POST("api/edukasi/bookmarks")
    suspend fun addBookmark(
        @Body body: AddBookmarkRequest
    ): MessageResponse

    @DELETE("api/edukasi/bookmarks/{id}")
    suspend fun deleteBookmark(
        @Path("id") bookmarkId: Int
    ): MessageResponse

    @PATCH("api/edukasi/bookmarks/{id}/read")
    suspend fun markBookmarkAsRead(
        @Path("id") bookmarkId: Int
    ): MessageResponse

    // =======================
    // MENTAL - MOOD (NAILAH)
    // =======================
    @POST("api/mental/mood")
    suspend fun postMood(@Body request: MoodRequest): Response<ApiResponse<InsertResponse>>

    // =======================
    // MENTAL - JURNAL (NAILAH)
    // =======================
    @POST("api/mental/jurnal")
    suspend fun postJournal(@Body request: JournalRequest): Response<ApiResponse<InsertResponse>>
}