package ara.note.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Notebook(
    val id: Int,
    val name: String,
    val noteCount: Int = -1,
)
