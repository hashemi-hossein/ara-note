package com.ara.aranote.di

import com.ara.aranote.data.model.NoteModel
import com.ara.aranote.data.model.NotebookModel
import com.ara.aranote.data.util.NoteDomainMapper
import com.ara.aranote.data.util.NotebookDomainMapper
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
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
    fun provideNoteDomainMapper(): Mapper<NoteModel, Note> = NoteDomainMapper()

    @Singleton
    @Provides
    fun provideNotebookDomainMapper(): Mapper<NotebookModel, Notebook> = NotebookDomainMapper()
}
