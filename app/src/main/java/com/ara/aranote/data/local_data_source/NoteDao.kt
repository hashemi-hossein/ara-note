package com.ara.aranote.data.local_data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ara.aranote.data.model.NoteModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert
    suspend fun insertNote(noteModel: NoteModel): Long?

    @Query("SELECT * FROM tblNotes ORDER BY id DESC")
    fun observeNotes(): Flow<List<NoteModel>>

    @Query("SELECT * FROM tblNotes WHERE notebook_id=:notebookId ORDER BY id DESC")
    fun observeNotes(notebookId: Int): Flow<List<NoteModel>>

    @Query("SELECT * FROM tblNotes WHERE id = :id")
    suspend fun getNote(id: Int): NoteModel?

    @Update
    suspend fun updateNote(noteModel: NoteModel): Int?

    @Delete
    suspend fun deleteNote(noteModel: NoteModel): Int?

    @Query("SELECT MAX(id) FROM tblNotes")
    suspend fun getLastId(): Int?

    @Query("SELECT * FROM tblNotes WHERE alarm_datetime IS NOT NULL")
    suspend fun getAllNotesWithAlarm(): List<NoteModel>?
}
