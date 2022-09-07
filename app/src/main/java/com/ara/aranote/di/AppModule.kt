package com.ara.aranote.di

import android.content.Context
import com.ara.aranote.data.local_data_source.NoteDao
import com.ara.aranote.data.local_data_source.NotebookDao
import com.ara.aranote.data.model.NoteModel
import com.ara.aranote.data.model.NotebookModel
import com.ara.aranote.data.repository.NoteRepositoryImpl
import com.ara.aranote.data.repository.NotebookRepositoryImpl
import com.ara.aranote.data.util.NoteDomainMapperImpl
import com.ara.aranote.data.util.NotebookDomainMapperImpl
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.domain.repository.NotebookRepository
import com.ara.aranote.domain.util.DomainMapper
import com.ara.aranote.ui.BaseApplication
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
        noteDomainMapper: DomainMapper<NoteModel, Note>,
    ): NoteRepository = NoteRepositoryImpl(
        noteDao = noteDao,
        noteDomainMapper = noteDomainMapper,
    )

    @Singleton
    @Provides
    fun provideNotebookRepository(
        notebookDao: NotebookDao,
        notebookDomainMapper: DomainMapper<NotebookModel, Notebook>
    ): NotebookRepository = NotebookRepositoryImpl(
        notebookDao = notebookDao,
        notebookDomainMapper = notebookDomainMapper,
    )

    @Singleton
    @Provides
    fun provideNoteDomainMapper(): DomainMapper<NoteModel, Note> = NoteDomainMapperImpl()

    @Singleton
    @Provides
    fun provideNotebookDomainMapper(): DomainMapper<NotebookModel, Notebook> =
        NotebookDomainMapperImpl()
}
