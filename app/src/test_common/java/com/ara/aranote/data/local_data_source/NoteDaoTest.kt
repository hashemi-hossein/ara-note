package com.ara.aranote.data.local_data_source

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.ara.aranote.data.model.NoteModel
import com.ara.aranote.test_util.TestUtil
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class NoteDaoTest {

    private lateinit var database: NoteDatabase
    private lateinit var systemUnderTest: NoteDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, NoteDatabase::class.java
        ).allowMainThreadQueries().build()
        systemUnderTest = database.getNoteDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertSeveralNotes_then_observeAll() = runBlocking {
        // arrange
        val note2 = TestUtil.tNoteModel.copy(
            id = 2,
            text = "t2",
            alarmDateTime = null
        )
        val note3 = TestUtil.tNoteModel.copy(
            id = 5,
            text = "t5",
        )

        // act
        val r = systemUnderTest.insertNote(TestUtil.tNoteModel)
        val r2 = systemUnderTest.insertNote(note2)
        val r3 = systemUnderTest.insertNote(note3)
        val r4 = systemUnderTest.observeNotes(TestUtil.tNoteModel.notebookId).first()

        // assert
        assertThat(r).isEqualTo(TestUtil.tNoteModel.id)
        assertThat(r2).isEqualTo(note2.id)
        assertThat(r3).isEqualTo(note3.id)
        assertThat(r4).containsExactly(note3, note2, TestUtil.tNoteModel).inOrder()
    }

    @Test
    @Throws(Exception::class)
    fun getNote() = runBlocking {
        // act
        val r = systemUnderTest.insertNote(TestUtil.tNoteModel)
        val r2 = systemUnderTest.getById(TestUtil.tNoteModel.id)
        val r3 = systemUnderTest.getById(TestUtil.tNoteModel.id + 1)
        val r4 = systemUnderTest.getById(-1)

        // assert
        assertThat(r).isEqualTo(1)
        assertThat(r2).isEqualTo(TestUtil.tNoteModel)
        assertThat(r3).isEqualTo(null)
        assertThat(r4).isEqualTo(null)
    }

    @Test
    @Throws(Exception::class)
    fun updateNote() = runBlocking {
        // arrange
        val textModifiedNote = TestUtil.tNoteModel.copy(text = "textModified")
        val idModifiedNote = TestUtil.tNoteModel.copy(id = 5)

        // act
        val r = systemUnderTest.insertNote(TestUtil.tNoteModel)
        val r2 = systemUnderTest.getById(TestUtil.tNoteModel.id)
        val r3 = systemUnderTest.updateNote(textModifiedNote)
        val r4 = systemUnderTest.getById(TestUtil.tNoteModel.id)
        val r5 = systemUnderTest.updateNote(idModifiedNote)
        val r6 = systemUnderTest.getById(TestUtil.tNoteModel.id)
        val r7 = systemUnderTest.getById(idModifiedNote.id)

        // assert
        assertThat(r).isEqualTo(TestUtil.tNoteModel.id)
        assertThat(r2).isEqualTo(TestUtil.tNoteModel)
        assertThat(r3).isEqualTo(1)
        assertThat(r4).isEqualTo(textModifiedNote)
        assertThat(r5).isEqualTo(0)
        assertThat(r6).isEqualTo(textModifiedNote)
        assertThat(r7).isEqualTo(null)
    }

    @Test
    @Throws(Exception::class)
    fun deleteNote() = runBlocking {
        // act
        val r = systemUnderTest.insertNote(TestUtil.tNoteModel)
        val r2 = systemUnderTest.getById(TestUtil.tNoteModel.id)
        val r3 = systemUnderTest.deleteNote(TestUtil.tNoteModel)
        val r4 = systemUnderTest.getById(TestUtil.tNoteModel.id)

        // assert
        assertThat(r).isEqualTo(TestUtil.tNoteModel.id)
        assertThat(r2).isEqualTo(TestUtil.tNoteModel)
        assertThat(r3).isEqualTo(1)
        assertThat(r4).isEqualTo(null)
    }

    @Test
    @Throws(Exception::class)
    fun insertSeveralNotes_then_getLastId() = runBlocking {
        // arrange
        val note2 = NoteModel(
            id = 2,
            notebookId = 1,
            text = "t2",
            addedDateTime = TestUtil.tDateTime,
            alarmDateTime = null
        )
        val note3 = NoteModel(
            id = 5,
            notebookId = 1,
            text = "t5",
            addedDateTime = TestUtil.tDateTime,
            alarmDateTime = TestUtil.tDateTime
        )

        // act
        val r = systemUnderTest.insertNote(TestUtil.tNoteModel)
        val r2 = systemUnderTest.insertNote(note2)
        val r3 = systemUnderTest.insertNote(note3)
        val r4 = systemUnderTest.getLastId()

        // assert
        assertThat(r).isEqualTo(TestUtil.tNoteModel.id)
        assertThat(r2).isEqualTo(note2.id)
        assertThat(r3).isEqualTo(note3.id)
        assertThat(r4).isEqualTo(note3.id)
    }

    @Test
    @Throws(Exception::class)
    fun insertNoteWithoutAlarm_then_getAllWithAlarm() = runBlocking {
        // arrange
        val noteWithoutAlarm = TestUtil.tNoteModel.copy(alarmDateTime = null)

        // act
        val r = systemUnderTest.insertNote(noteWithoutAlarm)
        val r2 = systemUnderTest.getAllNotesWithAlarm()

        // assert
        assertThat(r).isEqualTo(noteWithoutAlarm.id)
        assertThat(r2).isEmpty()
    }
}
