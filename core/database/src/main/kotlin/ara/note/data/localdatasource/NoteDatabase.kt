package ara.note.data.localdatasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ara.note.data.model.NoteModel
import ara.note.data.model.NotebookModel

@Database(
    entities = [NoteModel::class, NotebookModel::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DatabaseTypeConverters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNoteDao(): NoteDao

    abstract fun getNotebookDao(): NotebookDao

    companion object {
        const val DATABASE_NAME = "note_database"
    }
}
