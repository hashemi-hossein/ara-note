package ara.note.data.localdatasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.Query
import androidx.room.Update
import ara.note.data.model.NotebookModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {

    @Query("SELECT * FROM tblNotebook ORDER BY id ASC")
    fun observe(): Flow<List<NotebookModel>>

    @MapInfo(keyTable = "tblNotebook", valueColumn = "count")
    @Query(
        """
        SELECT tblNotebook.id, tblNotebook.name, COUNT(notebook_notes.notebook_id) AS count FROM tblNotebook 
        LEFT JOIN (SELECT notebook_id FROM tblNote) AS notebook_notes ON tblNotebook.id = notebook_notes.notebook_id 
        GROUP BY tblNotebook.id, tblNotebook.name
    """,
    )
    fun observeWithCount(): Flow<Map<NotebookModel, Int>>

    @Insert
    suspend fun insert(notebookModel: NotebookModel): Long?

    @Delete
    suspend fun delete(notebookModel: NotebookModel): Int?

    @Update
    suspend fun update(notebookModel: NotebookModel): Int?
}
