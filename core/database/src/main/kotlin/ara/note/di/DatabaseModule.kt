package ara.note.di

import android.content.Context
import androidx.room.Room
import ara.note.data.localdatasource.NoteDao
import ara.note.data.localdatasource.NoteDatabase
import ara.note.data.localdatasource.NotebookDao
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
}
