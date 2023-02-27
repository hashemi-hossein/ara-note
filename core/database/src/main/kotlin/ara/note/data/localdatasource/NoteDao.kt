package ara.note.data.localdatasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ara.note.data.model.NoteModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM tblNote ORDER BY id DESC")
    fun observe(): Flow<List<NoteModel>>

    @Query("SELECT * FROM tblNote WHERE notebook_id=:notebookId ORDER BY id DESC")
    fun observe(notebookId: Int): Flow<List<NoteModel>>

    @Query("SELECT * FROM tblNote WHERE text LIKE '%' || :searchText || '%' ORDER BY id DESC")
    fun observe(searchText: String): Flow<List<NoteModel>>

    @Insert
    suspend fun insert(noteModel: NoteModel): Long?

    @Delete
    suspend fun delete(noteModel: NoteModel): Int?

    @Update
    suspend fun update(noteModel: NoteModel): Int?

    @Query("SELECT * FROM tblNote WHERE id = :id")
    suspend fun getById(id: Int): NoteModel?

    @Query("SELECT MAX(id) FROM tblNote")
    suspend fun getLastId(): Int?
}
