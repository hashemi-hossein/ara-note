package ara.note.ui.component

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import ara.note.alarm.hManageAlarm
import ara.note.domain.entity.Note
import ara.note.notedetail.R.string
import ara.note.ui.screen.notedetail.NoteDetailState
import ara.note.ui.screen.notedetail.NoteDetailViewModel.TheOperation
import ara.note.ui.screen.notedetail.NoteDetailViewModel.TheOperation.DELETE
import ara.note.ui.screen.notedetail.NoteDetailViewModel.TheOperation.DISCARD
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun HAppBarActions(
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
        onBackPressed(if (!uiState.isNewNote) DELETE else DISCARD)
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
