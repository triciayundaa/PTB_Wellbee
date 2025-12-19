package com.example.wellbee.data

import android.content.Context
import android.util.Log
import com.example.wellbee.data.local.AppDatabase
import com.example.wellbee.data.local.SleepEntity
import com.example.wellbee.data.local.SportEntity
import com.example.wellbee.data.local.WeightEntity
import com.example.wellbee.data.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FisikRepository(private val context: Context) {

    private fun getUserId(): Int {
        val prefs = context.getSharedPreferences("wellbee_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("user_id", -1)
    }

    private val api = RetrofitClient.getInstance(context)

    // PERBAIKAN: Mengganti getDatabase() menjadi getInstance() sesuai hasil merge AppDatabase
    private val dao = AppDatabase.getInstance(context).fisikDao()

    // =========================================================
    // SPORT
    // =========================================================

    suspend fun catatOlahraga(req: SportRequest): Result<String> {
        return try {
            val response = api.catatOlahraga(req)
            if (response.isSuccessful && response.body()?.data != null) {
                val body = response.body()!!.data!!

                // Simpan ke DB Lokal (Primary Key = id dari server)
                dao.insertSport(
                    SportEntity(
                        id = body.id,
                        userId = getUserId(),
                        jenisOlahraga = body.jenisOlahraga,
                        durasiMenit = body.durasiMenit,
                        kaloriTerbakar = body.kaloriTerbakar,
                        tanggal = body.tanggal,
                        foto = body.foto
                    )
                )
                Result.success("Berhasil mencatat olahraga")
            } else {
                Result.failure(Exception("Gagal mencatat olahraga"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSportHistory(): Result<List<SportHistory>> {
        val userId = getUserId()

        return try {
            val response = api.getSportHistory()
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!

                // 🔥 OPTIMASI: Batch Insert (Lebih Cepat)
                val entities = list.map {
                    SportEntity(
                        id = it.id,
                        userId = userId,
                        jenisOlahraga = it.jenisOlahraga,
                        durasiMenit = it.durasiMenit,
                        kaloriTerbakar = it.kaloriTerbakar,
                        tanggal = it.tanggal ?: "",
                        foto = it.foto
                    )
                }
                dao.insertAllSport(entities) // Insert sekaligus

                Result.success(list)
            } else {
                // Ambil dari Cache Lokal jika Offline
                val local = dao.getSportHistory(userId)
                Result.success(local.map {
                    SportHistory(
                        it.id,
                        it.userId,
                        it.jenisOlahraga,
                        it.durasiMenit,
                        it.kaloriTerbakar,
                        it.tanggal,
                        it.foto
                    )
                })
            }
        } catch (e: Exception) {
            // Ambil dari Cache Lokal jika Error Jaringan
            val local = dao.getSportHistory(userId)
            Result.success(local.map {
                SportHistory(
                    it.id,
                    it.userId,
                    it.jenisOlahraga,
                    it.durasiMenit,
                    it.kaloriTerbakar,
                    it.tanggal,
                    it.foto
                )
            })
        }
    }

    suspend fun deleteSport(id: Int): Result<String> {
        return try {
            api.deleteSport(id)
            dao.deleteSport(id) // Hapus lokal juga
            Result.success("Berhasil menghapus")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSport(id: Int, req: SportRequest): Result<String> {
        return try {
            api.updateSport(id, req)

            // Update lokal
            dao.insertSport(
                SportEntity(
                    id = id,
                    userId = getUserId(),
                    jenisOlahraga = req.jenisOlahraga,
                    durasiMenit = req.durasiMenit,
                    kaloriTerbakar = req.kaloriTerbakar,
                    tanggal = req.tanggal.ifBlank {
                        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                    },
                    foto = req.foto
                )
            )
            Result.success("Berhasil update sport")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    data class WeeklyChartData(
        val labels: List<String>,
        val values: List<Double>,
        val rangeText: String
    )

    suspend fun getWeeklySportChartData(): WeeklyChartData {
        // Ambil data weekly langsung dari API (agregasi di server)
        val res = api.getWeeklySport()

        if (!res.isSuccessful || res.body() == null) {
            return WeeklyChartData(
                labels = listOf("Sen","Sel","Rab","Kam","Jum","Sab","Min"),
                values = List(7) { 0.0 },
                rangeText = ""
            )
        }

        val body = res.body()!!
        return WeeklyChartData(
            labels = body.labels,
            values = body.values,
            rangeText = body.rangeText
        )
    }

    // =========================================================
    // SLEEP
    // =========================================================

    suspend fun catatTidur(req: SleepRequest): Result<String> {
        return try {
            val response = api.catatTidur(req)
            if (response.isSuccessful && response.body()?.data != null) {
                val body = response.body()!!.data!!

                dao.insertSleep(
                    SleepEntity(
                        id = body.id,
                        userId = getUserId(),
                        jamTidur = body.jamTidur,
                        jamBangun = body.jamBangun,
                        durasiTidur = body.durasiTidur,
                        kualitasTidur = body.kualitasTidur,
                        tanggal = body.tanggal
                    )
                )
                Result.success("Berhasil mencatat tidur")
            } else {
                Result.failure(Exception("Gagal mencatat tidur"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSleepHistory(): Result<List<SleepData>> {
        val userId = getUserId()

        return try {
            val response = api.getSleepHistory()
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!

                // 🔥 OPTIMASI: Batch Insert
                val entities = list.map {
                    SleepEntity(
                        id = it.id,
                        userId = userId,
                        jamTidur = it.jamTidur,
                        jamBangun = it.jamBangun,
                        durasiTidur = it.durasiTidur,
                        kualitasTidur = it.kualitasTidur,
                        tanggal = it.tanggal ?: ""
                    )
                }
                dao.insertAllSleep(entities)

                Result.success(list)
            } else {
                val local = dao.getSleepHistory(userId)
                Result.success(local.map {
                    SleepData(it.id, it.jamTidur, it.jamBangun, it.durasiTidur, it.kualitasTidur, it.tanggal)
                })
            }
        } catch (e: Exception) {
            val local = dao.getSleepHistory(userId)
            Result.success(local.map {
                SleepData(it.id, it.jamTidur, it.jamBangun, it.durasiTidur, it.kualitasTidur, it.tanggal)
            })
        }
    }

    suspend fun deleteSleep(id: Int): Result<String> {
        return try {
            api.deleteSleep(id)
            dao.deleteSleep(id)
            Result.success("Berhasil menghapus")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSleep(id: Int, req: SleepRequest): Result<String> {
        return try {
            api.updateSleep(id, req)
            dao.insertSleep(
                SleepEntity(
                    id = id,
                    userId = getUserId(),
                    jamTidur = req.jamTidur,
                    jamBangun = req.jamBangun,
                    durasiTidur = req.durasiTidur,
                    kualitasTidur = req.kualitasTidur,
                    tanggal = req.tanggal.ifBlank {
                        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                    }
                )
            )
            Result.success("Update berhasil")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeeklySleepChartData(): WeeklyChartData {
        val res = api.getWeeklySleep() // Panggil API baru

        if (!res.isSuccessful || res.body() == null) {
            return WeeklyChartData(
                labels = listOf("Sen","Sel","Rab","Kam","Jum","Sab","Min"),
                values = List(7) { 0.0 },
                rangeText = ""
            )
        }

        val body = res.body()!!
        return WeeklyChartData(
            labels = body.labels,
            values = body.values,
            rangeText = body.rangeText
        )
    }

    // =========================================================
    // WEIGHT
    // =========================================================

    suspend fun catatWeight(req: WeightRequest): Result<Unit> {
        return try {
            val res = api.catatWeight(req)
            if (res.isSuccessful && res.body()?.data != null) {
                val body = res.body()!!.data!!
                dao.insertWeight(
                    WeightEntity(
                        id = body.id,
                        userId = getUserId(),
                        beratBadan = body.beratBadan,
                        tinggiBadan = body.tinggiBadan,
                        bmi = body.bmi,
                        kategori = body.kategori,
                        tanggal = body.tanggal
                    )
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("Gagal simpan berat badan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeightHistory(): Result<List<WeightData>> {
        val userId = getUserId()
        return try {
            val response = api.getWeightHistory()
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!

                // 🔥 OPTIMASI: Batch Insert
                val entities = list.map {
                    WeightEntity(
                        id = it.id,
                        userId = userId,
                        beratBadan = it.beratBadan,
                        tinggiBadan = it.tinggiBadan,
                        bmi = it.bmi,
                        kategori = it.kategori,
                        tanggal = it.tanggal
                    )
                }
                dao.insertAllWeight(entities)

                Result.success(list)
            } else {
                val local = dao.getWeightHistory(userId)
                Result.success(local.map {
                    WeightData(it.id, it.beratBadan, it.tinggiBadan, it.bmi, it.kategori, it.tanggal)
                })
            }
        } catch (e: Exception) {
            val local = dao.getWeightHistory(userId)
            Result.success(local.map {
                WeightData(it.id, it.beratBadan, it.tinggiBadan, it.bmi, it.kategori, it.tanggal)
            })
        }
    }

    suspend fun deleteWeight(id: Int): Result<String> {
        return try {
            api.deleteWeight(id)
            dao.deleteWeight(id)
            Result.success("Berhasil hapus berat badan")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateWeight(id: Int, req: WeightRequest): Result<String> {
        return try {
            api.updateWeight(id, req)
            dao.insertWeight(
                WeightEntity(
                    id = id,
                    userId = getUserId(),
                    beratBadan = req.beratBadan,
                    tinggiBadan = req.tinggiBadan,
                    bmi = req.bmi,
                    kategori = req.kategori,
                    tanggal = req.tanggal
                )
            )
            Result.success("Berhasil update berat badan")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Tambahkan ini di dalam class FisikRepository
    suspend fun syncFcmToken(token: String) {
        try {
            val payload = mapOf("fcm_token" to token)
            // Pastikan kamu punya endpoint ini di Retrofit API kamu
            // Kalau belum ada, tambahkan di interface API: @PUT("fisik/fcm-token")
            api.updateFcmToken(payload)
            android.util.Log.d("FCM_REPO", "✅ Token terkirim ke server")
        } catch (e: Exception) {
            android.util.Log.e("FCM_REPO", "❌ Gagal kirim token", e)
        }
    }
}