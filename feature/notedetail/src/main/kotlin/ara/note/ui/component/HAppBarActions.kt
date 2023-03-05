package ara.note.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ara.note.domain.entity.Note
import ara.note.notedetail.R.string
import ara.note.ui.screen.notedetail.NoteDetailState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun HAppBarActions(
    uiState: NoteDetailState,
    onNoteChanged: (Note) -> Unit,
    onDiscard: () -> Unit,
    onDelete: () -> Unit,
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
) {
    IconButton(onClick = {
        keyboardController?.hide()
        if (!uiState.isNewNote) onDelete() else onDiscard()
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
//    val doesHasAlarm = uiState.note.alarmDateTime != null
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
//    AnimatedVisibility(doesHasAlarm) {
//        IconButton(onClick = {
//            hManageAlarm(
//                context = context,
//                doesCreate = false,
//                noteId = uiState.note.id,
//            )
//            onNoteChanged(uiState.note.copy(alarmDateTime = null))
//        }) {
//            Icon(
//                imageVector = Icons.Default.AlarmOff,
//                contentDescription = stringResource(string.cd_delete_alarm),
//            )
//        }
//    }

    if (uiState.notebooks.isNotEmpty()) {
        HDropdown(
            items = uiState.notebooks.associate { it.id to it.name },
            selectedKey = uiState.note.notebookId,
            onItemClick = { onNoteChanged(uiState.note.copy(notebookId = it)) },
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
private fun HPreview() {
    HAppBarActions(
        uiState = NoteDetailState(),
        onNoteChanged = {},
        onDiscard = {},
        onDelete = {},
    )
}
