package ara.note.domain.usecase.note

import ara.note.domain.entity.Note
import ara.note.domain.repository.NoteRepository
import ara.note.util.HDateTime
import ara.note.util.Result
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
                note.copy(modifiedDateTime = HDateTime.getCurrentDateTime())
            } else {
                note
            }
            noteRepository.update(noteToUpdate) is Result.Success
        }
    }
}
