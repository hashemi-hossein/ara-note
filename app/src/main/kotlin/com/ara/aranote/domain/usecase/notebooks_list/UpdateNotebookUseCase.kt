package com.ara.aranote.domain.usecase.notebooks_list

import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NotebookRepository
import javax.inject.Inject

class UpdateNotebookUseCase @Inject constructor(
    private val notebookRepository: NotebookRepository,
) {
    suspend operator fun invoke(notebook: Notebook) =
        notebookRepository.update(notebook)
}
