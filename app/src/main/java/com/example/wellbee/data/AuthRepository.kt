package com.example.wellbee.data

import android.content.Context

class AuthRepository(private val context: Context) {
    private val api = RetrofitClient.getInstance(context)
    private val sessionManager = SessionManager(context)

    suspend fun login(email: String, pass: String): Result<String> {
        return try {
            val response = api.login(LoginRequest(email, pass))
            if (response.isSuccessful && response.body() != null) {
                val token = response.body()!!.token

                // Simpan token menggunakan sessionManager
                sessionManager.saveToken(token)

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

    fun logout() {
        sessionManager.clearSession()
    }
}