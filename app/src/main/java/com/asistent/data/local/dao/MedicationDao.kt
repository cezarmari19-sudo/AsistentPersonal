package com.asistent.data.local.dao

import androidx.room.*
import com.asistent.data.local.entity.Medication
import com.asistent.data.local.entity.MedicationLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications WHERE isActive = 1 ORDER BY hourOfDay, minuteOfHour")
    fun observeActive(): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE isActive = 1")
    suspend fun getActive(): List<Medication>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(med: Medication): Long

    @Query("UPDATE medications SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)

    @Insert
    suspend fun insertLog(log: MedicationLog)

    @Query("SELECT * FROM medication_logs WHERE dateKey = :dateKey")
    fun observeLogsForDay(dateKey: String): Flow<List<MedicationLog>>
}
