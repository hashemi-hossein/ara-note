package com.ara.aranote.ui.screen.note_detail

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.Divider
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
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ara.aranote.R
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.ui.component.HAppBar
import com.ara.aranote.ui.component.HDropdown
import com.ara.aranote.ui.component.showSnackbar
import com.ara.aranote.ui.screen.note_detail.NoteDetailViewModel.TheOperation
import com.ara.aranote.util.DateTimeFormatPattern
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class,
    ExperimentalLifecycleComposeApi::class
)
@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    navigateUp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.singleEvent.onEach {
            when (it) {
                is NoteDetailSingleEvent.NavigateUp -> navigateUp()

                is NoteDetailSingleEvent.DisableAlarm ->
                    hManageAlarm(context = context, doesCreate = false, noteId = it.noteId)

                is NoteDetailSingleEvent.OperationError ->
                    Toast.makeText(context, "error in operation occurred", Toast.LENGTH_LONG)
                        .show()

                is NoteDetailSingleEvent.BackPressed -> {
                    if (it.theOperation == TheOperation.DISCARD) {
                        showSnackbar(
                            scope = scope,
                            snackbarHostState = scaffoldState.snackbarHostState,
                            actionLabel = context.getString(R.string.discard)
                        ) {
                            navigateUp()
                        }
                    } else {
                        val deleteOrSaveOperation = {
                            viewModel.sendIntent(NoteDetailIntent.BackPressed(doesDelete = it.theOperation == TheOperation.DELETE))
                        }
                        if (it.theOperation == TheOperation.DELETE) {
                            showSnackbar(
                                scope = scope,
                                snackbarHostState = scaffoldState.snackbarHostState,
                                actionLabel = context.getString(R.string.delete)
                            ) {
                                deleteOrSaveOperation()
                            }
                        } else {
                            deleteOrSaveOperation()
                        }
                    }
                }
            }
        }.collect()
    }

    val modalBottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        confirmStateChange = {
            println("modalBottomSheetValue = $it")
            true
        },
    )

    NoteDetailScreen(
        uiState = uiState,
        onNoteChanged = { viewModel.sendIntent(NoteDetailIntent.ModifyNote(it)) },
        onBackPressed = { viewModel.triggerSingleEvent(NoteDetailSingleEvent.BackPressed(it)) },
        isNewNote = uiState.isNewNote,
        isAutoNoteSaving = uiState.userPreferences.isAutoSaveMode,
        modalBottomSheetState = modalBottomSheetState,
        scaffoldState = scaffoldState,
        scope = scope,
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun NoteDetailScreen(
    uiState: NoteDetailState,
    onNoteChanged: (Note) -> Unit,
    onBackPressed: (TheOperation) -> Unit,
    isNewNote: Boolean,
    isAutoNoteSaving: Boolean = true,
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
            onBackPressed(if (isAutoNoteSaving) TheOperation.SAVE else TheOperation.DISCARD)
    })
    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            HBottomSheet(
                note = uiState.note,
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
                    title = /*if (isNewNote) stringResource(R.string.add_note) else*/ "",
                    icon = if (isAutoNoteSaving) Icons.Default.Done else Icons.Default.ArrowBack,
                    actions = {
                        HAppBarActions(
                            note = uiState.note,
                            notebooks = uiState.notebooks,
                            onNoteChanged = onNoteChanged,
                            onBackPressed = onBackPressed,
                            isNewNote = isNewNote,
                            scope = scope,
                            context = context,
                            modalBottomSheetState = modalBottomSheetState,
                            keyboardController = keyboardController,
                        )
                    },
                    onNavButtonClick = {
                        keyboardController?.hide()
                        onBackPressed(if (isAutoNoteSaving) TheOperation.SAVE else TheOperation.DISCARD)
                    },
                )
            },
            floatingActionButton = {
                if (!isAutoNoteSaving)
                    FloatingActionButton(onClick = {
                        onBackPressed(TheOperation.SAVE)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = stringResource(R.string.cd_save)
                        )
                    }
            },
        ) { innerPadding ->
            HBody(
                innerPadding = innerPadding,
                note = uiState.note,
                onNoteChanged = onNoteChanged,
                isNewNote = isNewNote,
            )
        }
    }
}

@Composable
private fun HBody(
    innerPadding: PaddingValues,
    note: Note,
    onNoteChanged: (Note) -> Unit,
    isNewNote: Boolean,
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 10.dp)
    ) {
        Divider(Modifier.padding(vertical = 3.dp))
        if (!isNewNote) {
            Text(
                text = "Created at " +
                    HDateTime.formatDateAndTime(
                        dateTime = note.addedDateTime,
                        dateTimeFormatPattern = DateTimeFormatPattern.DATE_TIME
                    ),
                modifier = Modifier.alpha(0.7f)
            )
            Spacer(Modifier.padding(vertical = 3.dp))
        }
        AnimatedVisibility(note.alarmDateTime != null) {
            Text(
                text = "Alarm has been set for " +
                    if (note.alarmDateTime != null)
                        HDateTime.gerPrettyDateTime(note.alarmDateTime!!)
                    else "",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.alpha(0.4f)
            )
        }

        val focusRequester = remember { FocusRequester() }
        TextField(
            value = note.text,
            onValueChange = { onNoteChanged(note.copy(text = it)) },
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.body1.copy(
                textDirection = TextDirection.Content,
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            ),
            placeholder = { Text(text = stringResource(id = R.string.type_here)) },
        )

        LaunchedEffect(true) {
            if (isNewNote)
                focusRequester.requestFocus()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun HAppBarActions(
    note: Note,
    notebooks: List<Notebook>,
    onNoteChanged: (Note) -> Unit,
    onBackPressed: (TheOperation) -> Unit,
    isNewNote: Boolean,
    scope: CoroutineScope,
    context: Context,
    modalBottomSheetState: ModalBottomSheetState,
    keyboardController: SoftwareKeyboardController?
) {
    val doesHasAlarm = note.alarmDateTime != null

    IconButton(onClick = {
        keyboardController?.hide()
        onBackPressed(if (!isNewNote) TheOperation.DELETE else TheOperation.DISCARD)
    }) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = if (!isNewNote) stringResource(R.string.cd_delete)
            else stringResource(R.string.cd_discard)
        )
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
    AnimatedVisibility(doesHasAlarm) {
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
    if (notebooks.isNotEmpty())
        HDropdown(
            items = notebooks.associate { it.id to it.name },
            selectedIndex = note.notebookId,
            onItemClick = { onNoteChanged(note.copy(notebookId = it)) },
        )
}

@OptIn(ExperimentalMaterialApi::class)
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
                        text = HDateTime.formatDateAndTime(
                            dateTime = dateTime,
                            dateTimeFormatPattern = DateTimeFormatPattern.DATE
                        ),
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
                        text = HDateTime.formatDateAndTime(
                            dateTime = dateTime,
                            dateTimeFormatPattern = DateTimeFormatPattern.TIME
                        ),
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
                    if (dateTime.minus(HDateTime.getCurrentDateTime()) > 1.minutes) {
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

// @OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
// @Preview(
// //    showBackground = true,
// //    backgroundColor = 0xff2ff2f2,
// //    widthDp = 200,
// //    heightDp = 300,
// //    showSystemUi = true,
// )
// @Composable
// private fun HPreview() {
//    NoteDetailScreen(
//        note = Note(
//            id = 1,
//            notebookId = 1,
//            text = "Hello!",
//            addedDateTime = HDateTime.getCurrentDateTime()
//        ),
//        notebooks = emptyList(),
//        onNoteChanged = {},
//        onBackPressed = {},
//        isNewNote = true,
//        isModified = false,
//        restoreNote = {},
//    )
// }
