package com.asistent.data.repository

import com.asistent.data.local.dao.MedicationDao
import com.asistent.data.local.entity.Medication
import com.asistent.data.local.entity.MedicationLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicationRepository @Inject constructor(private val dao: MedicationDao) {
    fun observeActive(): Flow<List<Medication>> = dao.observeActive()
    suspend fun getActive(): List<Medication> = dao.getActive()
    suspend fun add(name: String, dose: String, hour: Int, minute: Int): Long =
        dao.insert(Medication(name = name, dose = dose, hourOfDay = hour, minuteOfHour = minute))
    suspend fun deactivate(id: Long) = dao.deactivate(id)
    suspend fun logTaken(medId: Long, dateKey: String) =
        dao.insertLog(MedicationLog(medicationId = medId, dateKey = dateKey))
    fun observeLogsForDay(dateKey: String): Flow<List<MedicationLog>> =
        dao.observeLogsForDay(dateKey)
}
