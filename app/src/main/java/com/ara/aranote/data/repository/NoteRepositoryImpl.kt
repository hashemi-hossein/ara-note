package com.ara.aranote.data.repository

import com.ara.aranote.data.local_data_source.NoteDao
import com.ara.aranote.data.model.NoteModel
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.domain.util.Mapper
import com.ara.aranote.util.INVALID_NOTE_ID
import com.ara.aranote.util.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Based on SINGLE-SOURCE-OF-TRUTH strategy:
 *
 * Every operations on tblNote such as CRUD or observation on the table's data
 * will be done via this repository
 *
 * Every function notify the caller about the result of the operation
 * by returning true or positive value in case of successful operation
 * and false or negative value in case of error
 */
class NoteRepositoryImpl(
    private val noteDao: NoteDao,
    private val noteDomainMapper: Mapper<NoteModel, Note>,
) : NoteRepository {

    override suspend fun insertNote(note: Note): Int {
        val result = noteDao.insertNote(noteDomainMapper.mapReverse(note))
        Timber.tag(TAG).d("insert note result = $result")
        return result?.toInt() ?: INVALID_NOTE_ID
    }

    override fun observeNotes(notebookId: Int?): Flow<List<Note>> {
        return (if (notebookId == null) noteDao.observeNotes() else noteDao.observeNotes(notebookId))
            .map {
                noteDomainMapper.mapList(it).sortedByDescending { item -> item.addedDateTime }
            }
    }

    override suspend fun getNote(id: Int): Note? {
        return noteDao.getNote(id)?.let {
            noteDomainMapper.map(it)
        }
    }

    override suspend fun updateNote(note: Note): Boolean {
        val result = noteDao.updateNote(noteDomainMapper.mapReverse(note))
        Timber.tag(TAG).d("update note result = $result")
        return result == 1
    }

    override suspend fun deleteNote(note: Note): Boolean {
        val result = noteDao.deleteNote(noteDomainMapper.mapReverse(note))
        Timber.tag(TAG).d("delete note result = $result")
        return result == 1
    }

    override suspend fun getLastId(): Int {
        return noteDao.getLastId() ?: 0
    }

    override suspend fun getAllNotesWithAlarm(): List<Note> {
        return noteDao.getAllNotesWithAlarm()?.let {
            noteDomainMapper.mapList(it)
        } ?: listOf()
    }
}
