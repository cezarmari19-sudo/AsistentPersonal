package com.asistent.ui.meds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asistent.alarm.AlarmScheduler
import com.asistent.data.local.entity.Medication
import com.asistent.data.repository.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class MedUiItem(val med: Medication, val isTakenToday: Boolean, val takenAtDisplay: String?, val timeDisplay: String)

@HiltViewModel
class MedsViewModel @Inject constructor(
    private val repo: MedicationRepository,
    private val scheduler: AlarmScheduler,
) : ViewModel() {
    private val today = LocalDate.now().toString()
    private val fmt = DateTimeFormatter.ofPattern("HH:mm")

    val items = combine(repo.observeActive(), repo.observeLogsForDay(today)) { meds, logs ->
        meds.map { med ->
            val log = logs.find { it.medicationId == med.id }
            MedUiItem(med, log != null,
                log?.let { LocalTime.ofSecondOfDay((it.takenAt / 1000) % 86400).format(fmt) },
                LocalTime.of(med.hourOfDay, med.minuteOfHour).format(fmt))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(name: String, dose: String, hour: Int, minute: Int) = viewModelScope.launch {
        val id = repo.add(name, dose, hour, minute)
        scheduler.scheduleMedication(Medication(id=id, name=name, dose=dose, hourOfDay=hour, minuteOfHour=minute))
    }
    fun markTaken(medId: Long) = viewModelScope.launch { repo.logTaken(medId, today) }
    fun delete(medId: Long) = viewModelScope.launch { repo.deactivate(medId); scheduler.cancelMedication(medId) }
}
