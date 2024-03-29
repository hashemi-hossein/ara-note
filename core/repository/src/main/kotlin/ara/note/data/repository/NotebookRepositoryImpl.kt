package ara.note.data.repository

import ara.note.data.localdatasource.NotebookDao
import ara.note.data.model.NotebookModel
import ara.note.data.model.toDomainEntity
import ara.note.domain.entity.Notebook
import ara.note.domain.entity.toDataModel
import ara.note.domain.repository.NotebookRepository
import ara.note.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Based on SINGLE-SOURCE-OF-TRUTH strategy:
 *
 * Every operations on tblNoteBook such as CRUD or observation on the table's data
 * will be done via this repository
 *
 * Every function notify the caller about the result of the operation
 * by returning true or positive value in case of successful operation
 * and false or negative value in case of error
 */
class NotebookRepositoryImpl(
    private val notebookDao: NotebookDao,
) : NotebookRepository {

    override fun observe(): Flow<List<Notebook>> {
        return notebookDao.observeWithCount().map { notebooksWithCount: Map<NotebookModel, Int> ->
            notebooksWithCount.map {
                it.key.toDomainEntity(it.value)
            }
        }
    }

    override suspend fun insert(notebook: Notebook): Result<Int> {
        val result = notebookDao.insert(notebook.toDataModel())
        return if (result != null) Result.Success(result.toInt()) else Result.Error()
    }

    override suspend fun delete(notebook: Notebook): Result<Boolean> {
        val result = notebookDao.delete(notebook.toDataModel())
        return if (result != null) Result.Success(result == 1) else Result.Error()
    }

    override suspend fun update(notebook: Notebook): Result<Boolean> {
        val result = notebookDao.update(notebook.toDataModel())
        return if (result != null) Result.Success(result == 1) else Result.Error()
    }
}
