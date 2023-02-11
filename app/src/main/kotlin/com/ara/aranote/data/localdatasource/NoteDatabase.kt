package com.ara.aranote.data.localdatasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ara.aranote.data.model.NoteModel
import com.ara.aranote.data.model.NotebookModel

@Database(
    entities = [NoteModel::class, NotebookModel::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(DatabaseTypeConverters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNoteDao(): NoteDao

    abstract fun getNotebookDao(): NotebookDao

    companion object {
        const val DATABASE_NAME = "note_database"
    }
}
