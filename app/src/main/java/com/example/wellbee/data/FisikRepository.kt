package com.example.wellbee.data

import android.content.Context
import com.example.wellbee.data.model.SportHistory
import com.example.wellbee.data.model.SportRequest
import com.example.wellbee.data.model.SportResponse
import retrofit2.Response

class FisikRepository(private val context: Context) {

    private val api = RetrofitClient.getInstance(context)

    // POST sport
    suspend fun catatOlahraga(req: SportRequest): Response<SportResponse> {
        return api.catatOlahraga(req)
    }

    // GET sport history
    suspend fun getSportHistory(userId: Int): Result<List<SportHistory>> {
        return try {
            val response = api.getSportHistory(userId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal memuat riwayat"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
