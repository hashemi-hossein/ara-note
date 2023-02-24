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

    suspend fun exportData(uri: Uri, onComplete: (result: String) -> Unit) =
        withContext(coroutineDispatcherProvider.io) {
            var result: String
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
                result = "Success"
            } catch (e: IOException) {
//                e.printStackTrace()
                result = "Error\n${e.localizedMessage}"
            }
            onComplete(result)
        }

    suspend fun importData(uri: Uri, onComplete: (result: String) -> Unit) =
        withContext(coroutineDispatcherProvider.io) {
            var result: String
            try {
                var stringBackup = ""
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        stringBackup = reader.readText()
                    }
                }
                val backup = Json.decodeFromString<HBackup>(stringBackup)

                notebookRepository.observe().first().forEach { notebookRepository.delete(it) }
                for (notebook in backup.notebooks) {
                    notebookRepository.insert(notebook).let {
                        when (it) {
                            is Success -> it.data
                            is Error -> error("Error in importing one notebook\n" + it.errorMessage)
                        }
                    }
                }

                noteRepository.observe().first().forEach { noteRepository.delete(it) }
                for (note in backup.notes) {
                    noteRepository.insert(note).let {
                        when (it) {
                            is Success -> it.data
                            is Error -> error("Error in importing one note\n" + it.errorMessage)
                        }
                    }
                }

                result = "Success"
            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
                result = "Error\n${e.localizedMessage}"
            } catch (e: IOException) {
//                e.printStackTrace()
                result = "Error\n${e.localizedMessage}"
            } catch (e: Exception) {
//                e.printStackTrace()
                result = "Error\n${e.localizedMessage}"
            }
            onComplete(result)
        }
}
