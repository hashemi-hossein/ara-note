package com.ara.aranote.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.AlertDialog
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        navigateToNoteDetailScreen = navigateToNoteDetailScreen,
        addNotebook = viewModel::addNotebook,
    )
}

@Composable
internal fun HomeScreen(
    notes: List<Note>,
    notebooks: List<Notebook>,
    navigateToNoteDetailScreen: (Int) -> Unit,
    addNotebook: (String) -> Unit = {},
) {
    var isDialogVisible by remember { mutableStateOf(false) }
    val setDialogVisibility: (Boolean) -> Unit = { isDialogVisible = it }

    Scaffold(
        topBar = {
            HAppBar {
            }
        },
        drawerContent = {
            HDrawer(
                notebooks = notebooks,
                setDialogVisibility = setDialogVisibility,
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
        HDialog(
            isDialogVisible = isDialogVisible,
            setDialogVisibility = setDialogVisibility,
            addNotebook = addNotebook,
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
    setDialogVisibility: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.notebooks),
                style = MaterialTheme.typography.h6
            )
            IconButton(onClick = { setDialogVisibility(true) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
        LazyColumn() {
            items(notebooks) { item: Notebook ->
                TextButton(
                    onClick = { /*TODO*/ },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = item.name, style = MaterialTheme.typography.body1)
                    }
                }
            }
        }
    }
}

@Composable
fun HDialog(
    isDialogVisible: Boolean,
    setDialogVisibility: (Boolean) -> Unit,
    addNotebook: (String) -> Unit,
) {
    if (isDialogVisible) {
        var text by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { setDialogVisibility(false) },
            confirmButton = {
                IconButton(onClick = {
                    setDialogVisibility(false)
                    addNotebook(text)
                }) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = null)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Add Notebook",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold,
                    )
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        singleLine = true,
                        label = { Text("name") },
                    )
                }
            },
        )
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
