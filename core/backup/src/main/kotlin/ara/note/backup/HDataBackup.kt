package ara.note.backup

import android.content.Context
import android.net.Uri
import ara.note.domain.entity.Note
import ara.note.domain.entity.Notebook
import ara.note.domain.repository.NoteRepository
import ara.note.domain.repository.NotebookRepository
import ara.note.util.CoroutineDispatcherProvider
import ara.note.util.Result.Error
import ara.note.util.Result.Success
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

class HDataBackup
@Inject constructor(
    @ApplicationContext private val context: Context,
    private val noteRepository: NoteRepository,
    private val notebookRepository: NotebookRepository,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    @Serializable
    data class HBackup(val notebooks: List<Notebook>, val notes: List<Note>)

    suspend fun exportData(uri: Uri, onComplete: () -> Unit) =
        withContext(coroutineDispatcherProvider.io) {
            try {
                val notebooks = notebookRepository.observe().first()
                val notes = noteRepository.observe().first()
                val hBackup = HBackup(notebooks, notes)
                val exportedData = Json.encodeToString(hBackup)

                context.contentResolver.openFileDescriptor(uri, "w")?.use {
                    FileOutputStream(it.fileDescriptor).use { outputStream ->
                        outputStream.write(exportedData.toByteArray())
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            onComplete()
        }

    suspend fun importData(uri: Uri, onComplete: () -> Unit) =
        withContext(coroutineDispatcherProvider.io) {
            try {
                var string = ""
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        string = reader.readText()
                    }
                }
                val backup = Json.decodeFromString<HBackup>(string)
                val notebooks = notebookRepository.observe().first()
                for (notebook in backup.notebooks) {
                    if (!notebooks.contains(notebook)) {
                        notebookRepository.insert(notebook).let {
                            when (it) {
                                is Success -> it.data
                                is Error -> error("INVALID_NOTEBOOK_ID")
                            }
                        }
                    }
                }
                val notes = noteRepository.observe().first()
                for (note in backup.notes) {
                    if (!notes.contains(note)) {
                        noteRepository.insert(note).let {
                            when (it) {
                                is Success -> it.data
                                is Error -> error("INVALID_NOTE_ID")
                            }
                        }
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            onComplete()
        }
}
