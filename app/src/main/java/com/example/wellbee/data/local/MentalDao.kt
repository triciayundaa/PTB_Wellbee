package com.example.wellbee.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MentalDao {

    // =========================
    // MOOD
    // =========================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(item: MentalMoodEntity): Long

    @Update
    suspend fun updateMood(item: MentalMoodEntity)

    @Query("DELETE FROM mental_mood WHERE id = :id")
    suspend fun deleteMood(id: Int)

    @Query("SELECT * FROM mental_mood WHERE userId = :userId ORDER BY tanggal DESC, id DESC")
    fun observeMoodByUser(userId: Int): Flow<List<MentalMoodEntity>>

    @Query("SELECT * FROM mental_mood WHERE userId = :userId ORDER BY tanggal DESC, id DESC")
    suspend fun getMoodByUser(userId: Int): List<MentalMoodEntity>

    // [BARU] Ambil mood berdasarkan tanggal untuk ditampilkan di Detail Journal
    @Query("SELECT * FROM mental_mood WHERE userId = :userId AND tanggal = :date ORDER BY id DESC LIMIT 1")
    suspend fun getMoodByDate(userId: Int, date: String): MentalMoodEntity?

    @Query("SELECT * FROM mental_mood WHERE isSynced = 0 ORDER BY id ASC")
    suspend fun getUnsyncedMood(): List<MentalMoodEntity>

    @Query("UPDATE mental_mood SET isSynced = 1, serverId = :serverId WHERE id = :localId")
    suspend fun markMoodSynced(localId: Int, serverId: Int)

    // =========================
    // JURNAL
    // =========================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(item: MentalJournalEntity): Long

    @Update
    suspend fun updateJournal(item: MentalJournalEntity)

    @Query("DELETE FROM mental_jurnal WHERE id = :id")
    suspend fun deleteJournal(id: Int)

    @Query("SELECT * FROM mental_jurnal WHERE userId = :userId ORDER BY tanggal DESC, id DESC")
    fun observeJournalsByUser(userId: Int): Flow<List<MentalJournalEntity>>

    @Query("SELECT * FROM mental_jurnal WHERE userId = :userId ORDER BY tanggal DESC, id DESC")
    suspend fun getJournalsByUser(userId: Int): List<MentalJournalEntity>

    @Query("SELECT * FROM mental_jurnal WHERE id = :id LIMIT 1")
    suspend fun getJournalDetail(id: Int): MentalJournalEntity?

    @Query("SELECT * FROM mental_jurnal WHERE isSynced = 0 ORDER BY id ASC")
    suspend fun getUnsyncedJournal(): List<MentalJournalEntity>

    @Query("UPDATE mental_jurnal SET isSynced = 1, serverId = :serverId WHERE id = :localId")
    suspend fun markJournalSynced(localId: Int, serverId: Int)
}
