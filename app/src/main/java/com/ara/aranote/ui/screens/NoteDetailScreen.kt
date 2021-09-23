package com.ara.aranote.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ara.aranote.R
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.viewmodels.NoteDetailViewModel
import com.ara.aranote.ui.components.AppBarNavButtonType
import com.ara.aranote.ui.components.HAppBar
import com.ara.aranote.ui.components.showSnackbar
import com.ara.aranote.util.HDateTime
import com.ara.aranote.util.TAG
import com.ara.aranote.util.alarm.hManageAlarm
import com.ara.aranote.util.change
import com.ara.aranote.util.millis
import com.ara.aranote.util.minus
import com.ara.aranote.util.plus
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    id: Int,
    navigateUp: () -> Unit,
) {
    val isNewNote = id < 0
    val note: Note by viewModel.note.collectAsState()
    val context = LocalContext.current

    val onBackPressed: (Boolean) -> Unit = { doesDelete ->
        viewModel.backPressed(
            isNewNote = isNewNote,
            doesDelete = doesDelete,
            navigateUp = navigateUp,
            disableAlarm = {
                hManageAlarm(
                    context = context,
                    doesCreate = false,
                    noteId = it,
                )
            },
            onOperationError = {
                Toast.makeText(context, "error in operation occurred", Toast.LENGTH_LONG).show()
            },
        )
    }

    NoteDetailScreen(
        note = note,
        onNoteChanged = viewModel::modifyNote,
        onBackPressed = onBackPressed,
        isNewNote = isNewNote,
    )
}

@Composable
internal fun NoteDetailScreen(
    note: Note,
    onNoteChanged: (Note) -> Unit,
    onBackPressed: (Boolean) -> Unit,
    isNewNote: Boolean,
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    BackHandler(onBack = { onBackPressed(false) })
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HAppBar(
                title = if (isNewNote) stringResource(R.string.add_note) else "",
                appBarNavButtonType = AppBarNavButtonType.BACK,
                actions = {
                    HAppBarActions(
                        note = note,
                        onNoteChanged = onNoteChanged,
                        onBackPressed = onBackPressed,
                        isNewNote = isNewNote,
                        scope = scope,
                        scaffoldState = scaffoldState
                    )
                },
                onNavButtonClick = { onBackPressed(false) },
            )
        },
        floatingActionButton = {
            if (isNewNote)
                FloatingActionButton(onClick = {
                    if (note.text.isNotEmpty()) {
                        showSnackbar(
                            scope = scope,
                            snackbarHostState = scaffoldState.snackbarHostState,
                            actionLabel = context.getString(R.string.discard)
                        ) {
                            onBackPressed(true)
                        }
                    } else
                        onBackPressed(true)
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.cd_discard)
                    )
                }
        },
    ) { innerPadding ->
        HBody(
            innerPadding = innerPadding,
            note = note,
            onNoteChanged = onNoteChanged,
        )
    }
}

@Composable
private fun HBody(
    innerPadding: PaddingValues,
    note: Note,
    onNoteChanged: (Note) -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(innerPadding)
    ) {
        TextField(
            value = note.text,
            onValueChange = { onNoteChanged(note.copy(text = it)) },
            modifier = Modifier
                .fillMaxSize(),
            textStyle = MaterialTheme.typography.body1,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            ),
//            shape = MaterialTheme.shapes.medium.copy(ZeroCornerSize),
        )
    }
}

@Composable
private fun HAppBarActions(
    note: Note,
    onNoteChanged: (Note) -> Unit,
    onBackPressed: (Boolean) -> Unit,
    isNewNote: Boolean,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
) {
    val context = LocalContext.current

    val doesHasAlarm = note.alarmDateTime != null

    if (!isNewNote)
        IconButton(onClick = {
            showSnackbar(
                scope = scope,
                snackbarHostState = scaffoldState.snackbarHostState,
                actionLabel = context.getString(R.string.delete)
            ) {
                onBackPressed(true)
            }
        }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
        }
    IconButton(onClick = {
        if (context as? AppCompatActivity != null)
            btnAlarmOnClick(
                note = note,
                onNoteChanged = onNoteChanged,
                scope = scope,
                scaffoldState = scaffoldState,
                activity = context,
            )
    }) {
        Icon(
            imageVector = if (doesHasAlarm) Icons.Default.Alarm else Icons.Default.AlarmAdd,
            contentDescription = if (doesHasAlarm) stringResource(R.string.cd_edit_note_alarm)
            else stringResource(R.string.cd_add_alarm)
        )
    }
    if (doesHasAlarm)
        IconButton(onClick = {
            if (context as? AppCompatActivity != null)
                hManageAlarm(
                    context = context,
                    doesCreate = false,
                    noteId = note.id,
                )
            onNoteChanged(note.copy(alarmDateTime = null))
        }) {
            Icon(
                imageVector = Icons.Default.AlarmOff,
                contentDescription = stringResource(R.string.cd_delete_alarm)
            )
        }
}

@OptIn(ExperimentalTime::class)
private fun btnAlarmOnClick(
    note: Note,
    onNoteChanged: (Note) -> Unit,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    activity: AppCompatActivity,
) {
    var dateTime =
        note.alarmDateTime ?: HDateTime.getCurrentDateTime()
            .plus(Duration.minutes(1))
    val showTimePickerDialog = {
        MaterialTimePicker.Builder()
//            .setTitleText("")
//            .setInputMode(INPUT_MODE_CLOCK)
//            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(dateTime.hour)
            .setMinute(dateTime.minute)
            .build().apply {
                addOnPositiveButtonClickListener {
                    Timber.tag(TAG).d("%d:%d", this.hour, this.minute)
                    dateTime =
                        dateTime.change(
                            hour = this.hour,
                            minute = this.minute,
                            second = 0,
                            nanosecond = 0
                        )

                    if (dateTime.minus(HDateTime.getCurrentDateTime()) > Duration.seconds(1)
                    ) {
                        hManageAlarm(
                            context = activity,
                            doesCreate = true,
                            noteId = note.id,
                            triggerAtMillis = dateTime.millis(),
                        )
                        onNoteChanged(note.copy(alarmDateTime = dateTime))
                    } else {
                        showSnackbar(
                            scope = scope,
                            scaffoldState.snackbarHostState,
                            message = "invalid Date or Time",
                            actionLabel = "OK"
                        )
                    }
                }
            }.show(activity.supportFragmentManager, "time_picker")
    }

    MaterialDatePicker.Builder.datePicker()
        .setSelection(dateTime.millis())
        .build().apply {
            addOnPositiveButtonClickListener {
                it?.let {
                    val result = HDateTime.getDateTimeFromMillis(it)
                    Timber.tag(TAG).d(result.toString())
                    dateTime = dateTime.change(
                        year = result.year,
                        month = result.monthNumber,
                        day = result.dayOfMonth
                    )
                }
                showTimePickerDialog()
            }
            addOnCancelListener { showTimePickerDialog() }
//                                addOnDismissListener { showTimePickerDialog() }
            addOnNegativeButtonClickListener { showTimePickerDialog() }
        }.show(activity.supportFragmentManager, "date_picker")
}

@Preview(
//    showBackground = true,
//    backgroundColor = 0xff2ff2f2,
//    widthDp = 200,
//    heightDp = 300,
//    showSystemUi = true,
)
@Composable
private fun HPreview() {
    NoteDetailScreen(
        note = Note(0, "Hello!", HDateTime.getCurrentDateTime()),
        onNoteChanged = {},
        onBackPressed = {},
        isNewNote = true,
    )
}
