package ara.note.ui.screen.home

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var pageObject: HomePageObject

    @Before
    fun setUp() {
        pageObject = HomePageObject(composeTestRule).apply { setUp() }
    }

    @Test
    fun idle_noteCardsOfCurrentNotebookAndNotebookNameAreDisplaying() {
        with(pageObject) {
            // then
            assertAllNotesOfCurrentNotebookAreDisplaying()
            assertNotebookNameInAppbarIsDisplaying()
        }
    }

    @Test
    fun fab_click_navigateToNoteDetailWithNewNoteId() {
        with(pageObject) {
            // when
            clickFab()

            // then
            assertNavigateToNodeDetailScreenWithNewNoteId()
        }
    }

    @Test
    fun noteCard_click_navigateToNoteDetailWithItsNoteId() {
        with(pageObject) {
            // when
            clickFirstNoteCard()

            // then
            assertNavigateToNodeDetailScreenWithItsNoteId()
        }
    }

    @Test
    fun drawer_openWhenClickingMenuButton() {
        with(pageObject) {
            // when
            clickMenuButton()

            // then
            assertAllNotebooksInDrawerAreDisplaying()
        }
    }


    @Test
    fun drawer_changeCurrentNotebookWhenClickingOnLastNotebook() {
        with(pageObject) {
            // given
            clickMenuButton()

            // when
            clickOnLastNotebookInDrawer()

            // then
            assertCurrentNotebookIdIsTheLastOne()
            assertAllNotesOfCurrentNotebookAreDisplaying()
        }
    }
}
