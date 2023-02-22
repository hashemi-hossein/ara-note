package ara.note.data.util

import ara.note.data.model.NoteModel
import ara.note.domain.entity.Note
import ara.note.domain.util.Mapper

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
            createdDateTime = t.createdDateTime,
            modifiedDateTime = t.modifiedDateTime,
            alarmDateTime = t.alarmDateTime,
        )
    }

    override fun mapReverse(r: Note): NoteModel {
        return NoteModel(
            id = r.id,
            notebookId = r.notebookId,
            text = r.text,
            createdDateTime = r.createdDateTime,
            modifiedDateTime = r.modifiedDateTime,
            alarmDateTime = r.alarmDateTime,
        )
    }
}
