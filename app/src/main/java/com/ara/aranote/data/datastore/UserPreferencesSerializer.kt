package com.ara.aranote.data.datastore

import androidx.datastore.core.Serializer
import com.ara.aranote.util.CoroutineDispatcherProvider
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class UserPreferencesSerializer(
    private val dispatcherProvider: CoroutineDispatcherProvider
) : Serializer<UserPreferences> {

    override val defaultValue: UserPreferences
        get() = UserPreferences()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        return withContext(dispatcherProvider.io + SupervisorJob()) {
            try {
                Json.decodeFromString(
                    deserializer = UserPreferences.serializer(),
                    string = input.readBytes().decodeToString()
                )
            } catch (e: SerializationException) {
                e.printStackTrace()
                defaultValue
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        withContext(dispatcherProvider.io + SupervisorJob()) {
            output.write(
                Json.encodeToString(
                    serializer = UserPreferences.serializer(),
                    value = t
                ).encodeToByteArray()
            )
        }
    }
}
