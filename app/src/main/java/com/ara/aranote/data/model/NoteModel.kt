package com.ara.aranote.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "tblNotes")
data class NoteModel(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "notebook_id")
    val notebookId: Int,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "added_datetime")
    val addedDateTime: LocalDateTime,

    @ColumnInfo(name = "alarm_datetime")
    val alarmDateTime: LocalDateTime? = null,
)
