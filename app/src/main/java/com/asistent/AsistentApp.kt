package com.asistent

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AsistentApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_MEDS, "Medicamente", NotificationManager.IMPORTANCE_HIGH)
                .apply { description = "Reminder-uri pentru pastile"; enableVibration(true) }
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_SUBS, "Abonamente", NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = "Abonamente neutilizate" }
        )
    }
    companion object {
        const val CHANNEL_MEDS = "ch_medications"
        const val CHANNEL_SUBS = "ch_subscriptions"
    }
}
