package ara.note.domain.usecase.note

import com.ara.aranote.domain.repository.NoteRepository
import ara.note.util.Result
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
