package com.ara.aranote.domain.usecase.home

import com.ara.aranote.data.datastore.UserPreferences
import com.ara.aranote.data.repository.UserPreferencesRepository
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NotebookRepository
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import com.ara.aranote.util.DEFAULT_NOTEBOOK_NAME
import javax.inject.Inject

class CreateDefaultNotebookUseCase @Inject constructor(
    private val notebookRepository: NotebookRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke() {
        if (!userPreferencesRepository.read().doesDefaultNotebookExist) {
            userPreferencesRepository.write(UserPreferences::doesDefaultNotebookExist, true)
            notebookRepository.insert(
                Notebook(
                    id = DEFAULT_NOTEBOOK_ID,
                    name = DEFAULT_NOTEBOOK_NAME,
                ),
            )
        }
    }
}
