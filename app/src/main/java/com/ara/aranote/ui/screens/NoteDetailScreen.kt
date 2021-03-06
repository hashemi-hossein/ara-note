package com.ara.aranote.ui.screens

import android.content.Context
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.RestorePage
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doAfterTextChanged
import com.ara.aranote.R
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.viewmodels.NoteDetailViewModel
import com.ara.aranote.domain.viewmodels.NoteDetailViewModel.TheOperation
import com.ara.aranote.ui.components.HAppBar
import com.ara.aranote.ui.components.HDropdown
import com.ara.aranote.ui.components.showSnackbar
import com.ara.aranote.util.DateTimeFormatPattern
import com.ara.aranote.util.HDateTime
import com.ara.aranote.util.TAG
import com.ara.aranote.util.alarm.hManageAlarm
import com.ara.aranote.util.change
import com.ara.aranote.util.millis
import com.ara.aranote.util.minus
import com.ara.aranote.util.plus
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    navigateUp: () -> Unit,
) {
    val note: Note by viewModel.note.collectAsState()
    val isNewNote = viewModel.isNewNote
    val notebooks: List<Notebook> by viewModel.notebooks.collectAsState()
    val isAutoNoteSaving by viewModel.appDataStore.isAutoSaveMode.collectAsState(initial = true)
    val isModified by viewModel.isModified.collectAsState()

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    val onBackPressed: (TheOperation) -> Unit = { theOperation ->
        if (theOperation == TheOperation.DISCARD) {
            if (viewModel.isModified.value)
                showSnackbar(
                    scope = scope,
                    snackbarHostState = scaffoldState.snackbarHostState,
                    actionLabel = context.getString(R.string.discard)
                ) {
                    navigateUp()
                }
            else
                navigateUp()
        } else {
            val deleteOrSaveOperation = {
                viewModel.backPressed(
                    isNewNote = isNewNote,
                    doesDelete = theOperation == TheOperation.DELETE,
                    navigateUp = navigateUp,
                    disableAlarm = {
                        hManageAlarm(
                            context = context,
                            doesCreate = false,
                            noteId = it,
                        )
                    },
                    onOperationError = {
                        Toast.makeText(context, "error in operation occurred", Toast.LENGTH_LONG)
                            .show()
                    },
                )
            }
            if (theOperation == TheOperation.DELETE) {
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

    val modalBottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        confirmStateChange = {
            println("modalBottomSheetValue = $it")
            true
        },
    )

    NoteDetailScreen(
        note = note,
        notebooks = notebooks,
        onNoteChanged = viewModel::modifyNote,
        onNoteTextChanged = { viewModel.modifyNote(viewModel.note.value.copy(text = it)) },
        onBackPressed = onBackPressed,
        isNewNote = isNewNote,
        isModified = isModified,
        restoreNote = viewModel::restoreNote,
        isAutoNoteSaving = isAutoNoteSaving,
        modalBottomSheetState = modalBottomSheetState,
        scaffoldState = scaffoldState,
        scope = scope,
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun NoteDetailScreen(
    note: Note,
    notebooks: List<Notebook>,
    onNoteChanged: (Note) -> Unit,
    onNoteTextChanged: (String) -> Unit,
    onBackPressed: (TheOperation) -> Unit,
    isNewNote: Boolean,
    isModified: Boolean,
    restoreNote: () -> Unit,
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
                    title = /*if (isNewNote) stringResource(R.string.add_note) else*/ "",
                    icon = if (isAutoNoteSaving) Icons.Default.Done else Icons.Default.ArrowBack,
                    actions = {
                        HAppBarActions(
                            note = note,
                            notebooks = notebooks,
                            onNoteChanged = onNoteChanged,
                            onBackPressed = onBackPressed,
                            isNewNote = isNewNote,
                            isModified = isModified,
                            restoreNote = restoreNote,
                            scaffoldState = scaffoldState,
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
                note = note,
                onNoteTextChanged = onNoteTextChanged,
                isModified = isModified,
                isNewNote = isNewNote,
            )
        }
    }
}

@Composable
private fun HBody(
    innerPadding: PaddingValues,
    note: Note,
    onNoteTextChanged: (String) -> Unit,
    isModified: Boolean,
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
                        HDateTime.gerPrettyDateTime(note.alarmDateTime)
                    else "",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.alpha(0.4f)
            )
        }
        var runOnce by remember { mutableStateOf(false) }
        var runOnceOnModifying by remember(isModified) { mutableStateOf(false) }
        AndroidView(
            factory = { context ->
                val editText = TextInputEditText(context).apply {
                    background = null
                    gravity = Gravity.TOP
                    textSize = 17f
                    hint = context.getString(R.string.type_here)
                    setHorizontallyScrolling(false)
                    requestFocus()
//                    setTypeface(Typeface.createFromAsset(context.assets,""))
                    doAfterTextChanged {
                        onNoteTextChanged(it?.toString() ?: "")
                    }
                }
                NestedScrollView(context).apply {
                    isFillViewport = true
                    addView(editText)
                }
            },
            update = { view ->
                if (note.text.isNotEmpty() && (view[0] as TextInputEditText).text?.isEmpty() == true) {
                    (view[0] as TextInputEditText).setText(note.text)
                    (view[0] as TextInputEditText).setSelection(note.text.length)
                    runOnceOnModifying = true
                }
                if (!runOnceOnModifying && !isModified) {
                    runOnceOnModifying = true
                    val previousSelection = (view[0] as TextInputEditText).selectionStart
                    (view[0] as TextInputEditText).setText(note.text)
                    (view[0] as TextInputEditText).setSelection(previousSelection)
                }

                if (!runOnce && note.id != 0 && isNewNote) {
                    runOnce = true
                    val imm: InputMethodManager? =
                        view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.showSoftInput(view[0], InputMethodManager.SHOW_FORCED)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
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
    isModified: Boolean,
    restoreNote: () -> Unit,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    context: Context,
    modalBottomSheetState: ModalBottomSheetState,
    keyboardController: SoftwareKeyboardController?
) {
    val doesHasAlarm = note.alarmDateTime != null

    AnimatedVisibility(isModified) {
        IconButton(onClick = {
            showSnackbar(
                scope,
                scaffoldState.snackbarHostState,
                actionLabel = "Restore Note",
                onClick = restoreNote
            )
        }) {
            Icon(
                imageVector = Icons.Default.RestorePage,
                contentDescription = "Restore Note"
            )
        }
    }
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
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
        notebooks = listOf(),
        onNoteChanged = {},
        onNoteTextChanged = {},
        onBackPressed = {},
        isNewNote = true,
        isModified = false,
        restoreNote = {},
    )
}
