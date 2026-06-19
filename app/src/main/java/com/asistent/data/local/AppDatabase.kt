package com.asistent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asistent.data.local.dao.MedicationDao
import com.asistent.data.local.dao.SubscriptionDao
import com.asistent.data.local.entity.Medication
import com.asistent.data.local.entity.MedicationLog
import com.asistent.data.local.entity.Subscription

@Database(
    entities = [Subscription::class, Medication::class, MedicationLog::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun medicationDao(): MedicationDao
}
