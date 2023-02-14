package com.ara.aranote.data.localdatasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ara.aranote.data.model.NoteModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM tblNotes ORDER BY id DESC")
    fun observe(): Flow<List<NoteModel>>

    @Query("SELECT * FROM tblNotes WHERE notebook_id=:notebookId ORDER BY id DESC")
    fun observe(notebookId: Int): Flow<List<NoteModel>>

    @Insert
    suspend fun insert(noteModel: NoteModel): Long?

    @Delete
    suspend fun delete(noteModel: NoteModel): Int?

    @Update
    suspend fun update(noteModel: NoteModel): Int?

    @Query("SELECT * FROM tblNotes WHERE id = :id")
    suspend fun getById(id: Int): NoteModel?

    @Query("SELECT MAX(id) FROM tblNotes")
    suspend fun getLastId(): Int?

    @Query("SELECT * FROM tblNotes WHERE alarm_datetime IS NOT NULL")
    suspend fun getAllNotesWithAlarm(): List<NoteModel>?
}
