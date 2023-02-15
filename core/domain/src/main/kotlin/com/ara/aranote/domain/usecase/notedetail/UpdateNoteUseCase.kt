package com.ara.aranote.domain.usecase.notedetail

import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.util.HDateTime
import com.ara.aranote.util.Result
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
) {
    suspend operator fun invoke(note: Note) {
        val oldNote = getNoteByIdUseCase(note.id)
        if (oldNote != note) {
//            Timber.tag(TAG).d("updating note")
//            Timber.tag(TAG).d("note = %s", state.note.toString())
            val noteToUpdate = if (oldNote.text != note.text) {
                note.copy(addedDateTime = HDateTime.getCurrentDateTime())
            } else
                note
            noteRepository.update(noteToUpdate) is Result.Success
        }
    }
}
