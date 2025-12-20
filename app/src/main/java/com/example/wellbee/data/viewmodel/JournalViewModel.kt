package com.example.wellbee.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellbee.data.local.MentalJournalEntity
import com.example.wellbee.data.repository.MentalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class JournalViewModel(
    private val repo: MentalRepository,
    private val userId: Int
) : ViewModel() {

    val journals: StateFlow<List<MentalJournalEntity>> =
        repo.observeJournalsByUser(userId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addJournal(
        triggerLabel: String?,
        isiJurnal: String,
        fotoPath: String?,
        audioPath: String?
    ) {
        val today = LocalDate.now().toString()

        viewModelScope.launch {
            val localId = repo.insertJournalOffline(
                MentalJournalEntity(
                    userId = userId,
                    triggerLabel = triggerLabel,
                    isiJurnal = isiJurnal,
                    fotoPath = fotoPath,
                    audioPath = audioPath,
                    tanggal = today
                )
            )

            val inserted = journals.value.firstOrNull { it.id.toLong() == localId }
            if (inserted != null) repo.syncJournal(inserted)
        }
    }

    fun syncPending() {
        viewModelScope.launch {
            repo.syncPending()
        }
    }
}
