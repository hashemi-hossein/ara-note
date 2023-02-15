package com.ara.aranote.domain.usecase.notedetail

import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.util.Result
import javax.inject.Inject

class GetLastNoteIdUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
) {
    suspend operator fun invoke() =
        noteRepository.getLastId().let {
            when (it) {
                is Result.Success -> it.data
                is Result.Error -> 0
            }
        }
}
