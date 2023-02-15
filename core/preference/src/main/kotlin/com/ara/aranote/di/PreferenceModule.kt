package com.ara.aranote.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.ara.aranote.data.datastore.UserPreferences
import com.ara.aranote.data.datastore.UserPreferencesSerializer
import com.ara.aranote.util.CoroutineDispatcherProvider
import com.ara.aranote.util.USER_PREFERENCES_FILE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    @Singleton
    @Provides
    fun provideUserPreferencesStore(
        @ApplicationContext context: Context,
        dispatcherProvider: CoroutineDispatcherProvider,
        userPreferencesSerializer: UserPreferencesSerializer,
    ): DataStore<UserPreferences> = DataStoreFactory.create(
        serializer = userPreferencesSerializer,
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { UserPreferences() },
        ),
        scope = CoroutineScope(dispatcherProvider.io + SupervisorJob()),
        produceFile = { context.dataStoreFile(USER_PREFERENCES_FILE_NAME) },
    )
}