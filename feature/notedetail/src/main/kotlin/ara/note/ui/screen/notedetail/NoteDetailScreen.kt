package ara.note.ui.screen.notedetail

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import ara.note.alarm.hManageAlarm
import ara.note.domain.entity.Note
import ara.note.notedetail.R.string
import ara.note.ui.component.AlarmDateTimePickerBottomSheet
import ara.note.ui.component.HAppBar
import ara.note.ui.component.HAppBarActions
import ara.note.ui.component.HBody
import ara.note.ui.component.HSnackbarHost
import ara.note.ui.component.showSnackbar
import ara.note.ui.screen.notedetail.NoteDetailIntent.ModifyNote
import ara.note.ui.screen.notedetail.NoteDetailSingleEvent.BackPressed
import ara.note.ui.screen.notedetail.NoteDetailViewModel.TheOperation
import ara.note.ui.screen.notedetail.NoteDetailViewModel.TheOperation.DISCARD
import ara.note.ui.screen.notedetail.NoteDetailViewModel.TheOperation.SAVE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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
//            println("modalBottomSheetValue = $it")
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
            AlarmDateTimePickerBottomSheet(
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
