package com.ara.aranote.domain.repository

import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    suspend fun insertNote(note: Note): Int

    fun observeNotes(notebookId: Int? = null): Flow<List<Note>>

    suspend fun getNote(id: Int): Note?

    suspend fun updateNote(note: Note): Boolean

    suspend fun deleteNote(note: Note): Boolean

    suspend fun getLastId(): Int

    suspend fun getAllNotesWithAlarm(): List<Note>

    fun observeNotebooks(): Flow<List<Notebook>>

    suspend fun insertNotebook(notebook: Notebook): Int
}
