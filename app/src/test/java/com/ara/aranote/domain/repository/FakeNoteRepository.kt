package com.ara.aranote.domain.repository

import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.util.INVALID_NOTE_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class FakeNoteRepository : NoteRepository {

    private val notes = mutableMapOf<Int, Note>()
    private val notesFlow = MutableStateFlow(notes.values.toList())

    private val notebooks = mutableMapOf<Int, Notebook>()
    private val notebooksFlow = MutableStateFlow(notebooks.values.toList())

    override suspend fun insertNote(note: Note): Int {
        val r = if (notes.put(note.id, note) == null) note.id else INVALID_NOTE_ID
        notesFlow.update { notes.values.toList() }
        return r
    }

    override fun observeNotes(): Flow<List<Note>> {
        return flow {
            notesFlow.collect { emit(it) }
        }
    }

    override suspend fun getNote(id: Int): Note? {
        return notes[id]
    }

    override suspend fun updateNote(note: Note): Boolean {
        val r = notes.put(note.id, note) == null
        notesFlow.update { notes.values.toList() }
        return r
    }

    override suspend fun deleteNote(note: Note): Boolean {
        val r = notes.remove(note.id) != null
        notesFlow.update { notes.values.toList() }
        return r
    }

    override suspend fun getLastId(): Int {
        return if (notes.keys.isEmpty()) 0 else notes.keys.last()
    }

    override suspend fun getAllNotesWithAlarm(): List<Note> {
        return notes.values.filter { it.alarmDateTime != null }
    }

    override fun observeNotebooks(): Flow<List<Notebook>> {
        return flow {
            notebooksFlow.collect { emit(it) }
        }
    }
}
