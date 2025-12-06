package com.example.wellbee.data

import android.content.Context
import com.example.wellbee.data.model.SleepData
import com.example.wellbee.data.model.SleepRequest
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
            val response = api.getSportHistory()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal memuat riwayat"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSport(id: Int): Result<String> {
        return try {
            val res = api.deleteSport(id)
            if (res.isSuccessful) {
                Result.success("Berhasil menghapus")
            } else {
                Result.failure(Exception("Gagal hapus data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSport(id: Int, req: SportRequest): Result<String> {
        return try {
            val response = api.updateSport(id, req)
            if (response.isSuccessful) {
                Result.success("Berhasil update sport")
            } else {
                Result.failure(Exception("Gagal update sport"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // =========================
// SLEEP
// =========================

    suspend fun catatTidur(req: SleepRequest): Result<String> {
        return try {
            val response = api.catatTidur(req)
            if (response.isSuccessful) {
                Result.success("Berhasil mencatat tidur")
            } else {
                Result.failure(Exception("Gagal mencatat tidur"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSleepHistory(): Result<List<SleepData>> {
        return try {
            val response = api.getSleepHistory()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal memuat riwayat tidur"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSleep(id: Int): Result<String> {
        return try {
            val res = api.deleteSleep(id)
            if (res.isSuccessful) {
                Result.success("Berhasil menghapus")
            } else {
                Result.failure(Exception("Gagal hapus data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSleep(id: Int, req: SleepRequest): Result<String> {
        return try {
            val response = api.updateSleep(id, req)
            if (response.isSuccessful) {
                Result.success("Update berhasil")
            } else {
                Result.failure(Exception("Gagal update tidur"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }





}
