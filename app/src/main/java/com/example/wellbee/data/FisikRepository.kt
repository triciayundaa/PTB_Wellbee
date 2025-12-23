package com.example.wellbee.data

import android.content.Context
import android.util.Log
import com.example.wellbee.data.local.AppDatabase
import com.example.wellbee.data.local.SleepEntity
import com.example.wellbee.data.local.SportEntity
import com.example.wellbee.data.local.WeightEntity
import com.example.wellbee.data.model.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FisikRepository(private val context: Context) {

    private fun getUserId(): Int {
        val prefs = context.getSharedPreferences("wellbee_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("user_id", -1)
    }

    private val api = RetrofitClient.getInstance(context)
    private val dao = AppDatabase.getInstance(context).fisikDao()

    data class WeeklyChartData(
        val labels: List<String>,
        val values: List<Double>,
        val rangeText: String
    )

    suspend fun catatOlahraga(req: SportRequest): Result<String> {
        return try {
            val response = api.catatOlahraga(req)
            if (response.isSuccessful && response.body()?.data != null) {
                val body = response.body()!!.data!!

                dao.insertSport(
                    SportEntity(body.id, getUserId(), body.jenisOlahraga, body.durasiMenit, body.kaloriTerbakar, body.tanggal, body.foto)
                )
                Result.success("Berhasil mencatat olahraga")
            } else {
                Result.failure(Exception("Gagal simpan ke server"))
            }
        } catch (e: Exception) {
            try {
                val fakeId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                dao.insertSport(
                    SportEntity(fakeId, getUserId(), req.jenisOlahraga, req.durasiMenit, req.kaloriTerbakar, req.tanggal, req.foto)
                )
                Result.success("Tersimpan secara Offline")
            } catch (dbEx: Exception) {
                Result.failure(dbEx)
            }
        }
    }

    suspend fun getSportHistory(): Result<List<SportHistory>> {
        val userId = getUserId()
        try {
            val response = api.getSportHistory()
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!
                val entities = list.map {
                    SportEntity(it.id, userId, it.jenisOlahraga, it.durasiMenit, it.kaloriTerbakar, it.tanggal ?: "", it.foto)
                }
                dao.insertAllSport(entities)
                return Result.success(list)
            }
        } catch (e: Exception) {
            Log.e("REPO", "Gagal load API Sport, ambil lokal: ${e.message}")
        }

        val local = dao.getSportHistory(userId)
        return Result.success(local.map {
            SportHistory(it.id, it.userId, it.jenisOlahraga, it.durasiMenit, it.kaloriTerbakar, it.tanggal, it.foto)
        })
    }

    suspend fun getWeeklySportChartData(): WeeklyChartData {
        try {
            val res = api.getWeeklySport()
            if (res.isSuccessful && res.body() != null) {
                return WeeklyChartData(res.body()!!.labels, res.body()!!.values, res.body()!!.rangeText)
            }
        } catch (e: Exception) {
            Log.e("REPO", "Chart Sport Offline")
        }
        return calculateLocalChart(dao.getSportHistory(getUserId()), isSport = true)
    }

    suspend fun catatTidur(req: SleepRequest): Result<String> {
        return try {
            val response = api.catatTidur(req)
            if (response.isSuccessful && response.body()?.data != null) {
                val body = response.body()!!.data!!
                dao.insertSleep(
                    SleepEntity(body.id, getUserId(), body.jamTidur, body.jamBangun, body.durasiTidur, body.kualitasTidur, body.tanggal)
                )
                Result.success("Berhasil mencatat tidur")
            } else { Result.failure(Exception("Gagal")) }
        } catch (e: Exception) {
            try {
                val fakeId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                dao.insertSleep(
                    SleepEntity(fakeId, getUserId(), req.jamTidur, req.jamBangun, req.durasiTidur, req.kualitasTidur, req.tanggal)
                )
                Result.success("Tersimpan Offline")
            } catch (dbEx: Exception) { Result.failure(dbEx) }
        }
    }

    suspend fun getSleepHistory(): Result<List<SleepData>> {
        val userId = getUserId()
        try {
            val response = api.getSleepHistory()
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!
                val entities = list.map {
                    SleepEntity(it.id, userId, it.jamTidur, it.jamBangun, it.durasiTidur, it.kualitasTidur, it.tanggal ?: "")
                }
                dao.insertAllSleep(entities)
                return Result.success(list)
            }
        } catch (e: Exception) { Log.e("REPO", "Ambil Sleep Lokal") }

        val local = dao.getSleepHistory(userId)
        return Result.success(local.map {
            SleepData(it.id, it.jamTidur, it.jamBangun, it.durasiTidur, it.kualitasTidur, it.tanggal)
        })
    }

    suspend fun getWeeklySleepChartData(): WeeklyChartData {
        try {
            val res = api.getWeeklySleep()
            if (res.isSuccessful && res.body() != null) {
                return WeeklyChartData(res.body()!!.labels, res.body()!!.values, res.body()!!.rangeText)
            }
        } catch (e: Exception) { Log.e("REPO", "Chart Sleep Offline") }

        return calculateLocalChart(dao.getSleepHistory(getUserId()), isSport = false)
    }

    suspend fun catatWeight(req: WeightRequest): Result<Unit> {
        return try {
            val res = api.catatWeight(req)
            if (res.isSuccessful && res.body()?.data != null) {
                val body = res.body()!!.data!!
                dao.insertWeight(
                    WeightEntity(body.id, getUserId(), body.beratBadan, body.tinggiBadan, body.bmi, body.kategori, body.tanggal)
                )
                Result.success(Unit)
            } else { Result.failure(Exception("Gagal")) }
        } catch (e: Exception) {
            try {
                val fakeId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

                val tinggiM = req.tinggiBadan / 100.0
                val bmiCalc = req.beratBadan / (tinggiM * tinggiM)
                val katCalc = when {
                    bmiCalc < 18.5 -> "Underweight"
                    bmiCalc < 25.0 -> "Normal"
                    else -> "Overweight"
                }

                dao.insertWeight(
                    WeightEntity(fakeId, getUserId(), req.beratBadan, req.tinggiBadan, bmiCalc, katCalc, req.tanggal)
                )
                Result.success(Unit)
            } catch (dbEx: Exception) { Result.failure(dbEx) }
        }
    }

    suspend fun getWeightHistory(): Result<List<WeightData>> {
        val userId = getUserId()
        try {
            val response = api.getWeightHistory()
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!
                val entities = list.map {
                    WeightEntity(it.id, userId, it.beratBadan, it.tinggiBadan, it.bmi, it.kategori, it.tanggal)
                }
                dao.insertAllWeight(entities)
                return Result.success(list)
            }
        } catch (e: Exception) { Log.e("REPO", "Ambil Weight Lokal") }

        val local = dao.getWeightHistory(userId)
        return Result.success(local.map {
            WeightData(it.id, it.beratBadan, it.tinggiBadan, it.bmi, it.kategori, it.tanggal)
        })
    }

    suspend fun deleteSport(id: Int): Result<String> {
        return try {
            api.deleteSport(id)
            dao.deleteSport(id)
            Result.success("Berhasil menghapus")
        } catch (e: Exception) {
            dao.deleteSport(id)
            Result.success("Terhapus dari penyimpanan lokal")
        }
    }

    suspend fun updateSport(id: Int, req: SportRequest): Result<String> {
        return try {
            api.updateSport(id, req)
            dao.insertSport(
                SportEntity(id, getUserId(), req.jenisOlahraga, req.durasiMenit, req.kaloriTerbakar, req.tanggal, req.foto)
            )
            Result.success("Berhasil update")
        } catch (e: Exception) {
            dao.insertSport(
                SportEntity(id, getUserId(), req.jenisOlahraga, req.durasiMenit, req.kaloriTerbakar, req.tanggal, req.foto)
            )
            Result.success("Update tersimpan lokal")
        }
    }

    suspend fun deleteSleep(id: Int): Result<String> {
        return try {
            api.deleteSleep(id)
            dao.deleteSleep(id)
            Result.success("Berhasil menghapus")
        } catch (e: Exception) {
            dao.deleteSleep(id)
            Result.success("Terhapus lokal")
        }
    }

    suspend fun updateSleep(id: Int, req: SleepRequest): Result<String> {
        return try {
            val response = api.updateSleep(id, req)
            if (response.isSuccessful) {
                dao.insertSleep(
                    SleepEntity(id, getUserId(), req.jamTidur, req.jamBangun, req.durasiTidur, req.kualitasTidur, req.tanggal)
                )
                Result.success("Update berhasil")
            } else {
                Result.failure(Exception("Server menolak update"))
            }
        } catch (e: Exception) {
            dao.insertSleep(
                SleepEntity(id, getUserId(), req.jamTidur, req.jamBangun, req.durasiTidur, req.kualitasTidur, req.tanggal)
            )
            Result.success("Disimpan Offline")
        }
    }

    suspend fun deleteWeight(id: Int): Result<String> {
        return try {
            api.deleteWeight(id)
            dao.deleteWeight(id)
            Result.success("Berhasil hapus")
        } catch (e: Exception) {
            dao.deleteWeight(id)
            Result.success("Hapus lokal berhasil")
        }
    }

    suspend fun updateWeight(id: Int, req: WeightRequest): Result<String> {
        return try {
            val response = api.updateWeight(id, req)
            if (response.isSuccessful) {
                val tinggiM = req.tinggiBadan / 100.0
                val bmiCalc = req.beratBadan / (tinggiM * tinggiM)
                val katCalc = when {
                    bmiCalc < 18.5 -> "Underweight"
                    bmiCalc < 25.0 -> "Normal"
                    else -> "Overweight"
                }
                dao.insertWeight(
                    WeightEntity(id, getUserId(), req.beratBadan, req.tinggiBadan, bmiCalc, katCalc, req.tanggal)
                )
                Result.success("Berhasil update")
            } else {
                Result.failure(Exception("Server menolak update"))
            }
        } catch (e: Exception) {
            val tinggiM = req.tinggiBadan / 100.0
            val bmiCalc = req.beratBadan / (tinggiM * tinggiM)
            val katCalc = when {
                bmiCalc < 18.5 -> "Underweight"
                bmiCalc < 25.0 -> "Normal"
                else -> "Overweight"
            }
            dao.insertWeight(
                WeightEntity(id, getUserId(), req.beratBadan, req.tinggiBadan, bmiCalc, katCalc, req.tanggal)
            )
            Result.success("Disimpan Offline")
        }
    }

    private fun calculateLocalChart(list: List<Any>, isSport: Boolean): WeeklyChartData {
        val chartSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val labelSdf = SimpleDateFormat("EEE", Locale("id", "ID"))

        val labels = mutableListOf<String>()
        val values = mutableListOf<Double>()

        val calendar = Calendar.getInstance(Locale("id", "ID"))

        calendar.firstDayOfWeek = Calendar.MONDAY

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        var startRange = ""
        var endRange = ""

        for (i in 0 until 7) {
            val dateForChart = chartSdf.format(calendar.time)

            if (i==0) startRange = dateForChart
            if (i==6) endRange = dateForChart

            labels.add(labelSdf.format(calendar.time))

            var total = 0.0

            if (isSport) {
                @Suppress("UNCHECKED_CAST")
                val sportList = list as List<SportEntity>
                total = sportList.filter { item ->
                    normalizeDate(item.tanggal) == dateForChart
                }.sumOf { it.durasiMenit }.toDouble()
            } else {
                @Suppress("UNCHECKED_CAST")
                val sleepList = list as List<SleepEntity>
                total = sleepList.filter { item ->
                    normalizeDate(item.tanggal) == dateForChart
                }.sumOf {
                    try { it.durasiTidur.toString().toDouble() } catch(e:Exception){ 0.0 }
                }
            }

            values.add(total)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return WeeklyChartData(labels, values, "$startRange - $endRange (Offline)")
    }

    private fun normalizeDate(rawDate: String?): String {
        if (rawDate.isNullOrBlank()) return ""

        val patterns = listOf(
            "yyyy-MM-dd",
            "dd-MM-yyyy",
            "dd MMM yyyy",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss"
        )

        for (pattern in patterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US)
                val date = sdf.parse(rawDate)

                if (date != null) {
                    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
                }
            } catch (e: Exception) {

            }
        }
        return rawDate ?: ""
    }

    suspend fun syncFcmToken(token: String) {
        try {
            val payload = mapOf("fcm_token" to token)
            api.updateFcmToken(payload)
            Log.d("FCM_REPO", "Token terkirim ke server")
        } catch (e: Exception) {
            Log.e("FCM_REPO", "Gagal kirim token (Mungkin offline)", e)
        }
    }
}