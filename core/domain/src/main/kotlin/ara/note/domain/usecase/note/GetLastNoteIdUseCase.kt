package ara.note.domain.usecase.note

import com.ara.aranote.domain.repository.NoteRepository
import ara.note.util.Result
import javax.inject.Inject

class GetLastNoteIdUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
) {
    suspend operator fun invoke() =
        noteRepository.getLastId().let {
            when (it) {
                is Result.Success -> it.data
                is Result.Error -> 0
            }
        }
}
