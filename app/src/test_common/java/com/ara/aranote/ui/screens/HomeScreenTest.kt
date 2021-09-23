package com.ara.aranote.ui.screens

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.ara.aranote.R
import com.ara.aranote.domain.entity.Note
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
    private var idToNavigate: Int? = null

    @Before
    fun setUp() {
        notes = mutableStateOf(listOf(TestUtil.tEntity))
        idToNavigate = null
        composeTestRule.setContent {
            HomeScreen(
                notes = notes.value,
                navigateToNoteDetailScreen = { idToNavigate = it },
            )
        }
    }

    @Test
    fun note_visibility() {
        // assert
        composeTestRule.onNodeWithText(TestUtil.tEntity.text).assertExists()
        composeTestRule.onNodeWithText(HDateTime.gerPrettyDateTime(TestUtil.tEntity.addedDateTime))
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
        composeTestRule.onNodeWithText(TestUtil.tEntity.text).performClick()

        // assert
        assertThat(idToNavigate).isEqualTo(TestUtil.tEntity.id)
    }
}
