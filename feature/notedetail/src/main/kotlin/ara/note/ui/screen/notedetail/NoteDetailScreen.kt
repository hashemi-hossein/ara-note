package ara.note.ui.screen.notedetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import ara.note.domain.entity.Note
import ara.note.notedetail.R.string
import ara.note.ui.component.HAppBar
import ara.note.ui.component.HAppBarActions
import ara.note.ui.component.HBody
import ara.note.ui.component.HSnackbarHost
import ara.note.ui.component.showSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    navigateUp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    NoteDetailScreen(
        uiState = uiState,
        singleEvent = viewModel.singleEvent,
        navigateUp = navigateUp,
        saveNote = { viewModel.sendIntent(NoteDetailIntent.CreateOrUpdateNote) },
        deleteNote = { viewModel.sendIntent(NoteDetailIntent.DeleteNote) },
        onNoteChanged = { viewModel.sendIntent(NoteDetailIntent.ModifyNote(it)) },
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
    singleEvent: SharedFlow<NoteDetailSingleEvent>,
    navigateUp: () -> Unit,
    saveNote: () -> Unit,
    deleteNote: () -> Unit,
    onNoteChanged: (Note) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    modalBottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
) {
    val context = LocalContext.current

    val onDiscard = {
        showSnackbar(
            scope = scope,
            snackbarHostState = snackbarHostState,
            actionLabel = context.getString(string.discard),
        ) {
            navigateUp()
        }
    }
    val onDelete = {
        showSnackbar(
            scope = scope,
            snackbarHostState = snackbarHostState,
            actionLabel = context.getString(string.delete),
        ) {
            deleteNote()
        }
    }

    LaunchedEffect(Unit) {
        singleEvent.collect {
            when (it) {
                is NoteDetailSingleEvent.NavigateUp -> navigateUp()

//                is NoteDetailSingleEvent.DisableAlarm -> Unit
//                    hManageAlarm(context = context, doesCreate = false, noteId = it.noteId)

                is NoteDetailSingleEvent.OperationError ->
                    showSnackbar(
                        scope = scope,
                        snackbarHostState = snackbarHostState,
                        message = context.getString(string.error_in_operation),
                        actionLabel = context.getString(string.exit)
                    ) {
                        navigateUp()
                    }
            }
        }
    }

    BackHandler(onBack = {
        if (modalBottomSheetState.isVisible) {
            scope.launch { modalBottomSheetState.hide() }
        } else {
            if (uiState.userPreferences.isAutoSaveMode) saveNote() else onDiscard()
        }
    })
//    ModalBottomSheetLayout(
//        sheetState = modalBottomSheetState,
//        sheetContent = {
//            AlarmDateTimePickerBottomSheet(
//                uiState = uiState,
//                onNoteChanged = onNoteChanged,
//                snackbarHostState = snackbarHostState,
//                modalBottomSheetState = modalBottomSheetState,
//            )
//        },
//    ) {
    Scaffold(
        snackbarHost = { HSnackbarHost(hostState = snackbarHostState) },
        topBar = {
            HAppBar(
                icon = if (uiState.userPreferences.isAutoSaveMode) Icons.Default.Done else Icons.Default.ArrowBack,
                actions = {
                    HAppBarActions(
                        uiState = uiState,
                        onNoteChanged = onNoteChanged,
                        onDiscard = onDiscard,
                        onDelete = onDelete,
                    )
                },
                onNavButtonClick = {
                    keyboardController?.hide()
                    if (uiState.userPreferences.isAutoSaveMode) saveNote() else onDiscard()
                },
            )
        },
        floatingActionButton = {
            if (!uiState.userPreferences.isAutoSaveMode) {
                FloatingActionButton(onClick = saveNote) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(string.cd_save),
                    )
                }
            }
        },
    ) { innerPadding ->
        HBody(
            modifier = Modifier.padding(innerPadding),
            uiState = uiState,
            onNoteChanged = onNoteChanged,
        )
    }
//    }
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
