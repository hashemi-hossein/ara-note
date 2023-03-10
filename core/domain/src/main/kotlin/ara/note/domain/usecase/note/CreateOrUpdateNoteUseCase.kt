package ara.note.domain.usecase.note

import ara.note.domain.entity.Note
import ara.note.domain.repository.NoteRepository
import ara.note.util.HDateTime
import ara.note.util.Result
import javax.inject.Inject

class CreateOrUpdateNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
) {
    suspend operator fun invoke(note: Note) =
        when (val oldNote = getNoteByIdUseCase(note.id)) {
            is Result.Success -> {
                if (oldNote.data != note) {
                    val noteToUpdate = if (oldNote.data.text != note.text) {
                        note.copy(modifiedDateTime = HDateTime.getCurrentDateTime())
                    } else {
                        note
                    }
                    noteRepository.update(noteToUpdate) is Result.Success
                } else {
                    true
                }
            }
            is Result.Error -> if (note.text.isNotBlank()) noteRepository.insert(note) is Result.Success else true
        }
}
