package ara.note.domain.entity

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
    val alarmDateTime: LocalDateTime? = null,
)
