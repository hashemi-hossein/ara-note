package ara.note.domain.entity

import ara.note.data.model.NoteModel
import ara.note.util.DEFAULT_NOTEBOOK_ID
import ara.note.util.HDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Int = 0,
    val notebookId: Int = DEFAULT_NOTEBOOK_ID,
    val text: String = "",
    val createdDateTime: LocalDateTime = HDateTime.getCurrentDateTime(),
    val modifiedDateTime: LocalDateTime = HDateTime.getCurrentDateTime(),
)

/**
 * Based on CLEAN Architecture:
 *
 * Extension function for mapping [Note] (Domain Entity) to [NoteModel] (Database Model)
 */
fun Note.toDataModel() = NoteModel(
    id = this.id,
    notebookId = this.notebookId,
    text = this.text,
    createdDateTime = this.createdDateTime,
    modifiedDateTime = this.modifiedDateTime,
)
