package com.ara.aranote.data.local_data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ara.aranote.data.model.NotebookModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {

    @Insert
    suspend fun insertNotebook(notebookModel: NotebookModel): Long?

    @Query("SELECT * FROM tblNotebooks ORDER BY id ASC")
    fun observeNotebooks(): Flow<List<NotebookModel>>
}
