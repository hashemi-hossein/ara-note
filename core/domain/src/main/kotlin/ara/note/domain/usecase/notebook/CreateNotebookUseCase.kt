package ara.note.domain.usecase.notebook

import ara.note.domain.entity.Notebook
import com.ara.aranote.domain.repository.NotebookRepository
import javax.inject.Inject

class CreateNotebookUseCase @Inject constructor(
    private val notebookRepository: NotebookRepository,
) {
    suspend operator fun invoke(notebook: Notebook) =
        notebookRepository.insert(notebook)
}
