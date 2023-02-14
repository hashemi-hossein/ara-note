package com.ara.aranote.data.util

import com.ara.aranote.data.model.NoteModel
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.util.Mapper

/**
 * Based on CLEAN Architecture:
 *
 * This class is using for mapping [NoteModel] (Database Model) into [Note] (Domain Entity)
 *
 * [map], [mapList] and their reversed version functions are ready to use
 */
class NoteDomainMapper : Mapper<NoteModel, Note> {

    override fun map(t: NoteModel): Note {
        return Note(
            id = t.id,
            notebookId = t.notebookId,
            text = t.text,
            addedDateTime = t.addedDateTime,
            alarmDateTime = t.alarmDateTime,
        )
    }

    override fun mapReverse(r: Note): NoteModel {
        return NoteModel(
            id = r.id,
            notebookId = r.notebookId,
            text = r.text,
            addedDateTime = r.addedDateTime,
            alarmDateTime = r.alarmDateTime,
        )
    }
}
