package ara.note.di

import android.content.Context
import androidx.room.Room
import ara.note.data.localdatasource.AppDatabase
import ara.note.data.localdatasource.MIGRATION_2_3
import ara.note.data.localdatasource.NoteDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME,
        )
            .addMigrations(MIGRATION_2_3)
            .build()

    @Singleton
    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao = database.getNoteDao()

    @Singleton
    @Provides
    fun provideNotebookDao(database: AppDatabase): NotebookDao = database.getNotebookDao()
}
