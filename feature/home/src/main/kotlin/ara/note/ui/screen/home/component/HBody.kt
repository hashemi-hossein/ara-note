package ara.note.ui.screen.home.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells.Fixed
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ara.note.data.datastore.NoteViewMode.GRID
import ara.note.data.datastore.NoteViewMode.LIST
import ara.note.domain.entity.Note
import ara.note.ui.screen.home.HomeState

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HBody(
    modifier: Modifier = Modifier,
    uiState: HomeState,
    navigateToNoteDetailScreen: (Int) -> Unit,
    listState: LazyListState,
) {
    Surface(modifier = modifier) {
        val noteCard: @Composable (Note) -> Unit = {
            NoteCard(note = it) {
                navigateToNoteDetailScreen(it.id)
            }
        }
        when (uiState.userPreferences.noteViewMode) {
            LIST -> {
                LazyColumn(state = listState) {
                    items(uiState.notes) { noteCard(it) }
                }
            }
            GRID -> {
                LazyVerticalStaggeredGrid(columns = Fixed(2)) {
                    items(uiState.notes) { noteCard(it) }
                }
            }
        }
    }
}
