package ara.note.domain.usecase.note

import ara.note.domain.repository.NoteRepository
import javax.inject.Inject

class ObserveNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
) {
    operator fun invoke(notebookId: Int?, searchText: String?) =
        noteRepository.observe(notebookId, searchText)
}
