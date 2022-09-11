package com.ara.aranote.domain.entity

import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import com.ara.aranote.util.HDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Int = 0,
    val notebookId: Int = DEFAULT_NOTEBOOK_ID,
    val text: String = "",
    val addedDateTime: LocalDateTime = HDateTime.getCurrentDateTime(),
    val alarmDateTime: LocalDateTime? = null,
)
