package com.asistent.ui.subs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asistent.alarm.AlarmScheduler
import com.asistent.data.local.entity.Subscription
import com.asistent.data.local.entity.daysSinceUsed
import com.asistent.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubUiItem(val sub: Subscription, val days: Int, val isStale: Boolean)

@HiltViewModel
class SubsViewModel @Inject constructor(
    private val repo: SubscriptionRepository,
    private val scheduler: AlarmScheduler,
) : ViewModel() {
    val items = repo.observeAll().map { list ->
        list.map { sub -> SubUiItem(sub, sub.daysSinceUsed(), sub.daysSinceUsed() >= 30) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(name: String, cost: Float, currency: String) = viewModelScope.launch {
        repo.add(name, cost, currency); scheduler.scheduleSubsCheck()
    }
    fun markUsed(id: Long) = viewModelScope.launch { repo.markUsed(id) }
    fun delete(id: Long) = viewModelScope.launch { repo.delete(id) }
}
