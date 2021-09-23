package com.ara.aranote.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ara.aranote.R
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.viewmodels.HomeViewModel
import com.ara.aranote.ui.components.HAppBar
import com.ara.aranote.ui.components.NoteCard
import com.ara.aranote.util.HDateTime
import com.ara.aranote.util.INVALID_NOTE_ID
import com.ara.aranote.util.minus
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToNoteDetailScreen: (Int) -> Unit,
) {
    val notes: List<Note> by viewModel.notes.collectAsState()

    HomeScreen(
        notes = notes,
        navigateToNoteDetailScreen = navigateToNoteDetailScreen
    )
}

@Composable
internal fun HomeScreen(
    notes: List<Note>,
    navigateToNoteDetailScreen: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            HAppBar {
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navigateToNoteDetailScreen(INVALID_NOTE_ID) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add_note),
                )
            }
        },
    ) { innerPadding ->
        HBody(
            innerPadding = innerPadding,
            notes = notes,
            navigateToNoteDetailScreen = navigateToNoteDetailScreen,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HBody(
    innerPadding: PaddingValues,
    notes: List<Note>,
    navigateToNoteDetailScreen: (Int) -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(innerPadding)
    ) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(count = 2),
            modifier = Modifier
        ) {
            itemsIndexed(items = notes) { index: Int, item: Note ->
                NoteCard(note = item) {
                    navigateToNoteDetailScreen(item.id)
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview(
//    showBackground = true,
//    backgroundColor = 0xff2ff2f2,
//    widthDp = 200,
//    heightDp = 300,
//    showSystemUi = true,
)
@Composable
private fun HPreview() {
    val lst = mutableListOf<Note>()
    val currentDateTime = HDateTime.getCurrentDateTime()
    for (i in 1..10) {
        lst.add(
            Note(
                id = i,
                text = "item $i",
                addedDateTime = currentDateTime.minus(Duration.seconds(i * i * i * i * i)),
                alarmDateTime = if (i % 3 == 1) currentDateTime else null,
            )
        )
    }
    HomeScreen(
        notes = lst,
        navigateToNoteDetailScreen = {},
    )
}
