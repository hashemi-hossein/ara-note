package ara.note.domain.usecase.notebook

import ara.note.domain.repository.NotebookRepository
import javax.inject.Inject

class ObserveNotebooksUseCase @Inject constructor(
    private val notebookRepository: NotebookRepository,
) {
    operator fun invoke() =
        notebookRepository.observe()
}
