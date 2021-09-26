package com.ara.aranote.di

import android.content.Context
import androidx.room.Room
import com.ara.aranote.data.local_data_source.NoteDao
import com.ara.aranote.data.local_data_source.NoteDatabase
import com.ara.aranote.data.local_data_source.NotebookDao
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
            NoteDatabase.DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun provideDao(database: NoteDatabase): NoteDao = database.getNoteDao()

    @Singleton
    @Provides
    fun provideNotebookDao(database: NoteDatabase): NotebookDao = database.getNotebookDao()
}
