package ara.note.domain.entity

import ara.note.data.model.NotebookModel
import kotlinx.serialization.Serializable

@Serializable
data class Notebook(
    val id: Int = 0,
    val name: String = "",
    val noteCount: Int = -1,
)

/**
 * Based on CLEAN Architecture:
 *
 * Extension function for mapping [Notebook] (Domain Entity) to [NotebookModel] (Database Model)
 */
fun Notebook.toDataModel() = NotebookModel(
    id = this.id,
    name = this.name,
)
