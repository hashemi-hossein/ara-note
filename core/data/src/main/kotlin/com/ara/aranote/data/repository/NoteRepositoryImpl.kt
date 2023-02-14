package com.ara.aranote.data.repository

import com.ara.aranote.data.localdatasource.NoteDao
import com.ara.aranote.data.model.NoteModel
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.domain.util.Mapper
import com.ara.aranote.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    override fun observe(notebookId: Int?): Flow<List<Note>> {
        return (if (notebookId == null) noteDao.observe() else noteDao.observe(notebookId))
            .map {
                noteDomainMapper.mapList(it).sortedByDescending { item -> item.addedDateTime }
            }
    }

    override suspend fun insert(note: Note): Result<Int> {
        val result = noteDao.insert(noteDomainMapper.mapReverse(note))
        return if (result != null) Result.Success(result.toInt()) else Result.Error()
    }

    override suspend fun delete(note: Note): Result<Boolean> {
        val result = noteDao.delete(noteDomainMapper.mapReverse(note))
        return if (result != null) Result.Success(result == 1) else Result.Error()
    }

    override suspend fun update(note: Note): Result<Boolean> {
        val result = noteDao.update(noteDomainMapper.mapReverse(note))
        return if (result != null) Result.Success(result == 1) else Result.Error()
    }

    override suspend fun getById(id: Int): Result<Note> {
        return noteDao.getById(id).let {
            if (it != null) {
                Result.Success(noteDomainMapper.map(it))
            } else {
                Result.Error()
            }
        }
    }

    override suspend fun getLastId(): Result<Int> {
        return noteDao.getLastId().let {
            if (it != null) {
                Result.Success(it)
            } else {
                Result.Error()
            }
        }
    }

    override suspend fun getAllNotesWithAlarm(): Result<List<Note>> {
        return noteDao.getAllNotesWithAlarm().let {
            if (it != null) {
                Result.Success(noteDomainMapper.mapList(it))
            } else {
                Result.Error()
            }
        }
    }
}
