package ara.note.data.localdatasource

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import ara.note.data.model.NoteModel
import ara.note.data.model.NotebookModel

@Database(
    entities = [NoteModel::class, NotebookModel::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = FirstAutoMigration::class)
    ]
)
@TypeConverters(DatabaseTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getNoteDao(): NoteDao

    abstract fun getNotebookDao(): NotebookDao

    companion object {
        const val DATABASE_NAME = "app_database.sqlite"
    }
}

@RenameColumn(
    tableName = "tblNote",
    fromColumnName = "added_datetime",
    toColumnName = "created_datetime"
)
class FirstAutoMigration : AutoMigrationSpec
