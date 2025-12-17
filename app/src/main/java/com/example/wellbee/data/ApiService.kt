package com.example.wellbee.data

import com.example.wellbee.data.model.SleepData
import com.example.wellbee.data.model.SleepRequest
import com.example.wellbee.data.model.SleepResponse
import com.example.wellbee.data.model.SportHistory
import com.example.wellbee.data.model.SportModel
import com.example.wellbee.data.model.SportRequest // Pastikan import sesuai package
import com.example.wellbee.data.model.SportResponse
import com.example.wellbee.data.model.WeeklySportChartResponse
import com.example.wellbee.data.model.WeightData
import com.example.wellbee.data.model.WeightRequest
import com.example.wellbee.data.model.WeightResponse
import retrofit2.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    // ====== SLEEP ======
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

    // =======================
// WEIGHT (FIX)
// =======================
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

