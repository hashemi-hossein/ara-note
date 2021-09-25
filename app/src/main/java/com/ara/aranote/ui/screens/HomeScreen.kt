package com.ara.aranote.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import com.ara.aranote.domain.entity.Notebook
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
    val notebooks: List<Notebook> by viewModel.notebooks.collectAsState()

    HomeScreen(
        notes = notes,
        notebooks = notebooks,
        navigateToNoteDetailScreen = navigateToNoteDetailScreen
    )
}

@Composable
internal fun HomeScreen(
    notes: List<Note>,
    notebooks: List<Notebook>,
    navigateToNoteDetailScreen: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            HAppBar {
            }
        },
        drawerContent = {
            HDrawer(
                notebooks = notebooks
            )
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

@Composable
private fun HDrawer(
    notebooks: List<Notebook>,
) {
    Column(modifier = Modifier) {
        Text(text = stringResource(R.string.notebooks))
        LazyColumn() {
            itemsIndexed(notebooks) { index: Int, item: Notebook ->
                Text(text = item.name)
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
    val lstNotes = mutableListOf<Note>()
    val currentDateTime = HDateTime.getCurrentDateTime()
    for (i in 1..10) {
        lstNotes.add(
            Note(
                id = i,
                text = "item $i",
                addedDateTime = currentDateTime.minus(Duration.seconds(i * i * i * i * i)),
                alarmDateTime = if (i % 3 == 1) currentDateTime else null,
            )
        )
    }
    val lstNotebooks = mutableListOf<Notebook>()
    for (i in 1..3) {
        lstNotebooks.add(
            Notebook(id = i, name = "notebook$i")
        )
    }
    HomeScreen(
        notes = lstNotes,
        notebooks = lstNotebooks,
        navigateToNoteDetailScreen = {},
    )
}
