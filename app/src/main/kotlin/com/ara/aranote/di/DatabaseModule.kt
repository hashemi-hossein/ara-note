package com.ara.aranote.di

import android.content.Context
import androidx.room.Room
import com.ara.aranote.data.localdatasource.NoteDao
import com.ara.aranote.data.localdatasource.NoteDatabase
import com.ara.aranote.data.localdatasource.NotebookDao
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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): NoteDatabase =
        Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME,
        ).build()

    @Singleton
    @Provides
    fun provideNoteDao(database: NoteDatabase): NoteDao = database.getNoteDao()

    @Singleton
    @Provides
    fun provideNotebookDao(database: NoteDatabase): NotebookDao = database.getNotebookDao()

    @Singleton
    @Provides
    fun provideNoteDomainMapper(): Mapper<NoteModel, Note> = NoteDomainMapper()

    @Singleton
    @Provides
    fun provideNotebookDomainMapper(): Mapper<NotebookModel, Notebook> = NotebookDomainMapper()
}
