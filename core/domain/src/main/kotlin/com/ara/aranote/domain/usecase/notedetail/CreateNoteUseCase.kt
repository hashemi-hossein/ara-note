package com.ara.aranote.domain.usecase.notedetail

import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.repository.NoteRepository
import ara.note.util.Result
import javax.inject.Inject

class CreateNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
) {
    suspend operator fun invoke(note: Note) =
        noteRepository.insert(note) is Result.Success
}
