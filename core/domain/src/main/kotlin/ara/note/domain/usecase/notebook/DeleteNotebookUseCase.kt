package ara.note.domain.usecase.notebook

import ara.note.domain.entity.Notebook
import ara.note.domain.repository.NoteRepository
import ara.note.domain.repository.NotebookRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteNotebookUseCase @Inject constructor(
    private val notebookRepository: NotebookRepository,
    private val noteRepository: NoteRepository,
) {
    suspend operator fun invoke(notebook: Notebook) {
        val notesOfNotebook = noteRepository.observe(notebook.id).first()
        for (note in notesOfNotebook) {
            noteRepository.delete(note)
        }
        notebookRepository.delete(notebook)
    }
}
