package com.ara.aranote.util.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast

fun hManageAlarm(
    context: Context,
    doesCreate: Boolean,
    noteId: Int,
    triggerAtMillis: Long = 0,
): Boolean {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId,
            Intent(context, AlarmReceiver::class.java).apply {
                putExtra(
                    "requestCode",
                    noteId,
                )
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_ONE_SHOT
            },
        )

        if (doesCreate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent,
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent,
                )
            }
//            Timber.tag(TAG).i("Alarm set successfully -- code=$noteId")
        } else {
            alarmManager.cancel(pendingIntent)
//            Timber.tag(TAG).i("Alarm canceled successfully -- code=$noteId")
        }

        return true
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "error in ${if (doesCreate) "setting" else "deleting"} alarm",
            Toast.LENGTH_LONG,
        ).show()
//        Timber.tag(TAG).e(e)
        return false
    }
}
