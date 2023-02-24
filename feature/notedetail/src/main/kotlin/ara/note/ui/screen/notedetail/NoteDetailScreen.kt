package ara.note.ui.screen.notedetail

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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import ara.note.alarm.hManageAlarm
import ara.note.domain.entity.Note
import ara.note.domain.entity.Notebook
import ara.note.notedetail.R.string
import ara.note.ui.component.HAppBar
import ara.note.ui.component.HDropdown
import ara.note.ui.component.HSnackbarHost
import ara.note.ui.component.showSnackbar
import ara.note.ui.screen.notedetail.NoteDetailIntent.ModifyNote
import ara.note.ui.screen.notedetail.NoteDetailSingleEvent.BackPressed
import ara.note.ui.screen.notedetail.NoteDetailViewModel.TheOperation
import ara.note.ui.screen.notedetail.NoteDetailViewModel.TheOperation.DISCARD
import ara.note.ui.screen.notedetail.NoteDetailViewModel.TheOperation.SAVE
import ara.note.util.DateTimeFormatPattern
import ara.note.util.HDateTime
import ara.note.util.change
import ara.note.util.millis
import ara.note.util.minus
import ara.note.util.plus
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    navigateUp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
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
                            snackbarHostState = snackbarHostState,
                            actionLabel = context.getString(string.discard),
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
                                snackbarHostState = snackbarHostState,
                                actionLabel = context.getString(string.delete),
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
        onNoteChanged = { viewModel.sendIntent(ModifyNote(it)) },
        onBackPressed = { viewModel.triggerSingleEvent(BackPressed(it)) },
        isAutoNoteSaving = uiState.userPreferences.isAutoSaveMode,
        modalBottomSheetState = modalBottomSheetState,
        snackbarHostState = snackbarHostState,
        scope = scope,
    )
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
internal fun NoteDetailScreen(
    uiState: NoteDetailState,
    onNoteChanged: (Note) -> Unit,
    onBackPressed: (TheOperation) -> Unit,
    isAutoNoteSaving: Boolean = true,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
    modalBottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
    ),
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
) {
    BackHandler(onBack = {
        if (modalBottomSheetState.isVisible) {
            scope.launch { modalBottomSheetState.hide() }
        } else {
            onBackPressed(if (isAutoNoteSaving) TheOperation.SAVE else TheOperation.DISCARD)
        }
    })
    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            HBottomSheet(
                uiState = uiState,
                onNoteChanged = onNoteChanged,
                scope = scope,
                snackbarHostState = snackbarHostState,
                context = context,
                modalBottomSheetState = modalBottomSheetState,
            )
        },
    ) {
        Scaffold(
            snackbarHost = { HSnackbarHost(hostState = snackbarHostState) },
            topBar = {
                HAppBar(
                    title = /*if (isNewNote) stringResource(string.add_note) else*/ "",
                    icon = if (isAutoNoteSaving) Icons.Default.Done else Icons.Default.ArrowBack,
                    actions = {
                        HAppBarActions(
                            uiState = uiState,
                            onNoteChanged = onNoteChanged,
                            onBackPressed = onBackPressed,
                            scope = scope,
                            context = context,
                            modalBottomSheetState = modalBottomSheetState,
                            keyboardController = keyboardController,
                        )
                    },
                    onNavButtonClick = {
                        keyboardController?.hide()
                        onBackPressed(if (isAutoNoteSaving) SAVE else DISCARD)
                    },
                )
            },
            floatingActionButton = {
                if (!isAutoNoteSaving) {
                    FloatingActionButton(onClick = {
                        onBackPressed(TheOperation.SAVE)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = stringResource(string.cd_save),
                        )
                    }
                }
            },
        ) { innerPadding ->
            HBody(
                innerPadding = innerPadding,
                uiState = uiState,
                onNoteChanged = onNoteChanged,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HBody(
    innerPadding: PaddingValues,
    uiState: NoteDetailState,
    onNoteChanged: (Note) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 10.dp),
    ) {
        Divider(Modifier.padding(vertical = 3.dp))
        if (!uiState.isNewNote) {
            Text(
                text = "Modified at " +
                    HDateTime.formatDateAndTime(
                        dateTime = uiState.note.modifiedDateTime,
                        dateTimeFormatPattern = DateTimeFormatPattern.DATE_TIME,
                    ),
                modifier = Modifier.alpha(0.7f),
            )
            Spacer(Modifier.padding(vertical = 3.dp))
        }
        AnimatedVisibility(uiState.note.alarmDateTime != null) {
            Text(
                text = "Alarm has been set for " +
                    uiState.note.alarmDateTime?.let {
                        HDateTime.gerPrettyDateTime(it)
                    },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.4f),
            )
        }

        val focusRequester = remember { FocusRequester() }
        TextField(
            value = uiState.note.text,
            onValueChange = { onNoteChanged(uiState.note.copy(text = it)) },
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                textDirection = TextDirection.Content,
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
            placeholder = { Text(text = stringResource(id = string.type_here)) },
        )

        LaunchedEffect(uiState.isNewNote) {
            if (uiState.isNewNote) {
                focusRequester.requestFocus()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun HAppBarActions(
    uiState: NoteDetailState,
    onNoteChanged: (Note) -> Unit,
    onBackPressed: (TheOperation) -> Unit,
    scope: CoroutineScope,
    context: Context,
    modalBottomSheetState: ModalBottomSheetState,
    keyboardController: SoftwareKeyboardController?,
) {
    val doesHasAlarm = uiState.note.alarmDateTime != null

    IconButton(onClick = {
        keyboardController?.hide()
        onBackPressed(if (!uiState.isNewNote) TheOperation.DELETE else TheOperation.DISCARD)
    }) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = if (!uiState.isNewNote) {
                stringResource(string.cd_delete)
            } else {
                stringResource(string.cd_discard)
            },
        )
    }
    // TODO: fix alarm
//    IconButton(onClick = {
//        keyboardController?.hide()
//        scope.launch { modalBottomSheetState.show() }
//    }) {
//        Icon(
//            imageVector = if (doesHasAlarm) Icons.Default.Alarm else Icons.Default.AlarmAdd,
//            contentDescription = if (doesHasAlarm) {
//                stringResource(string.cd_edit_note_alarm)
//            } else {
//                stringResource(string.cd_add_alarm)
//            },
//        )
//    }
    AnimatedVisibility(doesHasAlarm) {
        IconButton(onClick = {
            hManageAlarm(
                context = context,
                doesCreate = false,
                noteId = uiState.note.id,
            )
            onNoteChanged(uiState.note.copy(alarmDateTime = null))
        }) {
            Icon(
                imageVector = Icons.Default.AlarmOff,
                contentDescription = stringResource(string.cd_delete_alarm),
            )
        }
    }
    if (uiState.notebooks.isNotEmpty()) {
        HDropdown(
            items = uiState.notebooks.associate { it.id to it.name },
            selectedKey = uiState.note.notebookId,
            onItemClick = { onNoteChanged(uiState.note.copy(notebookId = it)) },
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HBottomSheet(
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
                    MaterialDatePicker.Builder.datePicker()
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
                            dateTimeFormatPattern = DateTimeFormatPattern.DATE,
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
                            dateTimeFormatPattern = DateTimeFormatPattern.TIME,
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
