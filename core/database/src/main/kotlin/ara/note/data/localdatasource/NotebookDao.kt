package ara.note.data.localdatasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ara.note.data.model.NotebookModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {

    @Query("SELECT * FROM tblNotebooks ORDER BY id ASC")
    fun observe(): Flow<List<NotebookModel>>

    @Insert
    suspend fun insert(notebookModel: NotebookModel): Long?

    @Delete
    suspend fun delete(notebookModel: NotebookModel): Int?

    @Update
    suspend fun update(notebookModel: NotebookModel): Int?
}
