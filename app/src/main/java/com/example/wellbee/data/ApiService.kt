package com.example.wellbee.data

import com.example.wellbee.data.model.SportHistory
import com.example.wellbee.data.model.SportModel
import com.example.wellbee.data.model.SportRequest // Pastikan import sesuai package
import com.example.wellbee.data.model.SportResponse
import retrofit2.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Model data untuk Login (Request & Response)
data class LoginRequest(
    val email: String,  // Ubah nama ini biar backend Node.js membacanya sebagai 'email'
    val password: String
)
data class LoginResponse(val message: String, val token: String, val user: UserData?)
data class UserData(val id: Int, val username: String, val email: String)

interface ApiService {
    @POST("api/auth/login") // Sesuaikan path ini dengan backendmu
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    // Endpoint baru untuk olahraga
    @POST("api/fisik/olahraga")
    suspend fun catatOlahraga(@Body req: SportRequest): Response<SportResponse>


    @GET("api/fisik/riwayat/{userId}")
    suspend fun getSportHistory(
        @Path("userId") userId: Int
    ): Response<List<SportHistory>>


}

//REGISTER
data class RegisterRequest(
    val username: String, // Kita akan isi ini dengan 'Full Name' dari UI
    val email: String,
    val password: String,
    val phone: String
)

data class RegisterResponse(
    val message: String
    // Kita tidak butuh data user detail untuk sekarang, cukup pesan sukses
)

