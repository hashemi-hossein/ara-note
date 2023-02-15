package com.ara.aranote.domain.usecase.notedetail

import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.util.Result
import javax.inject.Inject

class GetNoteByIdUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
) {
    suspend operator fun invoke(noteId: Int) =
        noteRepository.getById(noteId).let {
            when (it) {
                is Result.Success -> it.data
                is Result.Error -> error("Not Found")
            }
        }
}
