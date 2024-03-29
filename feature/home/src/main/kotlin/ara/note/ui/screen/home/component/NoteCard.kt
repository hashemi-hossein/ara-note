package ara.note.ui.screen.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ara.note.domain.entity.Note
import ara.note.util.HDateTime

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .padding(
                bottom = 3.dp,
                top = 3.dp,
                start = 3.dp,
                end = 3.dp,
            )
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = note.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDirection = TextDirection.Content,
                ),
                maxLines = 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp),
            )
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp)
                        .padding(top = 5.dp),
                ) {
                    Text(
                        text = HDateTime.gerPrettyDateTime(note.modifiedDateTime),
                        style = MaterialTheme.typography.bodySmall,
                    )
//                    if (note.alarmDateTime != null) {
//                        Icon(
//                            imageVector = Icons.Default.Alarm,
//                            contentDescription = null,
//                            modifier = Modifier.size(20.dp),
//                        )
//                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HPreview() {
    val note = Note(text = "sample text")
    NoteCard(note = note) {
    }
}
