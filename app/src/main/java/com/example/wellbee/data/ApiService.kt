package com.example.wellbee.data

import com.example.wellbee.data.model.SportRequest
import com.example.wellbee.data.model.SportResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// =======================
// AUTH
// =======================
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

// =======================
// FISIK
// =======================
interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/fisik/olahraga")
    fun catatOlahraga(@Body request: SportRequest): Call<SportResponse>

    // =======================
    // MENTAL - MOOD
    // =======================
    @POST("api/mental/mood")
    suspend fun postMood(@Body request: MoodRequest): Response<ApiResponse<InsertResponse>>

    // =======================
    // MENTAL - JURNAL
    // =======================
    @POST("api/mental/jurnal")
    suspend fun postJournal(@Body request: JournalRequest): Response<ApiResponse<InsertResponse>>
}

// =======================
// MENTAL MODELS
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
