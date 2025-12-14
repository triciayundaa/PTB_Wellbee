package com.example.wellbee.data

import android.content.Context
import com.example.wellbee.data.local.AppDatabase
import com.example.wellbee.data.local.SleepEntity
import com.example.wellbee.data.local.SportEntity
import com.example.wellbee.data.local.WeightEntity
import com.example.wellbee.data.model.SleepData
import com.example.wellbee.data.model.SleepRequest
import com.example.wellbee.data.model.SportHistory
import com.example.wellbee.data.model.SportRequest
import com.example.wellbee.data.model.WeightData
import com.example.wellbee.data.model.WeightRequest
import retrofit2.Response

class FisikRepository(private val context: Context) {

    private val api = RetrofitClient.getInstance(context)
    private val dao = AppDatabase.getDatabase(context).fisikDao()

    // ---------------------------------------------------------
    // POST SPORT
    // ---------------------------------------------------------
    suspend fun catatOlahraga(req: SportRequest): Result<String> {
        return try {
            val response = api.catatOlahraga(req)

            if (response.isSuccessful && response.body()?.data != null) {

                val body = response.body()!!.data!!

                // Simpan ke Room
                dao.insertSport(
                    SportEntity(
                        id = body.id,
                        userId = body.userId,
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

    // ---------------------------------------------------------
    // GET SPORT HISTORY (ONLINE + OFFLINE)
    // ---------------------------------------------------------
    suspend fun getSportHistory(userId: Int): Result<List<SportHistory>> {
        return try {
            val response = api.getSportHistory()

            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!

                // Sinkronisasi ke Room
                list.forEach {
                    dao.insertSport(
                        SportEntity(
                            id = it.id,
                            userId = it.userId,
                            jenisOlahraga = it.jenisOlahraga,
                            durasiMenit = it.durasiMenit,
                            kaloriTerbakar = it.kaloriTerbakar,
                            tanggal = it.tanggal,
                            foto = it.foto
                        )
                    )
                }

                Result.success(list.sortedByDescending { it.id })

            } else {

                val local = dao.getSportHistory()

                Result.success(
                    local.map {
                        SportHistory(
                            id = it.id,
                            userId = it.userId,
                            jenisOlahraga = it.jenisOlahraga,
                            durasiMenit = it.durasiMenit,
                            kaloriTerbakar = it.kaloriTerbakar,
                            tanggal = it.tanggal,
                            foto = it.foto
                        )
                    }.sortedByDescending { it.id }
                )
            }

        } catch (e: Exception) {

            val local = dao.getSportHistory()

            Result.success(
                local.map {
                    SportHistory(
                        id = it.id,
                        userId = it.userId,
                        jenisOlahraga = it.jenisOlahraga,
                        durasiMenit = it.durasiMenit,
                        kaloriTerbakar = it.kaloriTerbakar,
                        tanggal = it.tanggal,
                        foto = it.foto
                    )
                }.sortedByDescending { it.id }
            )
        }
    }

    // ---------------------------------------------------------
    // DELETE SPORT (SERVER + ROOM)
    // ---------------------------------------------------------
    suspend fun deleteSport(id: Int): Result<String> {
        return try {
            val response = api.deleteSport(id)

            if (response.isSuccessful) {
                dao.deleteSport(id)   // HAPUS LOCAL JUGA
                Result.success("Berhasil menghapus")
            } else {
                Result.failure(Exception("Gagal hapus data"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------------------------------------------------
    // UPDATE SPORT (SERVER + ROOM)
    // ---------------------------------------------------------
    suspend fun updateSport(id: Int, req: SportRequest): Result<String> {
        return try {
            val response = api.updateSport(id, req)

            if (response.isSuccessful) {

                // Update Room manual
                dao.insertSport(
                    SportEntity(
                        id = id,
                        userId = -1, // server tidak kirim user? bisa isi dari SharedPref
                        jenisOlahraga = req.jenisOlahraga,
                        durasiMenit = req.durasiMenit,
                        kaloriTerbakar = req.kaloriTerbakar,
                        tanggal = req.tanggal,
                        foto = req.foto
                    )
                )

                Result.success("Berhasil update sport")
            } else {
                Result.failure(Exception("Gagal update sport"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // ---------------------------------------------------------
    // SLEEP FUNCTIONS
    // ---------------------------------------------------------

    suspend fun catatTidur(req: SleepRequest): Result<String> {
        return try {
            val response = api.catatTidur(req)

            if (response.isSuccessful && response.body()?.data != null) {

                val body = response.body()!!.data!!

                dao.insertSleep(
                    SleepEntity(
                        id = body.id,
                        userId = -1,
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
        return try {
            val response = api.getSleepHistory()

            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!

                // update Room
                list.forEach {
                    dao.insertSleep(
                        SleepEntity(
                            id = it.id,
                            userId = -1,
                            jamTidur = it.jamTidur,
                            jamBangun = it.jamBangun,
                            durasiTidur = it.durasiTidur,
                            kualitasTidur = it.kualitasTidur,
                            tanggal = it.tanggal
                        )
                    )
                }

                Result.success(list)

            } else {
                val local = dao.getSleepHistory()
                Result.success(local.map {
                    SleepData(
                        id = it.id,
                        jamTidur = it.jamTidur,
                        jamBangun = it.jamBangun,
                        durasiTidur = it.durasiTidur,
                        kualitasTidur = it.kualitasTidur,
                        tanggal = it.tanggal
                    )
                })
            }

        } catch (e: Exception) {
            val local = dao.getSleepHistory()
            Result.success(local.map {
                SleepData(
                    id = it.id,
                    jamTidur = it.jamTidur,
                    jamBangun = it.jamBangun,
                    durasiTidur = it.durasiTidur,
                    kualitasTidur = it.kualitasTidur,
                    tanggal = it.tanggal
                )
            })
        }
    }

    suspend fun deleteSleep(id: Int): Result<String> {
        return try {
            val response = api.deleteSleep(id)

            if (response.isSuccessful) {
                dao.deleteSleep(id)
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

                dao.insertSleep(
                    SleepEntity(
                        id = id,
                        userId = -1,
                        jamTidur = req.jamTidur,
                        jamBangun = req.jamBangun,
                        durasiTidur = req.durasiTidur,
                        kualitasTidur = req.kualitasTidur,
                        tanggal = req.tanggal
                    )
                )

                Result.success("Update berhasil")
            } else {
                Result.failure(Exception("Gagal update tidur"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //weight
    suspend fun catatWeight(req: WeightRequest): Result<Unit> {
        return try {
            val res = api.catatWeight(req)

            if (res.isSuccessful && res.body()?.data != null) {
                val body = res.body()!!.data

                dao.insertWeight(
                    WeightEntity(
                        id = body.id,
                        beratBadan = body.beratBadan,
                        tinggiBadan = body.tinggiBadan,
                        bmi = body.bmi,
                        kategori = body.kategori,
                        tanggal = body.tanggal
                    )
                )

                Result.success(Unit)
            } else {
                Result.failure(Exception("Response gagal: ${res.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)   // ⬅⬅⬅ INI YANG HILANG
        }
    }



    suspend fun getWeightHistory(): Result<List<WeightData>> {
        val local = dao.getWeightHistory()
        return Result.success(local.map {
            WeightData(
                it.id, it.beratBadan, it.tinggiBadan,
                it.bmi, it.kategori, it.tanggal
            )
        })
    }

    suspend fun deleteWeight(id: Int): Result<String> {
        return try {
            val response = api.deleteWeight(id)
            if (response.isSuccessful) {
                dao.deleteWeight(id)
                Result.success("Berhasil hapus berat badan")
            } else {
                Result.failure(Exception("Gagal hapus data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateWeight(id: Int, req: WeightRequest): Result<String> {
        return try {
            val response = api.updateWeight(id, req)
            if (response.isSuccessful) {

                dao.insertWeight(
                    WeightEntity(
                        id = id,
                        beratBadan = req.beratBadan,
                        tinggiBadan = req.tinggiBadan,
                        bmi = req.bmi,
                        kategori = req.kategori,
                        tanggal = req.tanggal
                    )
                )

                Result.success("Berhasil update berat badan")
            } else {
                Result.failure(Exception("Gagal update"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}
