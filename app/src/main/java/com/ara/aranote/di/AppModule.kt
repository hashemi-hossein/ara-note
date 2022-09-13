package com.ara.aranote.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.ara.aranote.data.datastore.UserPreferences
import com.ara.aranote.data.datastore.UserPreferencesSerializer
import com.ara.aranote.data.local_data_source.NoteDao
import com.ara.aranote.data.local_data_source.NotebookDao
import com.ara.aranote.data.model.NoteModel
import com.ara.aranote.data.model.NotebookModel
import com.ara.aranote.data.repository.NoteRepositoryImpl
import com.ara.aranote.data.repository.NotebookRepositoryImpl
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.domain.repository.NotebookRepository
import com.ara.aranote.domain.util.Mapper
import com.ara.aranote.ui.main.BaseApplication
import com.ara.aranote.util.CoroutineDispatcherProvider
import com.ara.aranote.util.USER_PREFERENCES_FILE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext context: Context): BaseApplication {
        return context as BaseApplication
    }

    @Singleton
    @Provides
    fun provideNoteRepository(
        noteDao: NoteDao,
        noteDomainMapper: Mapper<NoteModel, Note>,
    ): NoteRepository = NoteRepositoryImpl(
        noteDao = noteDao,
        noteDomainMapper = noteDomainMapper,
    )

    @Singleton
    @Provides
    fun provideNotebookRepository(
        notebookDao: NotebookDao,
        notebookDomainMapper: Mapper<NotebookModel, Notebook>
    ): NotebookRepository = NotebookRepositoryImpl(
        notebookDao = notebookDao,
        notebookDomainMapper = notebookDomainMapper,
    )

    private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
        fileName = USER_PREFERENCES_FILE_NAME,
        serializer = UserPreferencesSerializer(CoroutineDispatcherProvider())
    )

    @Singleton
    @Provides
    fun provideUserPreferencesStore(
        @ApplicationContext context: Context
    ): DataStore<UserPreferences> = context.userPreferencesStore
}
