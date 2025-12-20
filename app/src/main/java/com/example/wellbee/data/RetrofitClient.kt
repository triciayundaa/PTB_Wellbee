package com.example.wellbee.data

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Ganti 10.0.2.2 jika pakai Emulator.
    // Ganti dengan IP Laptop (misal 192.168.1.x) jika pakai HP fisik.
    private const val BASE_URL = "http://192.168.1.9:3000/"

    fun getInstance(context: Context): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context)) // Pasang interceptor token
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}