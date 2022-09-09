package com.ara.aranote.ui.screen

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.ara.aranote.R
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.test_util.TestUtil
import com.ara.aranote.ui.screen.home.HomeScreen
import com.ara.aranote.util.HDateTime
import com.ara.aranote.util.INVALID_NOTE_ID
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private lateinit var notes: MutableState<List<Note>>
    private lateinit var notebooks: MutableState<List<Notebook>>
    private lateinit var currentNotebookId: MutableState<Int>

    private var noteIdToNavigate: Int? = null
    private var notebookIdToNavigate: Int? = null
//    private var notebookNameToAdd: String? = null

    @Before
    fun setUp() {
        notes = mutableStateOf(TestUtil.tNoteEntityList)
        notebooks = mutableStateOf(TestUtil.tNotebookEntityList)
        currentNotebookId = mutableStateOf(TestUtil.tNotebookEntity.id)
        val filterNotes = {
            notes.value =
                TestUtil.tNoteEntityList.filter { it.notebookId == currentNotebookId.value }
        }
        filterNotes()
        noteIdToNavigate = null
        notebookIdToNavigate = null
//        notebookNameToAdd = null
        composeTestRule.setContent {
            HomeScreen(
                notes = notes.value,
                notebooks = notebooks.value,
                navigateToNoteDetailScreen = { noteId ->
                    noteIdToNavigate = noteId
                    notebookIdToNavigate = currentNotebookId.value
                },
                navigateToSettingsScreen = {},
//                addNotebook = { notebookNameToAdd = it },
                navigateToNotebooksScreen = {},
                currentNotebookId = currentNotebookId.value,
                setCurrentNotebookId = { notebookId ->
                    currentNotebookId.value = notebookId
                    filterNotes()
                },
            )
        }
    }

    @Test
    fun note_visibility() {
        // assert
        composeTestRule.onNodeWithText(TestUtil.tNoteEntity.text).assertExists()
        // notebook name in the Appbar
        composeTestRule.onNode(
            !SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton) and
                hasText(TestUtil.tNotebookEntity.name)
        ).assertIsDisplayed()
        // notebook name in the drawer
        composeTestRule.onNode(
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton) and
                hasText(TestUtil.tNotebookEntity.name)
        ).assertExists()
        composeTestRule.onNodeWithText(HDateTime.gerPrettyDateTime(TestUtil.tNoteEntity.addedDateTime))
            .assertExists()
    }

    @Test
    fun click_addNoteFAB() {
        // act
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_add_note))
            .performClick()

        // assert
        assertThat(noteIdToNavigate).isEqualTo(INVALID_NOTE_ID)
    }

    @Test
    fun click_note() {
        // act
        composeTestRule.onNodeWithText(TestUtil.tNoteEntity.text, substring = true).performClick()

        // assert
        assertThat(noteIdToNavigate).isEqualTo(TestUtil.tNoteEntity.id)
        assertThat(notebookIdToNavigate).isEqualTo(currentNotebookId.value)
    }

//    @Test
//    fun click_addNotebookIcon() {
//        // act
//        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_add_notebook))
//            .performClick()
//
//        // assert
//        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_dialog_confirm))
//            .assertIsDisplayed()
//        composeTestRule.onNodeWithText(context.getString(R.string.add_notebook)).assertIsDisplayed()
//    }

    @Test
    fun change_notebook_on_clicking() {
        // act
        composeTestRule.onNodeWithText(TestUtil.tNotebookEntity2.name).performClick()

        // assert
        assertThat(currentNotebookId.value).isEqualTo(TestUtil.tNotebookEntity2.id)
        composeTestRule.onNodeWithText(
            TestUtil.tNoteEntityList.first { it.notebookId == currentNotebookId.value }.text,
            substring = true,
        ).assertIsDisplayed()
    }
}
