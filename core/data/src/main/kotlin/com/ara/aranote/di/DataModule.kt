package com.ara.aranote.di

import com.ara.aranote.data.localdatasource.NoteDao
import com.ara.aranote.data.localdatasource.NotebookDao
import ara.note.data.model.NoteModel
import ara.note.data.model.NotebookModel
import com.ara.aranote.data.repository.NoteRepositoryImpl
import com.ara.aranote.data.repository.NotebookRepositoryImpl
import com.ara.aranote.data.util.NoteDomainMapper
import com.ara.aranote.data.util.NotebookDomainMapper
import ara.note.domain.entity.Note
import ara.note.domain.entity.Notebook
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.domain.repository.NotebookRepository
import com.ara.aranote.domain.util.Mapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

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
        notebookDomainMapper: Mapper<NotebookModel, Notebook>,
    ): NotebookRepository = NotebookRepositoryImpl(
        notebookDao = notebookDao,
        notebookDomainMapper = notebookDomainMapper,
    )

    @Singleton
    @Provides
    fun provideNoteDomainMapper(): Mapper<NoteModel, Note> = NoteDomainMapper()

    @Singleton
    @Provides
    fun provideNotebookDomainMapper(): Mapper<NotebookModel, Notebook> = NotebookDomainMapper()
}
