package com.asistent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dose: String = "",
    val hourOfDay: Int,
    val minuteOfHour: Int,
    val isActive: Boolean = true,
)
