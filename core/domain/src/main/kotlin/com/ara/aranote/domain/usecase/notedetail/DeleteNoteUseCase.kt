package com.ara.aranote.domain.usecase.notedetail

import ara.note.domain.entity.Note
import com.ara.aranote.domain.repository.NoteRepository
import ara.note.util.Result
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
) {
    suspend operator fun invoke(note: Note) =
        noteRepository.delete(note) is Result.Success
}
