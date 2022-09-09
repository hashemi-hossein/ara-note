package com.ara.aranote.domain.usecase.home

import com.ara.aranote.domain.repository.NoteRepository
import javax.inject.Inject

class ObserveNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
) {
    operator fun invoke(notebookId: Int? = null) =
        noteRepository.observe(notebookId)
}
