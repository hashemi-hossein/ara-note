package ara.note.ui.component

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ara.note.alarm.hManageAlarm
import ara.note.domain.entity.Note
import ara.note.notedetail.R.string
import ara.note.ui.screen.notedetail.NoteDetailState
import ara.note.util.DateTimeFormatPattern.DATE
import ara.note.util.DateTimeFormatPattern.TIME
import ara.note.util.HDateTime
import ara.note.util.change
import ara.note.util.millis
import ara.note.util.minus
import ara.note.util.plus
import com.google.android.material.datepicker.MaterialDatePicker.Builder
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun AlarmDateTimePickerBottomSheet(
    uiState: NoteDetailState,
    onNoteChanged: (Note) -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context,
    modalBottomSheetState: ModalBottomSheetState,
) {
    var dateTime by remember(modalBottomSheetState.isVisible) {
        mutableStateOf(uiState.note.alarmDateTime ?: HDateTime.getCurrentDateTime())
    }
    Column(
        modifier = Modifier.padding(vertical = 30.dp, horizontal = 10.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Date: ", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = {
                    Builder.datePicker()
                        .setSelection(dateTime.millis())
                        .build().apply {
                            addOnPositiveButtonClickListener {
                                it?.let {
                                    val result = HDateTime.getDateTimeFromMillis(it)
//                                    Timber.tag(TAG).d(result.toString())
                                    dateTime = dateTime.change(
                                        year = result.year,
                                        month = result.monthNumber,
                                        day = result.dayOfMonth,
                                    )
                                }
                            }
                        }.show((context as AppCompatActivity).supportFragmentManager, "date_picker")
                }) {
                    Text(
                        text = HDateTime.formatDateAndTime(
                            dateTime = dateTime,
                            dateTimeFormatPattern = DATE,
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Time: ", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = {
                    MaterialTimePicker.Builder()
                        .setHour(dateTime.hour)
                        .setMinute(dateTime.minute)
                        .build().apply {
                            addOnPositiveButtonClickListener {
//                                Timber.tag(TAG).d("%d:%d", this.hour, this.minute)
                                dateTime = dateTime.change(
                                    hour = this.hour,
                                    minute = this.minute,
                                    second = 0,
                                    nanosecond = 0,
                                )
                            }
                        }.show((context as AppCompatActivity).supportFragmentManager, "time_picker")
                }) {
                    Text(
                        text = HDateTime.formatDateAndTime(
                            dateTime = dateTime,
                            dateTimeFormatPattern = TIME,
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        LazyRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(),
        ) {
            item {
                Button(onClick = {
                    dateTime = dateTime.plus(10.minutes)
                }) {
                    Text(text = "+10 mins")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = {
                    dateTime = dateTime.plus(30.minutes)
                }) {
                    Text(text = "+30 mins")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = {
                    dateTime = dateTime.plus(1.hours)
                }) {
                    Text(text = "+1 hour")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = {
                    dateTime = dateTime.plus(3.hours)
                }) {
                    Text(text = "+3 hours")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = {
                    dateTime = dateTime.plus(24.hours)
                }) {
                    Text(text = "+1 day")
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedButton(onClick = {
                dateTime = HDateTime.getCurrentDateTime()
            }) {
                Icon(
                    imageVector = Icons.Default.Undo,
                    contentDescription = stringResource(string.cd_reset_date_and_time),
                )
            }
            OutlinedButton(
                onClick = {
                    if (dateTime.minus(HDateTime.getCurrentDateTime()) > 1.minutes) {
                        hManageAlarm(
                            context = context,
                            doesCreate = true,
                            noteId = uiState.note.id,
                            triggerAtMillis = dateTime.millis(),
                        )
                        onNoteChanged(uiState.note.copy(alarmDateTime = dateTime))
                    } else {
                        showSnackbar(
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            message = context.getString(string.invalid_date_and_time),
                            actionLabel = context.getString(string.ok),
                        )
                    }
                    scope.launch { modalBottomSheetState.hide() }
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(string.cd_set_alarm),
                )
            }
        }
    }
}
