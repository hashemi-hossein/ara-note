package ara.note.ui.screen.notedetail

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NoteDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var pageObject: NoteDetailPageObject

    @Before
    fun setUp() {
        pageObject = NoteDetailPageObject(composeTestRule).apply { setUp() }
    }

    @Test
    fun textField_textEntering_whenNewNote() {
        with(pageObject) {
            // given
            setIsNewNote(isNewNote = true)
            val testText = "hello"

            // when
            typeIntoTextField(testText)

            // then
            textIsDisplayingInTextField(testText)
        }
    }

    @Test
    fun backButton_triggerSaveWhenAutoSave() {
        with(pageObject) {
            // given
            setAutoSaveMode(on = true)

            // when
            clickOnBackButton()

            // then
            assertSaveTriggered()
        }
    }

    @Test
    fun backButton_triggerDiscardWhenAutoSaveIsOff() {
        with(pageObject) {
            // given
            setAutoSaveMode(on = false)

            // when
            clickOnBackButton()

            // then
            assertDiscardTriggered()
        }
    }

    @Test
    fun deleteButton_triggerDeleteWhenIsNotNewNote() {
        with(pageObject) {
            // given
            setIsNewNote(isNewNote = true)

            // when
            clickOnDeleteButton()

            // then
            assertDiscardTriggered()
        }
    }

    @Test
    fun deleteButton_triggerDiscardWhenIsNewNote() {
        with(pageObject) {
            // given
            setIsNewNote(isNewNote = false)

            // when
            clickOnDeleteButton()

            // then
            assertDeleteTriggered()
        }
    }

    @Test
    fun notebookDropdown_showAllWhenClick() {
        with(pageObject) {
            // when
            clickOnNotebookDropdown()

            // then
            assertAllNotebooksAreDisplaying()
        }
    }

    @Test
    fun notebookDropdown_changeNotebookOnClick() {
        with(pageObject) {
            // when
            clickOnNotebookDropdown()
            clickOnLastDropdownItem()

            // then
            assertLastNotebookIsDisplaying()
        }
    }
}
