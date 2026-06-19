package com.asistent.data.repository

import com.asistent.data.local.dao.SubscriptionDao
import com.asistent.data.local.entity.Subscription
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(private val dao: SubscriptionDao) {
    fun observeAll(): Flow<List<Subscription>> = dao.observeAll()
    suspend fun getAll(): List<Subscription> = dao.getAll()
    suspend fun add(name: String, cost: Float, currency: String): Long =
        dao.insert(Subscription(name = name, cost = cost, currency = currency))
    suspend fun markUsed(id: Long) = dao.markUsed(id)
    suspend fun delete(id: Long) = dao.delete(id)
}
