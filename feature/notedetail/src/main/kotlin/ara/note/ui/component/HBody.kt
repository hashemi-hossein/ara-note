package ara.note.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import ara.note.domain.entity.Note
import ara.note.notedetail.R.string
import ara.note.ui.screen.notedetail.NoteDetailState
import ara.note.util.DateTimeFormatPattern.DATE_TIME
import ara.note.util.HDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HBody(
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
                        dateTimeFormatPattern = DATE_TIME,
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
