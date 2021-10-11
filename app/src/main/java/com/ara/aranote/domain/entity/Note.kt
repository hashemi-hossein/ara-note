package com.ara.aranote.domain.entity

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Int,
    val notebookId: Int,
    val text: String,
    val addedDateTime: LocalDateTime,
    val alarmDateTime: LocalDateTime? = null,
)
