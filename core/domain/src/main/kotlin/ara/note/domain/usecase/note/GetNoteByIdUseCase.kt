package ara.note.domain.usecase.note

import ara.note.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteByIdUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
) {
    suspend operator fun invoke(noteId: Int) =
        noteRepository.getById(noteId)
}
