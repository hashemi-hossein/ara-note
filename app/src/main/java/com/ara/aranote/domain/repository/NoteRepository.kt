package com.ara.aranote.domain.repository

import com.ara.aranote.domain.entity.Note
import com.ara.aranote.util.Result
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun observe(notebookId: Int? = null): Flow<List<Note>>

    suspend fun insert(note: Note): Result<Int>

    suspend fun delete(note: Note): Result<Boolean>

    suspend fun update(note: Note): Result<Boolean>

    suspend fun getById(id: Int): Result<Note>

    suspend fun getLastId(): Result<Int>

    suspend fun getAllNotesWithAlarm(): Result<List<Note>>
}
