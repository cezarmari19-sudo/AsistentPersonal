package com.asistent.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.asistent.data.local.entity.Medication
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(@ApplicationContext private val context: Context) {
    private val am = context.getSystemService(AlarmManager::class.java)

    fun scheduleMedication(med: Medication) {
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTrigger(med.hourOfDay, med.minuteOfHour),
            medPi(med.id, med.name, med.dose))
    }

    fun snoozeMedication(medId: Long, name: String, dose: String) {
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 30 * 60 * 1000L,
            PendingIntent.getBroadcast(context, (medId + SNOOZE_OFFSET).toInt(),
                buildMedIntent(medId, name, dose),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
    }

    fun cancelMedication(medId: Long) {
        PendingIntent.getBroadcast(context, medId.toInt(),
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
            ?.also { am.cancel(it); it.cancel() }
    }

    fun scheduleSubsCheck() {
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, nextTrigger(10, 0),
            AlarmManager.INTERVAL_DAY,
            PendingIntent.getBroadcast(context, REQUEST_SUBS,
                Intent(context, AlarmReceiver::class.java).apply { action = ACTION_SUBS_CHECK },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
    }

    fun rescheduleAll(medications: List<Medication>) {
        medications.forEach { scheduleMedication(it) }
        scheduleSubsCheck()
    }

    private fun medPi(id: Long, name: String, dose: String) =
        PendingIntent.getBroadcast(context, id.toInt(), buildMedIntent(id, name, dose),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    private fun buildMedIntent(id: Long, name: String, dose: String) =
        Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_MEDICATION
            putExtra(EXTRA_MED_ID, id); putExtra(EXTRA_MED_NAME, name); putExtra(EXTRA_MED_DOSE, dose)
        }

    private fun nextTrigger(hour: Int, minute: Int): Long =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

    companion object {
        const val ACTION_MEDICATION = "com.asistent.MEDICATION_ALARM"
        const val ACTION_MED_TAKEN  = "com.asistent.MED_TAKEN"
        const val ACTION_MED_SNOOZE = "com.asistent.MED_SNOOZE"
        const val ACTION_SUBS_CHECK = "com.asistent.SUBS_CHECK"
        const val EXTRA_MED_ID      = "med_id"
        const val EXTRA_MED_NAME    = "med_name"
        const val EXTRA_MED_DOSE    = "med_dose"
        private const val REQUEST_SUBS  = 9_000
        private const val SNOOZE_OFFSET = 100_000
    }
}
