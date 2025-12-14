package com.example.wellbee.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FisikDao {

    // SPORT
    @Query("SELECT * FROM sport_history ORDER BY localId DESC")
    suspend fun getSportHistory(): List<SportEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSport(sport: SportEntity)

    @Query("DELETE FROM sport_history WHERE id = :id")
    suspend fun deleteSport(id: Int)


    // SLEEP
    @Query("SELECT * FROM sleep_history ORDER BY localId DESC")
    suspend fun getSleepHistory(): List<SleepEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(sleep: SleepEntity)

    @Query("DELETE FROM sleep_history WHERE id = :id")
    suspend fun deleteSleep(id: Int)

    //weight
    @Query("SELECT * FROM weight_history ORDER BY tanggal DESC")
    suspend fun getWeightHistory(): List<WeightEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(weight: WeightEntity)

    @Query("DELETE FROM weight_history WHERE id = :id")
    suspend fun deleteWeight(id: Int)

}
