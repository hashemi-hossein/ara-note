package ara.note.domain.usecase.notebook

import ara.note.domain.entity.Notebook
import ara.note.domain.repository.NotebookRepository
import javax.inject.Inject

class UpdateNotebookUseCase @Inject constructor(
    private val notebookRepository: NotebookRepository,
) {
    suspend operator fun invoke(notebook: Notebook) =
        notebookRepository.update(notebook)
}
