package com.example.wellbee.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FisikDao {

    @Query("""
        SELECT * FROM sport_history
        WHERE userId = :userId
        ORDER BY tanggal DESC
    """)
    suspend fun getSportHistory(userId: Int): List<SportEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSport(sport: SportEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSport(sports: List<SportEntity>)

    @Query("DELETE FROM sport_history WHERE id = :id")
    suspend fun deleteSport(id: Int)

    @Query("DELETE FROM sport_history WHERE userId = :userId")
    suspend fun clearSportHistory(userId: Int)

    @Query("""
        SELECT substr(tanggal, 1, 10) AS date,
               SUM(durasiMenit) AS total
        FROM sport_history
        WHERE userId = :userId
          AND substr(tanggal,1,10) BETWEEN :startDate AND :endDate
        GROUP BY substr(tanggal, 1, 10)
        ORDER BY substr(tanggal, 1, 10) ASC
    """)
    suspend fun getWeeklySportSummary(
        userId: Int,
        startDate: String,
        endDate: String
    ): List<DailySportSum>

    @Query("""
        SELECT * FROM sleep_history
        WHERE userId = :userId
        ORDER BY tanggal DESC
    """)
    suspend fun getSleepHistory(userId: Int): List<SleepEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(sleep: SleepEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSleep(sleeps: List<SleepEntity>)

    @Query("DELETE FROM sleep_history WHERE id = :id")
    suspend fun deleteSleep(id: Int)

    @Query("""
       SELECT * FROM weight_history
       WHERE userId = :userId
       ORDER BY tanggal DESC
    """)
    suspend fun getWeightHistory(userId: Int): List<WeightEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(weight: WeightEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWeight(weights: List<WeightEntity>)

    @Query("DELETE FROM weight_history WHERE id = :id")
    suspend fun deleteWeight(id: Int)
}