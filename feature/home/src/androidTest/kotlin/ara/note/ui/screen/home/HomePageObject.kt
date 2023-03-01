package ara.note.ui.screen.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ara.note.home.R.string
import ara.note.test.BasePageObject
import ara.note.test.TestUtil
import ara.note.util.HDateTime
import ara.note.util.INVALID_NOTE_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.test.assertEquals

/**
 * Page Object for HomeScreen
 */
internal class HomePageObject(
    private val composeTestRule: ComposeContentTestRule,
) : BasePageObject() {

    private val uiState = MutableStateFlow(
        HomeState(
            notes = TestUtil.tNoteEntityList,
            notebooks = TestUtil.tNotebookEntityList,
            currentNotebookId = TestUtil.tNotebookEntityList.first().id,
        ),
    )
    private val singleEvent = mutableListOf<HomeSingleEvent>()

    private var noteIdToNavigate: Int? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun setUp() {
        val filterNotes = {
            uiState.update { it.copy(notes = TestUtil.tNoteEntityList.filter { it.notebookId == uiState.value.currentNotebookId }) }
        }
        filterNotes()
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState.collectAsState().value,
                navigateToNoteDetailScreen = { noteId ->
                    noteIdToNavigate = noteId
                },
                navigateToSettingsScreen = {},
                navigateToNotebooksScreen = {},
                setCurrentNotebookId = { notebookId ->
                    uiState.update { it.copy(currentNotebookId = notebookId) }
                    filterNotes()
                },
                modifySearchText = {},
            )
        }
    }

    private fun findNoteCardText(text: String) = composeTestRule.onNodeWithText(text)

    private fun findNoteCardModifiedTime(text: String) = composeTestRule.onNodeWithText(text)

    private fun findNotebookNameInAppbar() =
        composeTestRule.onNode(
            !SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton) and
                hasText(uiState.value.notebooks.find { it.id == uiState.value.currentNotebookId }!!.name),
        )

    private fun findNotebookNameInDrawer(text: String) =
        composeTestRule.onNode(
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton) and
                hasText(text),
        )

    private fun findFab() = composeTestRule.onNodeWithContentDescription(getString(string.cd_add_note))

    private fun findMenuButton() = composeTestRule.onNodeWithContentDescription(getString(ara.note.ui.R.string.cd_appbar_menu))

    fun clickFab() = findFab().performClick()

    fun clickFirstNoteCard() = findNoteCardText(uiState.value.notes.first().text).performClick()

    fun clickMenuButton() = findMenuButton().performClick()

    fun clickOnLastNotebookInDrawer() = findNotebookNameInDrawer(uiState.value.notebooks.last().name).performClick()

    fun assertAllNotesOfCurrentNotebookAreDisplaying() {
        for (item in uiState.value.notes) {
            findNoteCardText(item.text).assertIsDisplayed()
            findNoteCardModifiedTime(HDateTime.gerPrettyDateTime(item.modifiedDateTime)).assertIsDisplayed()
        }
    }

    fun assertNotebookNameInAppbarIsDisplaying() = findNotebookNameInAppbar().assertIsDisplayed()

    fun assertAllNotebooksInDrawerAreDisplaying() {
        for (item in uiState.value.notebooks)
            findNotebookNameInDrawer(item.name).assertIsDisplayed()
    }

    fun assertNavigateToNodeDetailScreenWithNewNoteId() =
        assertEquals(noteIdToNavigate, INVALID_NOTE_ID)

    fun assertNavigateToNodeDetailScreenWithItsNoteId() =
        assertEquals(noteIdToNavigate, uiState.value.notes.first().id)

    fun assertCurrentNotebookIdIsTheLastOne() =
        assertEquals(uiState.value.currentNotebookId, uiState.value.notebooks.last().id)
}
