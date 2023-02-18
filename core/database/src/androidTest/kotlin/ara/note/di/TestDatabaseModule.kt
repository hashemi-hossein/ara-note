package ara.note.di

import android.content.Context
import androidx.room.Room
import ara.note.data.localdatasource.NoteDao
import ara.note.data.localdatasource.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class],
)
object TestDatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java,
        ).build()

    @Singleton
    @Provides
    fun provideDao(database: AppDatabase): NoteDao = database.getNoteDao()
}
