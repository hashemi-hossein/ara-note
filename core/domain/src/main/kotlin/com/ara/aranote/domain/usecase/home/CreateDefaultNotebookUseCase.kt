package com.ara.aranote.domain.usecase.home

import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NotebookRepository
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import com.ara.aranote.util.DEFAULT_NOTEBOOK_NAME
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
