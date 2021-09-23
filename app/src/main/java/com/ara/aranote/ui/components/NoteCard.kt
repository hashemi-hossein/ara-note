package com.ara.aranote.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.ui.theme.DeepOrange500
import com.ara.aranote.util.HDateTime

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
) {
    Card(
//        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(
                bottom = 3.dp,
                top = 3.dp,
                start = 3.dp,
                end = 3.dp
            )
            .height(100.dp)
//            .fillMaxHeight(0.2f)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 4.dp,
    ) {
        Surface(
            contentColor = Color.White,
            color = DeepOrange500,
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = note.text,
                    modifier = Modifier.padding(7.dp),
                    style = MaterialTheme.typography.body1,
                    maxLines = 3,
                )
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.disabled,
                    LocalContentColor provides Color.White
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(3.dp),
                    ) {
                        Text(
                            text = HDateTime.gerPrettyDateTime(note.addedDateTime),
                            style = MaterialTheme.typography.caption,
                        )
                        if (note.alarmDateTime != null)
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                    }
                }
            }
        }
    }
}
