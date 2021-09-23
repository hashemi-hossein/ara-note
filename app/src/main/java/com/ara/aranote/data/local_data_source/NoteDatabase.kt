package com.ara.aranote.data.local_data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ara.aranote.data.model.NoteModel

@Database(
    entities = [NoteModel::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNoteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "note_database"
    }
}
