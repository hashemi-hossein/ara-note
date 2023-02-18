package ara.note.domain.usecase.notebook

import com.ara.aranote.domain.repository.NotebookRepository
import javax.inject.Inject

class ObserveNotebooksUseCase @Inject constructor(
    private val notebookRepository: NotebookRepository,
) {
    operator fun invoke() =
        notebookRepository.observe()
}
