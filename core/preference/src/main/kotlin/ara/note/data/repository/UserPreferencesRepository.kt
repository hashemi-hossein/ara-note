package ara.note.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import ara.note.data.model.DarkMode
import ara.note.data.model.NoteViewMode
import ara.note.data.model.UserPreferences
import ara.note.util.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KProperty1

/**
 * The repository which handles saving and retrieving user preferences
 */
@Singleton
class UserPreferencesRepository
@Inject constructor(private val userPreferencesStore: DataStore<UserPreferences>) {

    suspend fun read() = userPreferencesStore.data.first()

    suspend fun <T> write(kProperty: KProperty1<UserPreferences, T>, value: T) =
        userPreferencesStore.updateData {
            Log.d(TAG, "$kProperty-- value= $value")
            when (kProperty) {
                UserPreferences::darkMode -> {
                    it.copy(darkMode = value as DarkMode)
                }
                UserPreferences::noteViewMode -> {
                    it.copy(noteViewMode = value as NoteViewMode)
                }
                UserPreferences::isAutoSaveMode -> {
                    it.copy(isAutoSaveMode = value as Boolean)
                }
                UserPreferences::isDoubleBackToExitMode -> {
                    it.copy(isDoubleBackToExitMode = value as Boolean)
                }
                else -> {
                    error("wrong input")
                }
            }
        }

    fun observe(): Flow<UserPreferences> = userPreferencesStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(UserPreferences())
            } else {
                throw exception
            }
        }
}
