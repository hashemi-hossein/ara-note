package com.ara.aranote.domain.usecase.home

import ara.note.domain.entity.Notebook
import com.ara.aranote.domain.repository.NotebookRepository
import ara.note.util.DEFAULT_NOTEBOOK_ID
import ara.note.util.DEFAULT_NOTEBOOK_NAME
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CreateDefaultNotebookUseCase @Inject constructor(
    private val notebookRepository: NotebookRepository,
) {
    suspend operator fun invoke() {
        if (notebookRepository.observe().first().isEmpty()) {
            notebookRepository.insert(
                Notebook(
                    id = DEFAULT_NOTEBOOK_ID,
                    name = DEFAULT_NOTEBOOK_NAME,
                ),
            )
        }
    }
}
