package com.ara.aranote.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tblNotebooks")
data class NotebookModel(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,
)
