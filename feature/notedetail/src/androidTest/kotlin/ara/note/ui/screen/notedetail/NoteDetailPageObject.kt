package ara.note.ui.screen.notedetail

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ara.note.notedetail.R.string
import ara.note.test.BasePageObject
import ara.note.test.TestUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.test.assertContains

/**
 * Page Object for NoteDetailScreen
 */
internal class NoteDetailPageObject(
    private val composeTestRule: ComposeContentTestRule,
) : BasePageObject() {

    private val uiState = MutableStateFlow(NoteDetailState(notebooks = TestUtil.tNotebookEntityList))
    private val event = mutableListOf<String>()

    @OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
    override fun setUp() {
        composeTestRule.setContent {
            NoteDetailScreen(
                uiState = uiState.collectAsStateWithLifecycle().value,
                singleEvent = MutableSharedFlow(),
                navigateUp = { event.add("navigateUp") },
                saveNote = { event.add("saveNote") },
                deleteNote = { event.add("deleteNote") },
                onNoteChanged = { note -> uiState.update { it.copy(note = note) } },
            )
        }
    }

    private fun findTextField(text: String = uiState.value.note.text.ifEmpty { getString(string.type_here) }) = composeTestRule.onNodeWithText(text)

    private fun findNotebookText(text: String = uiState.value.notebooks[uiState.value.note.notebookId - 1].name) =
        composeTestRule.onNodeWithText(text)

    private fun findNotebookDropdownItem(text: String) = composeTestRule.onNode(
        !SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button) and
            hasText(text),
    )

    private fun findDeleteButton() =
        composeTestRule.onNodeWithContentDescription(if (uiState.value.isNewNote) getString(string.cd_discard) else getString(string.cd_delete))

    private fun findBackButton() = composeTestRule.onNodeWithContentDescription(getString(ara.note.ui.R.string.cd_appbar_back))

    private fun findSnackbarButton(text: String) = composeTestRule.onNodeWithText(text)

    fun setIsNewNote(isNewNote: Boolean) = uiState.update { it.copy(isNewNote = isNewNote) }

    fun setAutoSaveMode(on: Boolean) = uiState.update { it.copy(userPreferences = it.userPreferences.copy(isAutoSaveMode = on)) }

    fun typeIntoTextField(text: String) = findTextField().performTextInput(text)

    fun textIsDisplayingInTextField(text: String) = findTextField(text).assertIsDisplayed()

    fun clickOnBackButton() = findBackButton().performClick()

    fun clickOnDeleteButton() = findDeleteButton().performClick()

    fun clickOnNotebookDropdown() = findNotebookText().assertIsDisplayed().performClick()

    fun clickOnLastDropdownItem() =
        findNotebookDropdownItem(uiState.value.notebooks.last().name).assertIsDisplayed().performClick()

    fun confirmSnackbarDiscarding() = findSnackbarButton(getString(string.discard)).performClick()
    fun confirmSnackbarDeleting() = findSnackbarButton(getString(string.delete)).performClick()

    fun assertLastNotebookIsDisplaying() = findNotebookText(uiState.value.notebooks.last().name).assertIsDisplayed()

    fun assertAllNotebooksAreDisplaying() {
        for (item in uiState.value.notebooks)
            findNotebookDropdownItem(item.name).assertIsDisplayed()
    }

    fun assertSaveTriggered() = assertContains(event, "saveNote")

    fun assertNavigateUpTriggered() = assertContains(event, "navigateUp")

    fun assertDeleteTriggered() = assertContains(event, "deleteNote")
}
