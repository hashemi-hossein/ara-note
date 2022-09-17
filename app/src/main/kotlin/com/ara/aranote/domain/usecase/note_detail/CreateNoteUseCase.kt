package com.ara.aranote.domain.usecase.note_detail

import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.util.Result
import javax.inject.Inject

class CreateNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
) {
    suspend operator fun invoke(note: Note) =
        noteRepository.insert(note) is Result.Success
}
