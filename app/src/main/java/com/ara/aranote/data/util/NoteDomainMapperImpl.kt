package com.ara.aranote.data.util

import com.ara.aranote.data.model.NoteModel
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.util.DomainMapper

class NoteDomainMapperImpl : DomainMapper<NoteModel, Note> {

    override fun mapToDomainEntity(model: NoteModel): Note {
        return Note(
            id = model.id,
            text = model.text,
            addedDateTime = model.addedDateTime,
            alarmDateTime = model.alarmDateTime,
        )
    }

    override fun mapFromDomainEntity(domainEntity: Note): NoteModel {
        return NoteModel(
            id = domainEntity.id,
            text = domainEntity.text,
            addedDateTime = domainEntity.addedDateTime,
            alarmDateTime = domainEntity.alarmDateTime,
        )
    }

    override fun toDomainList(lstModel: List<NoteModel>): List<Note> {
        return lstModel.map { mapToDomainEntity(it) }
    }

    override fun fromDomainList(lstDomainEntity: List<Note>): List<NoteModel> {
        return lstDomainEntity.map { mapFromDomainEntity(it) }
    }
}
