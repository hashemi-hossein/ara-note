package com.ara.aranote.ui.screens

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
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
    private var idToNavigate: Int? = null
    private var notebookNameToAdd: String? = null

    @Before
    fun setUp() {
        notes = mutableStateOf(listOf(TestUtil.tNoteEntity))
        notebooks = mutableStateOf(TestUtil.tNotebookEntityList)
        idToNavigate = null
        notebookNameToAdd = null
        composeTestRule.setContent {
            HomeScreen(
                notes = notes.value,
                notebooks = notebooks.value,
                navigateToNoteDetailScreen = { idToNavigate = it },
                addNotebook = { notebookNameToAdd = it }
            )
        }
    }

    @Test
    fun note_visibility() {
        // assert
        composeTestRule.onNodeWithText(TestUtil.tNoteEntity.text).assertExists()
        composeTestRule.onNodeWithText(TestUtil.tNotebookEntity.name).assertExists()
        composeTestRule.onNodeWithText(HDateTime.gerPrettyDateTime(TestUtil.tNoteEntity.addedDateTime))
            .assertExists()
    }

    @Test
    fun click_addNoteFAB() {
        // act
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_add_note))
            .performClick()

        // assert
        assertThat(idToNavigate).isEqualTo(INVALID_NOTE_ID)
    }

    @Test
    fun click_note() {
        // act
        composeTestRule.onNodeWithText(TestUtil.tNoteEntity.text).performClick()

        // assert
        assertThat(idToNavigate).isEqualTo(TestUtil.tNoteEntity.id)
    }

    @Test
    fun click_addNotebookIcon() {
        // act
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_add_notebook))
            .performClick()

        // assert
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_confirm_adding_notebook))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.add_notebook)).assertIsDisplayed()
    }
}
