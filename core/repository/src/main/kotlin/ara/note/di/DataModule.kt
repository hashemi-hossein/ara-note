package ara.note.di

import ara.note.data.localdatasource.NoteDao
import ara.note.data.localdatasource.NotebookDao
import ara.note.data.repository.NoteRepositoryImpl
import ara.note.data.repository.NotebookRepositoryImpl
import ara.note.domain.repository.NoteRepository
import ara.note.domain.repository.NotebookRepository
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
    ): NoteRepository = NoteRepositoryImpl(noteDao = noteDao)

    @Singleton
    @Provides
    fun provideNotebookRepository(
        notebookDao: NotebookDao,
    ): NotebookRepository = NotebookRepositoryImpl(notebookDao = notebookDao)
}
