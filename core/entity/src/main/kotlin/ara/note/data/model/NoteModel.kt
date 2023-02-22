package ara.note.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "tblNote",
    foreignKeys = [
        ForeignKey(
            entity = NotebookModel::class, parentColumns = ["id"], childColumns = ["notebook_id"],
            onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.RESTRICT,
        )
    ],
)
data class NoteModel(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "notebook_id")
    val notebookId: Int,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "created_datetime")
    val createdDateTime: LocalDateTime,

    @ColumnInfo(name = "modified_datetime")
    val modifiedDateTime: LocalDateTime,

    @ColumnInfo(name = "alarm_datetime")
    val alarmDateTime: LocalDateTime? = null,
)
