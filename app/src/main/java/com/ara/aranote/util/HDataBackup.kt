package com.ara.aranote.util

import android.content.Context
import android.net.Uri
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NoteRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import javax.inject.Inject

class HDataBackup
@Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: NoteRepository,
) {

    suspend fun exportData(uri: Uri, onComplete: () -> Unit) {
        withContext(Dispatchers.IO) {
            val notebooks = repository.observeNotebooks().first()
            val notebooksJson = Json.encodeToJsonElement(notebooks)
            val exportedData = notebooksJson.toString()

            context.contentResolver.openFileDescriptor(uri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use { outputStream ->
                    outputStream.write(exportedData.toByteArray())
                }
            }
            onComplete()
        }
    }

    suspend fun importData(uri: Uri, onComplete: () -> Unit) {
        withContext(Dispatchers.IO) {
            var string = ""
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    string = reader.readText()
                }
            }
            val backupNotebooks = Json.decodeFromString<List<Notebook>>(string)
            val notebooks = repository.observeNotebooks().first()

            for (notebook in backupNotebooks) {
                if (!notebooks.contains(notebook)) {
                    val r = repository.insertNotebook(notebook)
                    if (r == INVALID_NOTEBOOK_ID) {
                        throw Throwable("INVALID_NOTEBOOK_ID")
                    }
                }
            }
        }
    }
}
