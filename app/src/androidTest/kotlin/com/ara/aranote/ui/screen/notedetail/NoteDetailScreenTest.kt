package com.ara.aranote.ui.screen.notedetail

import android.content.Context
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import ara.note.ui.screen.notedetail.NoteDetailState
import ara.note.R
import ara.note.ui.screen.notedetail.NoteDetailScreen
import com.ara.aranote.domain.entity.Note
import ara.note.ui.screen.notedetail.NoteDetailViewModel.TheOperation
import ara.note.util.DateTimeFormatPattern
import ara.note.util.HDateTime
import ara.note.util.plus
import com.ara.test.TestUtil
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.hours

@RunWith(AndroidJUnit4::class)
@SmallTest
class NoteDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private lateinit var uiState: MutableState<NoteDetailState>
    private var backPressResult: TheOperation? = null

    @OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
    @Before
    fun setUp() {
        uiState = mutableStateOf(
            NoteDetailState(
                note = Note(id = 1),
                notebooks = TestUtil.tNotebookEntityList,
            ),
        )
        backPressResult = null
        composeTestRule.setContent {
            NoteDetailScreen(
                uiState = uiState.value,
                onNoteChanged = { uiState.value = uiState.value.copy(note = it) },
                onBackPressed = { backPressResult = it },
                isNewNote = true,
            )
        }
    }

    @Test
    fun entering_text() {
        // act
        composeTestRule.onNodeWithText(context.getString(R.string.type_here))
            .performTextReplacement("Hello")

        // assert
        composeTestRule.onNodeWithText("Hello").assertIsDisplayed()
        assertThat(uiState.value.note.text).isEqualTo("Hello")
    }

    @Test
    fun notebooks_visibility() {
        // act
        composeTestRule.onNodeWithText(TestUtil.tNotebookEntityList[uiState.value.note.notebookId - 1].name)
            .assertIsDisplayed().performClick()

        // assert
        for (item in TestUtil.tNotebookEntityList)
            composeTestRule.onNode(
                !SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button) and
                    hasText(item.name),
            ).assertIsDisplayed()
    }

    @Test
    fun changing_notebook_of_note() {
        // act
        composeTestRule.onNodeWithText(TestUtil.tNotebookEntityList[uiState.value.note.notebookId - 1].name)
            .assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText(TestUtil.tNotebookEntityList[1].name)
            .assertIsDisplayed().performClick()

        // assert
        for (item in TestUtil.tNotebookEntityList)
            composeTestRule.onNode(
                !SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button) and
                    hasText(item.name),
            ).assertDoesNotExist()
        assertThat(uiState.value.note.notebookId).isEqualTo(TestUtil.tNotebookEntityList[1].id)
    }

    @Test
    fun addAlarmVisibility() {
        // assert
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_add_alarm))
            .assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_delete_alarm))
            .assertDoesNotExist()
    }

    @Test
    fun open_alarm_bottomSheet() {
        // act
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_add_alarm))
            .assertIsDisplayed().performClick()

        // assert
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_set_alarm))
            .assertIsDisplayed()
    }

    @Test
    fun set_invalid_alarm() {
        // act
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_add_alarm))
            .assertIsDisplayed().performClick()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_set_alarm))
            .assertIsDisplayed().performClick()

        // assert
        composeTestRule.onNodeWithText(context.getString(R.string.invalid_date_and_time))
            .assertIsDisplayed()
    }

    @Test
    fun set_and_reset_alarmDateTime() {
        // arrange
        val newAlarmDateTime = HDateTime.getCurrentDateTime().plus(25.hours)

        // act
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_add_alarm))
            .assertIsDisplayed().performClick()
        uiState.value = uiState.value.copy(note = uiState.value.note.copy(alarmDateTime = newAlarmDateTime))

        // assert
        composeTestRule.onNodeWithText(
            HDateTime.formatDateAndTime(
                newAlarmDateTime,
                DateTimeFormatPattern.DATE,
            ),
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            HDateTime.formatDateAndTime(
                newAlarmDateTime,
                DateTimeFormatPattern.TIME,
            ),
        ).assertIsDisplayed()

        // act
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_reset_date_and_time))
            .assertIsDisplayed().performClick()

        // assert
        composeTestRule.onNodeWithText(
            HDateTime.formatDateAndTime(
                HDateTime.getCurrentDateTime(),
                DateTimeFormatPattern.DATE,
            ),
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            HDateTime.formatDateAndTime(
                HDateTime.getCurrentDateTime(),
                DateTimeFormatPattern.TIME,
            ),
        ).assertIsDisplayed()
    }

    @Test
    fun delete_alarm() {
        // arrange
        uiState.value = uiState.value.copy(note = uiState.value.note.copy(alarmDateTime = HDateTime.getCurrentDateTime()))

        // act
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_delete_alarm))
            .assertIsDisplayed().performClick()

        // assert
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_delete_alarm))
            .assertDoesNotExist()
        assertThat(uiState.value.note.alarmDateTime).isNull()
    }

    @Test
    fun back_button_press() {
        // act
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_happbar_back))
            .assertIsDisplayed().performClick()

        // assert
        assertThat(backPressResult).isEqualTo(TheOperation.SAVE)
    }

    @Test
    fun clickDeleteIcon_when_no_text() {
        // act
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_discard))
            .performClick()
        // snackbar
        // this way can be used
        // https://github.com/android/compose-samples/blob/main/JetNews/app/src/sharedTest/java/com/example/jetnews/HomeScreenTests.kt
//        composeTestRule.onNodeWithText(context.getString(R.string.discard)).assertDoesNotExist()

        // assert
        assertThat(backPressResult).isEqualTo(TheOperation.DISCARD)
    }

    @Test
    fun clickDeleteIcon_when_text() {
        // arrange
        uiState.value = uiState.value.copy(note = uiState.value.note.copy(text = "hello"))

        // act
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_discard))
            .performClick()
        // snackbar
//        composeTestRule.onNodeWithText(context.getString(R.string.discard))
//            .assertIsDisplayed().performClick()

        // assert
        assertThat(backPressResult).isEqualTo(TheOperation.DISCARD)
    }
}
