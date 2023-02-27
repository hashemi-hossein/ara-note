package ara.note.data.repository

import ara.note.data.localdatasource.NoteDao
import ara.note.data.model.toDomainEntity
import ara.note.domain.entity.Note
import ara.note.domain.entity.toDataModel
import ara.note.domain.repository.NoteRepository
import ara.note.util.Result
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
) : NoteRepository {

    override fun observe(notebookId: Int?, searchText: String?): Flow<List<Note>> {
        return when {
            searchText != null -> noteDao.observe(searchText)
            notebookId != null -> noteDao.observe(notebookId)
            else -> noteDao.observe()
        }.map {
            it.toDomainEntity().sortedByDescending { item -> item.modifiedDateTime }
        }
    }

    override suspend fun insert(note: Note): Result<Int> {
        val result = noteDao.insert(note.toDataModel())
        return if (result != null) Result.Success(result.toInt()) else Result.Error()
    }

    override suspend fun delete(note: Note): Result<Boolean> {
        val result = noteDao.delete(note.toDataModel())
        return if (result != null) Result.Success(result == 1) else Result.Error()
    }

    override suspend fun update(note: Note): Result<Boolean> {
        val result = noteDao.update(note.toDataModel())
        return if (result != null) Result.Success(result == 1) else Result.Error()
    }

    override suspend fun getById(id: Int): Result<Note> {
        return noteDao.getById(id).let {
            if (it != null) {
                Result.Success(it.toDomainEntity())
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
}
