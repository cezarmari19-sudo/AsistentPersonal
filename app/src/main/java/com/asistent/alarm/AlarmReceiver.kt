package com.asistent.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.asistent.AsistentApp
import com.asistent.MainActivity
import com.asistent.alarm.AlarmScheduler.Companion.*
import com.asistent.data.local.entity.daysSinceUsed
import com.asistent.data.repository.MedicationRepository
import com.asistent.data.repository.SubscriptionRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var scheduler: AlarmScheduler
    @Inject lateinit var medRepo: MedicationRepository
    @Inject lateinit var subRepo: SubscriptionRepository

    override fun onReceive(context: Context, intent: Intent) {
        val medId   = intent.getLongExtra(EXTRA_MED_ID, -1L)
        val medName = intent.getStringExtra(EXTRA_MED_NAME) ?: ""
        val medDose = intent.getStringExtra(EXTRA_MED_DOSE) ?: ""

        when (intent.action) {
            ACTION_MEDICATION -> {
                showMedNotif(context, medId, medName, medDose)
                goAsync().let { r ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try { medRepo.getActive().find { it.id == medId }?.let { scheduler.scheduleMedication(it) } }
                        finally { r.finish() }
                    }
                }
            }
            ACTION_MED_TAKEN -> {
                cancelNotif(context, medId)
                goAsync().let { r ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try { medRepo.logTaken(medId, LocalDate.now().toString()) }
                        finally { r.finish() }
                    }
                }
            }
            ACTION_MED_SNOOZE -> { cancelNotif(context, medId); scheduler.snoozeMedication(medId, medName, medDose) }
            ACTION_SUBS_CHECK -> goAsync().let { r ->
                CoroutineScope(Dispatchers.IO).launch {
                    try { subRepo.getAll().filter { it.daysSinceUsed() >= 30 }
                        .forEach { showSubNotif(context, it.id, it.name, it.daysSinceUsed()) } }
                    finally { r.finish() }
                }
            }
            Intent.ACTION_BOOT_COMPLETED -> goAsync().let { r ->
                CoroutineScope(Dispatchers.IO).launch {
                    try { scheduler.rescheduleAll(medRepo.getActive()) }
                    finally { r.finish() }
                }
            }
        }
    }

    private fun showMedNotif(context: Context, medId: Long, name: String, dose: String) {
        val title = "💊 $name${if (dose.isNotBlank()) " — $dose" else ""}"
        val takenPi = PendingIntent.getBroadcast(context, (medId + 1).toInt(),
            Intent(context, AlarmReceiver::class.java).apply { action = ACTION_MED_TAKEN; putExtra(EXTRA_MED_ID, medId) },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val snoozePi = PendingIntent.getBroadcast(context, (medId + 2).toInt(),
            Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_MED_SNOOZE
                putExtra(EXTRA_MED_ID, medId); putExtra(EXTRA_MED_NAME, name); putExtra(EXTRA_MED_DOSE, dose)
            }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        context.getSystemService(NotificationManager::class.java).notify(medId.toInt(),
            NotificationCompat.Builder(context, AsistentApp.CHANNEL_MEDS)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle(title).setContentText("Este ora să îți iei medicamentul")
                .setPriority(NotificationCompat.PRIORITY_MAX).setCategory(NotificationCompat.CATEGORY_ALARM)
                .setOngoing(true).setAutoCancel(false)
                .setContentIntent(PendingIntent.getActivity(context, medId.toInt(),
                    Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE))
                .addAction(0, "✓ Am luat", takenPi).addAction(0, "⏰ 30 minute", snoozePi)
                .build())
    }

    private fun showSubNotif(context: Context, subId: Long, name: String, days: Int) {
        context.getSystemService(NotificationManager::class.java).notify((subId + 50_000).toInt(),
            NotificationCompat.Builder(context, AsistentApp.CHANNEL_SUBS)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Abonament neutilizat: $name")
                .setContentText("Nu l-ai folosit de $days zile")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true).build())
    }

    private fun cancelNotif(context: Context, id: Long) =
        context.getSystemService(NotificationManager::class.java).cancel(id.toInt())
}
