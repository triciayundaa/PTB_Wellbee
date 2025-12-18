package com.example.wellbee.data.model

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellbee.data.RetrofitClient
import kotlinx.coroutines.launch

class ProfileViewModel(context: Context) : ViewModel() {
    private val api = RetrofitClient.getInstance(context)

    var fullName by mutableStateOf("Memuat...")
    var email by mutableStateOf("Memuat...")
    var phoneNumber by mutableStateOf("Memuat...")
    var isLoading by mutableStateOf(false)

    fun fetchUserProfile() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = api.getProfile()
                if (response.isSuccessful && response.body() != null) {
                    val userData = response.body()!!
                    fullName = userData.username
                    email = userData.email
                    // 🔹 Ambil data phone (pastikan UserData di ApiService sudah punya field phone)
                    phoneNumber = userData.phone ?: "Tidak ada nomor"
                } else {
                    fullName = "Gagal memuat"
                    email = "Gagal memuat"
                    phoneNumber = "-"
                }
            } catch (e: Exception) {
                fullName = "Koneksi Error"
                phoneNumber = "-"
            } finally {
                isLoading = false
            }
        }
    }
}