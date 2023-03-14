package ara.note.data.model

import androidx.annotation.Keep
import ara.note.data.model.DarkMode.SYSTEM
import ara.note.data.model.NoteViewMode.GRID
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UserPreferences(
    val darkMode: DarkMode = SYSTEM,
    val noteViewMode: NoteViewMode = GRID,
    val isAutoSaveMode: Boolean = true,
    val isDoubleBackToExitMode: Boolean = false,
)

enum class NoteViewMode { LIST, GRID }
enum class DarkMode { LIGHT, DARK, SYSTEM }
