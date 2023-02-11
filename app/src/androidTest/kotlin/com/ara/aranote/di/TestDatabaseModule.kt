package com.ara.aranote.di

import android.content.Context
import androidx.room.Room
import com.ara.aranote.data.localdatasource.NoteDao
import com.ara.aranote.data.localdatasource.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): NoteDatabase =
        Room.inMemoryDatabaseBuilder(
            context,
            NoteDatabase::class.java,
        ).build()

    @Singleton
    @Provides
    fun provideDao(database: NoteDatabase): NoteDao = database.getNoteDao()
}
