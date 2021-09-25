package com.ara.aranote.data.util

import com.ara.aranote.data.model.NotebookModel
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.util.DomainMapper

class NotebookDomainMapperImpl : DomainMapper<NotebookModel, Notebook> {

    override fun mapToDomainEntity(model: NotebookModel): Notebook {
        return Notebook(
            id = model.id,
            name = model.name,
        )
    }

    override fun mapFromDomainEntity(domainEntity: Notebook): NotebookModel {
        return NotebookModel(
            id = domainEntity.id,
            name = domainEntity.name,
        )
    }

    override fun toDomainList(lstModel: List<NotebookModel>): List<Notebook> {
        return lstModel.map { mapToDomainEntity(it) }
    }

    override fun fromDomainList(lstDomainEntity: List<Notebook>): List<NotebookModel> {
        return lstDomainEntity.map { mapFromDomainEntity(it) }
    }
}
