package com.example.wellbee.data

import android.content.Context

class AuthRepository(private val context: Context) {
    private val api = RetrofitClient.getInstance(context)
    private val prefs = context.getSharedPreferences("wellbee_prefs", Context.MODE_PRIVATE)

    suspend fun login(email: String, pass: String): Result<String> {
        return try {
            val response = api.login(LoginRequest(email, pass))
            if (response.isSuccessful && response.body() != null) {

                val token = response.body()!!.token
                val userId = response.body()!!.user?.id ?: -1

                prefs.edit()
                    .putString("auth_token", token)
                    .putInt("user_id", userId)
                    .apply()

                Result.success("Login Berhasil!")
            } else {
                Result.failure(Exception("Login Gagal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun register(username: String, email: String, pass: String, phone: String): Result<String> {
        return try {
            // Masukkan phone ke request
            val response = api.register(RegisterRequest(username, email, pass, phone))

            if (response.isSuccessful) {
                Result.success("Registrasi Berhasil!")
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Registrasi Gagal"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun getUserId(): Int {
        return prefs.getInt("user_id", -1)
    }
}

