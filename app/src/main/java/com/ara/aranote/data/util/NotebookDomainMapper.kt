package com.ara.aranote.data.util

import com.ara.aranote.data.model.NotebookModel
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.util.Mapper

/**
 * Based on CLEAN Architecture:
 * This class is using for mapping Database Model into Domain Entity
 */
class NotebookDomainMapper : Mapper<NotebookModel, Notebook> {

    override fun map(t: NotebookModel): Notebook {
        return Notebook(
            id = t.id,
            name = t.name,
        )
    }

    override fun mapReverse(r: Notebook): NotebookModel {
        return NotebookModel(
            id = r.id,
            name = r.name,
        )
    }
}
