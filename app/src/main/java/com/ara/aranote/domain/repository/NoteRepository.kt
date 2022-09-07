package com.ara.aranote.domain.repository

import com.ara.aranote.domain.entity.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun observe(notebookId: Int? = null): Flow<List<Note>>

    suspend fun insert(note: Note): Int

    suspend fun delete(note: Note): Boolean

    suspend fun update(note: Note): Boolean

    suspend fun getById(id: Int): Note?

    suspend fun getLastId(): Int

    suspend fun getAllNotesWithAlarm(): List<Note>
}
