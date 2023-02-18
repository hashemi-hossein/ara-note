package com.ara.aranote.domain.repository

import ara.note.domain.entity.Note
import ara.note.domain.repository.NoteRepository
import ara.note.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class FakeNoteRepository : NoteRepository {

    private val notes = mutableMapOf<Int, Note>()
    private val notesFlow = MutableStateFlow(notes.values.toList())

    override fun observe(notebookId: Int?): Flow<List<Note>> {
        return flow { notesFlow.collect { emit(it.filter { it.notebookId == notebookId }) } }
    }

    override suspend fun insert(note: Note): Result<Int> {
        val r = if (notes.put(note.id, note) == null) note.id else null
        return if (r != null) {
            notesFlow.update { notes.values.toList() }
            Result.Success(r)
        } else {
            Result.Error()
        }
    }

    override suspend fun delete(note: Note): Result<Boolean> {
        val r = notes.remove(note.id) != null
        return if (r) {
            notesFlow.update { notes.values.toList() }
            Result.Success(r)
        } else {
            Result.Error()
        }
    }

    override suspend fun update(note: Note): Result<Boolean> {
        val r = notes.put(note.id, note) != null
        return if (r) {
            notesFlow.update { notes.values.toList() }
            Result.Success(r)
        } else {
            Result.Error()
        }
    }

    override suspend fun getById(id: Int): Result<Note> {
        return notes[id].let { if (it != null) Result.Success(it) else Result.Error() }
    }

    override suspend fun getLastId(): Result<Int> {
        return if (notes.keys.isEmpty()) {
            Result.Error()
        } else {
            Result.Success(notes.keys.last())
        }
    }

    override suspend fun getAllNotesWithAlarm(): Result<List<Note>> {
        return Result.Success(notes.values.filter { it.alarmDateTime != null })
    }
}
