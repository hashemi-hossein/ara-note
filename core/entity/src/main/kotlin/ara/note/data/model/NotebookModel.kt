package ara.note.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ara.note.domain.entity.Notebook

@Entity(tableName = "tblNotebook")
data class NotebookModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,
)

/**
 * Based on CLEAN Architecture:
 *
 * Extension function for mapping [NotebookModel] (Database Model) to [Notebook] (Domain Entity)
 */
fun NotebookModel.toDomainEntity(noteCount: Int) = Notebook(
    id = this.id,
    name = this.name,
    noteCount = noteCount,
)