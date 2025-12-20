package com.example.wellbee.data.repository

import com.example.wellbee.data.ApiService
import com.example.wellbee.data.JournalRequest
import com.example.wellbee.data.MoodRequest
import com.example.wellbee.data.local.MentalDao
import com.example.wellbee.data.local.MentalJournalEntity
import com.example.wellbee.data.local.MentalMoodEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MentalRepository(
    private val dao: MentalDao,
    private val api: ApiService
) {

    // =========================
    // MOOD (OFFLINE FIRST)
    // =========================
    suspend fun insertMoodOffline(item: MentalMoodEntity): Long =
        withContext(Dispatchers.IO) {
            dao.insertMood(item.copy(isSynced = false))
        }

    fun observeMoodByUser(userId: Int): Flow<List<MentalMoodEntity>> =
        dao.observeMoodByUser(userId)

    suspend fun syncMood(entity: MentalMoodEntity) =
        withContext(Dispatchers.IO) {
            if (entity.isSynced) return@withContext

            val resp = api.postMood(
                MoodRequest(
                    userId = entity.userId,
                    emoji = entity.emoji,
                    moodLabel = entity.moodLabel,
                    moodScale = entity.moodScale,
                    tanggal = entity.tanggal
                )
            )

            if (resp.isSuccessful) {
                val serverId = resp.body()?.data?.id
                if (serverId != null) dao.markMoodSynced(entity.id, serverId)
            }
        }

    // =========================
    // JURNAL (OFFLINE FIRST)
    // =========================
    suspend fun insertJournalOffline(item: MentalJournalEntity): Long =
        withContext(Dispatchers.IO) {
            dao.insertJournal(item.copy(isSynced = false))
        }

    fun observeJournalsByUser(userId: Int): Flow<List<MentalJournalEntity>> =
        dao.observeJournalsByUser(userId)

    suspend fun getJournalDetail(id: Int): MentalJournalEntity? =
        withContext(Dispatchers.IO) {
            dao.getJournalDetail(id)
        }

    suspend fun syncJournal(entity: MentalJournalEntity) =
        withContext(Dispatchers.IO) {
            if (entity.isSynced) return@withContext

            val resp = api.postJournal(
                JournalRequest(
                    userId = entity.userId,
                    triggerLabel = entity.triggerLabel,
                    isiJurnal = entity.isiJurnal,
                    foto = entity.fotoPath,
                    audio = entity.audioPath,
                    tanggal = entity.tanggal
                )
            )

            if (resp.isSuccessful) {
                val serverId = resp.body()?.data?.id
                if (serverId != null) dao.markJournalSynced(entity.id, serverId)
            }
        }

    // =========================
    // BULK SYNC (BACKGROUND)
    // =========================
    suspend fun syncPending() = withContext(Dispatchers.IO) {

        dao.getUnsyncedMood().forEach { mood ->
            try {
                val resp = api.postMood(
                    MoodRequest(
                        userId = mood.userId,
                        emoji = mood.emoji,
                        moodLabel = mood.moodLabel,
                        moodScale = mood.moodScale,
                        tanggal = mood.tanggal
                    )
                )
                if (resp.isSuccessful) {
                    resp.body()?.data?.id?.let {
                        dao.markMoodSynced(mood.id, it)
                    }
                }
            } catch (_: Exception) {}
        }

        dao.getUnsyncedJournal().forEach { journal ->
            try {
                val resp = api.postJournal(
                    JournalRequest(
                        userId = journal.userId,
                        triggerLabel = journal.triggerLabel,
                        isiJurnal = journal.isiJurnal,
                        foto = journal.fotoPath,
                        audio = journal.audioPath,
                        tanggal = journal.tanggal
                    )
                )
                if (resp.isSuccessful) {
                    resp.body()?.data?.id?.let {
                        dao.markJournalSynced(journal.id, it)
                    }
                }
            } catch (_: Exception) {}
        }
    }
}
