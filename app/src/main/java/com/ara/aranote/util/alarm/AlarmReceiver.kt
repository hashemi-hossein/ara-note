package com.ara.aranote.util.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ara.aranote.R
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.ui.main.MainActivity
import com.ara.aranote.util.TAG
import dagger.hilt.android.AndroidEntryPoint
import io.karn.notify.Notify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: NoteRepository

    private val job: Job = Job()
    private val coroutineScope = CoroutineScope(job + Dispatchers.IO)

    override fun onReceive(context: Context?, intent: Intent?) {
        val requestCode = intent?.extras?.getInt("requestCode")
        Timber.tag(TAG).d("AlarmReceiver -- onReceive -- requestCode=$requestCode")

        if (context != null && requestCode != null) {
            coroutineScope.launch {
                val note = repository.getNote(requestCode)
                println(note)

                if (note != null) {
                    Notify.with(context)
                        .header {
                            icon = R.drawable.ic_outline_alarm_on_24
                        }
                        .content {
                            title = note.text
                        }
                        .meta {
                            clickIntent = PendingIntent.getActivity(
                                context,
                                0,
                                Intent(context, MainActivity::class.java)
                                    .putExtra("requestCode", requestCode),
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                                else
                                    PendingIntent.FLAG_ONE_SHOT
                            )
                        }
                        .show()
                    repository.updateNote(note.copy(alarmDateTime = null))
                }
            }
        }
    }
}
