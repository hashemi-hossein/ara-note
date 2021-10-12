package com.ara.aranote.util

import android.content.Context
import android.net.Uri
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NoteRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
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
    private val repository: NoteRepository,
) {

    @Serializable
    data class HBackup(val notebooks: List<Notebook>, val notes: List<Note>)

    suspend fun exportData(uri: Uri, onComplete: () -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val notebooks = repository.observeNotebooks().first()
                val notes = repository.observeNotes().first()
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
    }

    suspend fun importData(uri: Uri, onComplete: () -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                var string = ""
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        string = reader.readText()
                    }
                }
                val backup = Json.decodeFromString<HBackup>(string)
                val notebooks = repository.observeNotebooks().first()
                for (notebook in backup.notebooks) {
                    if (!notebooks.contains(notebook)) {
                        val r = repository.insertNotebook(notebook)
                        if (r == INVALID_NOTEBOOK_ID) {
                            throw Throwable("INVALID_NOTEBOOK_ID")
                        }
                    }
                }
                val notes = repository.observeNotes().first()
                for (note in backup.notes) {
                    if (!notes.contains(note)) {
                        val r = repository.insertNote(note)
                        if (r == INVALID_NOTE_ID) {
                            throw Throwable("INVALID_NOTE_ID")
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
}
