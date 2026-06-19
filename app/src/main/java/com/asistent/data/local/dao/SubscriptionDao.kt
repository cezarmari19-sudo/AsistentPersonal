package com.asistent.data.local.dao

import androidx.room.*
import com.asistent.data.local.entity.Subscription
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY lastUsedAt ASC")
    fun observeAll(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions")
    suspend fun getAll(): List<Subscription>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sub: Subscription): Long

    @Query("UPDATE subscriptions SET lastUsedAt = :ts WHERE id = :id")
    suspend fun markUsed(id: Long, ts: Long = System.currentTimeMillis())

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun delete(id: Long)
}
