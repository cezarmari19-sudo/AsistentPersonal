package com.asistent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val cost: Float = 0f,
    val currency: String = "RON",
    val lastUsedAt: Long = System.currentTimeMillis(),
    val addedAt: Long = System.currentTimeMillis(),
)

fun Subscription.daysSinceUsed(): Int =
    ((System.currentTimeMillis() - lastUsedAt) / 86_400_000L).toInt()
