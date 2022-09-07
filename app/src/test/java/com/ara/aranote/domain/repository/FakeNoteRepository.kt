package com.ara.aranote.domain.repository

import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.util.INVALID_NOTEBOOK_ID
import com.ara.aranote.util.INVALID_NOTE_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class FakeNoteRepository : NoteRepository {

    private val notes = mutableMapOf<Int, Note>()
    private val notesFlow = MutableStateFlow(notes.values.toList())

    private val notebooks = mutableMapOf<Int, Notebook>()
    private val notebooksFlow = MutableStateFlow(notebooks.values.toList())

    override fun observe(notebookId: Int?): Flow<List<Note>> {
        return flow {
            notesFlow.collect { emit(it.filter { it.notebookId == notebookId }) }
        }
    }

    override suspend fun insert(note: Note): Int {
        val r = if (notes.put(note.id, note) == null) note.id else INVALID_NOTE_ID
        notesFlow.update { notes.values.toList() }
        return r
    }

    override suspend fun delete(note: Note): Boolean {
        val r = notes.remove(note.id) != null
        notesFlow.update { notes.values.toList() }
        return r
    }

    override suspend fun update(note: Note): Boolean {
        val r = notes.put(note.id, note) == null
        notesFlow.update { notes.values.toList() }
        return r
    }

    override suspend fun getById(id: Int): Note? {
        return notes[id]
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

    override suspend fun insertNotebook(notebook: Notebook): Int {
        val r =
            if (notebooks.put(notebook.id, notebook) == null) notebook.id else INVALID_NOTEBOOK_ID
        notebooksFlow.update { notebooks.values.toList() }
        return r
    }

    override suspend fun deleteNotebook(notebook: Notebook): Boolean {
        val r = notebooks.remove(notebook.id) != null
        notebooksFlow.update { notebooks.values.toList() }
        return r
    }

    override suspend fun updateNotebook(notebook: Notebook): Boolean {
        val r = notebooks.put(notebook.id, notebook) == null
        notebooksFlow.update { notebooks.values.toList() }
        return r
    }
}
