package com.ara.aranote.test_util

import com.ara.aranote.data.model.NoteModel
import com.ara.aranote.domain.entity.Note
import kotlinx.datetime.LocalDateTime

object TestUtil {

    val tDateTime = LocalDateTime.parse("2021-01-01T00:00")
    val tEntity = Note(
        id = 1,
        text = "test",
        addedDateTime = tDateTime,
        alarmDateTime = tDateTime,
    )
    val tModel = NoteModel(
        id = 1,
        text = "test",
        addedDateTime = tDateTime,
        alarmDateTime = tDateTime,
    )
}
