package ara.note.ui.navigation

import ara.note.util.NAV_ARGUMENT_NOTEBOOK_ID
import ara.note.util.NAV_ARGUMENT_NOTE_ID

sealed class NavScreen(val route: String) {

    object Home : NavScreen("Home")
    data class NoteDetail(val noteId: Int? = null, val notebookId: Int? = null) :
        NavScreen(
            "NoteDetail" +
                "?$NAV_ARGUMENT_NOTE_ID=" +
                (noteId?.toString() ?: "{$NAV_ARGUMENT_NOTE_ID}") +
                "&$NAV_ARGUMENT_NOTEBOOK_ID=" +
                (notebookId?.toString() ?: "{$NAV_ARGUMENT_NOTEBOOK_ID}"),
        )

    object NotebooksList : NavScreen("NotebooksList")
    object Settings : NavScreen("Settings")
}
