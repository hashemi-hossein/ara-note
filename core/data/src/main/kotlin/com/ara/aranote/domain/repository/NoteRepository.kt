package com.ara.aranote.domain.repository

import ara.note.domain.entity.Note
import ara.note.util.Result
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun observe(notebookId: Int? = null, searchText: String? = null): Flow<List<Note>>

    suspend fun insert(note: Note): Result<Int>

    suspend fun delete(note: Note): Result<Boolean>

    suspend fun update(note: Note): Result<Boolean>

    suspend fun getById(id: Int): Result<Note>

    suspend fun getLastId(): Result<Int>

    suspend fun getAllNotesWithAlarm(): Result<List<Note>>
}
