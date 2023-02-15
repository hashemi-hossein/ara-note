package com.ara.aranote.domain.usecase.notebookslist

import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NotebookRepository
import javax.inject.Inject

class CreateNotebookUseCase @Inject constructor(
    private val notebookRepository: NotebookRepository,
) {
    suspend operator fun invoke(notebook: Notebook) =
        notebookRepository.insert(notebook)
}
