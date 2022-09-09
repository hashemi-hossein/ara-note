package com.ara.aranote.domain.usecase.home

import com.ara.aranote.data.datastore.AppDataStore
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NotebookRepository
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import com.ara.aranote.util.DEFAULT_NOTEBOOK_NAME
import javax.inject.Inject

class CreateDefaultNotebookUseCase @Inject constructor(
    private val notebookRepository: NotebookRepository,
    private val appDataStore: AppDataStore,
) {
    suspend operator fun invoke() {
        if (!appDataStore.readPref(AppDataStore.DEFAULT_NOTEBOOK_EXISTENCE_KEY, false)) {
            appDataStore.writePref(AppDataStore.DEFAULT_NOTEBOOK_EXISTENCE_KEY, true)
            notebookRepository.insert(
                Notebook(
                    id = DEFAULT_NOTEBOOK_ID,
                    name = DEFAULT_NOTEBOOK_NAME
                )
            )
        }
    }
}
