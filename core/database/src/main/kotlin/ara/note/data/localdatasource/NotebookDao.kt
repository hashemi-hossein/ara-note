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

    @Query("SELECT * FROM tblNotebooks ORDER BY id ASC")
    fun observe(): Flow<List<NotebookModel>>

    @MapInfo(keyTable = "tblNotebooks", valueColumn = "count")
    @Query("""
        SELECT tblNotebooks.id, tblNotebooks.name, COUNT(notebook_notes.notebook_id) AS count FROM tblNotebooks 
        LEFT JOIN (SELECT notebook_id FROM tblNotes) AS notebook_notes ON tblNotebooks.id = notebook_notes.notebook_id 
        GROUP BY tblNotebooks.id, tblNotebooks.name
    """
    )
    fun observeWithCount(): Flow<Map<NotebookModel,Int>>

    @Insert
    suspend fun insert(notebookModel: NotebookModel): Long?

    @Delete
    suspend fun delete(notebookModel: NotebookModel): Int?

    @Update
    suspend fun update(notebookModel: NotebookModel): Int?
}
