package com.ara.aranote.ui.screen.home

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
import com.ara.aranote.util.HDateTime
import com.ara.aranote.util.INVALID_NOTE_ID
import com.ara.core_test.TestUtil
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

    private lateinit var uiState: MutableState<HomeState>

    private var noteIdToNavigate: Int? = null
    private var notebookIdToNavigate: Int? = null

    @Before
    fun setUp() {
        uiState = mutableStateOf(
            HomeState(
                notes = TestUtil.tNoteEntityList,
                notebooks = TestUtil.tNotebookEntityList,
                currentNotebookId = TestUtil.tNotebookEntity.id
            )
        )
        val filterNotes = {
            uiState.value =
                uiState.value.copy(notes = TestUtil.tNoteEntityList.filter { it.notebookId == uiState.value.currentNotebookId })
        }
        filterNotes()
        noteIdToNavigate = null
        notebookIdToNavigate = null
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState.value,
                navigateToNoteDetailScreen = { noteId ->
                    noteIdToNavigate = noteId
                    notebookIdToNavigate = uiState.value.currentNotebookId
                },
                navigateToSettingsScreen = {},
                navigateToNotebooksScreen = {},
                setCurrentNotebookId = { notebookId ->
                    uiState.value = uiState.value.copy(currentNotebookId = notebookId)
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
        assertThat(notebookIdToNavigate).isEqualTo(uiState.value.currentNotebookId)
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
        assertThat(uiState.value.currentNotebookId).isEqualTo(TestUtil.tNotebookEntity2.id)
        composeTestRule.onNodeWithText(
            TestUtil.tNoteEntityList.first { it.notebookId == uiState.value.currentNotebookId }.text,
            substring = true,
        ).assertIsDisplayed()
    }
}
