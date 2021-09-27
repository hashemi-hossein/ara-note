package com.ara.aranote.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterialApi::class)
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

    val modalBottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        confirmStateChange = {
            println("modalBottomSheetValue = $it")
            true
        },
    )

    NoteDetailScreen(
        note = note,
        onNoteChanged = viewModel::modifyNote,
        onBackPressed = onBackPressed,
        isNewNote = isNewNote,
        modalBottomSheetState = modalBottomSheetState,
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun NoteDetailScreen(
    note: Note,
    onNoteChanged: (Note) -> Unit,
    onBackPressed: (Boolean) -> Unit,
    isNewNote: Boolean,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
    modalBottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    ),
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
) {
    BackHandler(onBack = {
        if (modalBottomSheetState.isVisible)
            scope.launch { modalBottomSheetState.hide() }
        else
            onBackPressed(false)
    })
    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            HBottomSheet(
                note = note,
                onNoteChanged = onNoteChanged,
                scope = scope,
                scaffoldState = scaffoldState,
                context = context,
                modalBottomSheetState = modalBottomSheetState,
            )
        }
    ) {
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
                            scaffoldState = scaffoldState,
                            context = context,
                            modalBottomSheetState = modalBottomSheetState,
                            keyboardController = keyboardController,
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun HAppBarActions(
    note: Note,
    onNoteChanged: (Note) -> Unit,
    onBackPressed: (Boolean) -> Unit,
    isNewNote: Boolean,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    context: Context,
    modalBottomSheetState: ModalBottomSheetState,
    keyboardController: SoftwareKeyboardController?
) {
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
        keyboardController?.hide()
        scope.launch { modalBottomSheetState.show() }
    }) {
        Icon(
            imageVector = if (doesHasAlarm) Icons.Default.Alarm else Icons.Default.AlarmAdd,
            contentDescription = if (doesHasAlarm) stringResource(R.string.cd_edit_note_alarm)
            else stringResource(R.string.cd_add_alarm)
        )
    }
    if (doesHasAlarm)
        IconButton(onClick = {
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

@OptIn(ExperimentalTime::class, ExperimentalMaterialApi::class)
@Composable
private fun HBottomSheet(
    note: Note,
    onNoteChanged: (Note) -> Unit,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    context: Context,
    modalBottomSheetState: ModalBottomSheetState,
) {
    var dateTime by remember(modalBottomSheetState.isVisible) {
        mutableStateOf(note.alarmDateTime ?: HDateTime.getCurrentDateTime())
    }
    Column(
        modifier = Modifier.padding(vertical = 30.dp, horizontal = 10.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Date: ", style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = {
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
                            }
                        }.show((context as AppCompatActivity).supportFragmentManager, "date_picker")
                }) {
                    Text(
                        text = HDateTime.formatDateAndTime(dateTime = dateTime, isDate = true),
                        style = MaterialTheme.typography.body1,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Time: ", style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = {
                    MaterialTimePicker.Builder()
                        .setHour(dateTime.hour)
                        .setMinute(dateTime.minute)
                        .build().apply {
                            addOnPositiveButtonClickListener {
                                Timber.tag(TAG).d("%d:%d", this.hour, this.minute)
                                dateTime = dateTime.change(
                                    hour = this.hour,
                                    minute = this.minute,
                                    second = 0,
                                    nanosecond = 0
                                )
                            }
                        }.show((context as AppCompatActivity).supportFragmentManager, "time_picker")
                }) {
                    Text(
                        text = HDateTime.formatDateAndTime(dateTime = dateTime, isDate = false),
                        style = MaterialTheme.typography.body1,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        LazyRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Button(onClick = {
                    dateTime = dateTime.plus(Duration.minutes(10))
                }) {
                    Text(text = "+10 mins")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = {
                    dateTime = dateTime.plus(Duration.minutes(30))
                }) {
                    Text(text = "+30 mins")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = {
                    dateTime = dateTime.plus(Duration.hours(1))
                }) {
                    Text(text = "+1 hour")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = {
                    dateTime = dateTime.plus(Duration.hours(3))
                }) {
                    Text(text = "+3 hours")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = {
                    dateTime = dateTime.plus(Duration.hours(24))
                }) {
                    Text(text = "+1 day")
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = {
                dateTime = HDateTime.getCurrentDateTime()
            }) {
                Icon(
                    imageVector = Icons.Default.Undo,
                    contentDescription = stringResource(R.string.cd_reset_date_and_time)
                )
            }
            OutlinedButton(
                onClick = {
                    if (dateTime.minus(HDateTime.getCurrentDateTime()) > Duration.minutes(1)) {
                        hManageAlarm(
                            context = context,
                            doesCreate = true,
                            noteId = note.id,
                            triggerAtMillis = dateTime.millis(),
                        )
                        onNoteChanged(note.copy(alarmDateTime = dateTime))
                    } else {
                        showSnackbar(
                            scope = scope,
                            scaffoldState.snackbarHostState,
                            message = context.getString(R.string.invalid_date_and_time),
                            actionLabel = context.getString(R.string.ok)
                        )
                    }
                    scope.launch { modalBottomSheetState.hide() }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(R.string.cd_set_alarm)
                )
            }
        }
    }
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
        note = Note(
            id = 1,
            notebookId = 1,
            text = "Hello!",
            addedDateTime = HDateTime.getCurrentDateTime()
        ),
        onNoteChanged = {},
        onBackPressed = {},
        isNewNote = true,
    )
}
