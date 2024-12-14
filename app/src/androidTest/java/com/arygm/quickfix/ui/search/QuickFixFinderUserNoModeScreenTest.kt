package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.QuickFixFinderScreen
import com.arygm.quickfix.ui.userModeUI.navigation.UserRoute
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.userModeUI.navigation.UserTopLevelDestinations
import com.arygm.quickfix.ui.userModeUI.navigation.getBottomBarIdUser
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class QuickFixFinderUserNoModeScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var navigationActionsRoot: NavigationActions
  private lateinit var workerProfileRepo: WorkerProfileRepositoryFirestore
  private lateinit var categoryRepo: CategoryRepositoryFirestore
  private lateinit var searchViewModel: SearchViewModel

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(UserScreen.SEARCH)
    navigationActionsRoot = mock(NavigationActions::class.java)
    workerProfileRepo = mockk(relaxed = true)
    categoryRepo = mockk(relaxed = true)
    searchViewModel = SearchViewModel(workerProfileRepo)
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun quickFixFinderScreenUserDisplaysCorrectly() {
    composeTestRule.setContent {
      QuickFixFinderScreen(
          navigationActions,
          navigationActionsRoot,
          isUser = true,
          searchViewModel = searchViewModel)
    }

    // Assert top bar is displayed
    composeTestRule.onNodeWithTag("QuickFixFinderTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuickFixFinderTopBarTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuickFixFinderTopBarTitle").assertTextEquals("Quickfix")

    // Assert main content is displayed
    composeTestRule.onNodeWithTag("QuickFixFinderContent").assertIsDisplayed()

    // Assert tab row is displayed with both tabs
    composeTestRule.onNodeWithTag("quickFixSearchTabRow").assertIsDisplayed()
    var title = "Search"
    composeTestRule.onNodeWithTag("tab$title", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("tabText$title", useUnmergedTree = true)
        .assertTextEquals("Search")
    title = "Announce"
    composeTestRule.onNodeWithTag("tab$title", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("tabText$title", useUnmergedTree = true)
        .assertTextEquals("Announce")

    // Assert pager is displayed and verify that the first page (SearchScreen) is shown
    composeTestRule.onNodeWithTag("quickFixSearchPager").assertIsDisplayed()
  }

  @Test
  fun quickFixFinderScreenWorkerDisplaysCorrectly() {
    composeTestRule.setContent {
      QuickFixFinderScreen(
          navigationActions,
          navigationActionsRoot,
          isUser = false,
          searchViewModel = searchViewModel)
    }

    // Assert top bar is displayed
    composeTestRule.onNodeWithTag("QuickFixFinderTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuickFixFinderTopBarTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuickFixFinderTopBarTitle").assertTextEquals("Quickfix")

    // Assert main content is displayed
    composeTestRule.onNodeWithTag("QuickFixFinderContent").assertIsDisplayed()

    // Assert tab row is displayed with both tabs
    composeTestRule.onNodeWithTag("quickFixSearchTabRow").assertIsDisplayed()
    var title = "Search"
    composeTestRule.onNodeWithTag("tab$title", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("tabText$title", useUnmergedTree = true)
        .assertTextEquals("Search")
    title = "Announce"
    composeTestRule.onNodeWithTag("tab$title", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("tabText$title", useUnmergedTree = true)
        .assertTextEquals("Announce")

    // Assert pager is displayed and verify that the first page (SearchScreen) is shown
    composeTestRule.onNodeWithTag("quickFixSearchPager").assertIsDisplayed()
  }

  @Test
  fun tabSelectionChangesPagerContent() {
    composeTestRule.setContent {
      QuickFixFinderScreen(
          navigationActions,
          navigationActionsRoot,
          isUser = true,
          searchViewModel = searchViewModel)
    }

    composeTestRule.waitForIdle()
    // Initially, SearchScreen should be displayed
    composeTestRule.onNodeWithTag("searchContent").assertExists()

    // Select the Announce tab and verify that AnnouncementScreen is displayed
    val title = "Announce"
    composeTestRule.onNodeWithTag("tab$title").performClick()
    composeTestRule.onNodeWithTag("AnnouncementContent").assertIsDisplayed()
  }

  @Test
  fun cancelButtonNavigatesToHomeAndUpdatesBottomBar() {
    composeTestRule.setContent {
      QuickFixFinderScreen(
          navigationActions,
          navigationActionsRoot,
          isUser = true,
          searchViewModel = searchViewModel)
    }

    // Click the "Cancel" button
    composeTestRule.onNodeWithText("Cancel").performClick()

    // Verify that the navigation action was triggered to the home screen
    verify(navigationActionsRoot).navigateTo(UserTopLevelDestinations.HOME)

    // As the bottom bar get updated only if the currentRoute is updated check that it has the right
    // value
    assertEquals(1, getBottomBarIdUser(UserRoute.HOME))
  }

  @Test
  fun tabSelectionUpdatesUIStateCorrectly() {
    composeTestRule.setContent {
      QuickFixFinderScreen(
          navigationActions,
          navigationActionsRoot,
          isUser = true,
          searchViewModel = searchViewModel)
    }

    // Verify initial state
    composeTestRule.onNodeWithTag("tabSearch").assertExists().assertIsDisplayed()

    // Switch to Announce tab
    composeTestRule.onNodeWithTag("tabAnnounce").performClick()

    // Verify AnnouncementScreen is displayed
    composeTestRule.onNodeWithTag("AnnouncementContent").assertIsDisplayed()

    // Switch back to Search tab
    composeTestRule.onNodeWithTag("tabSearch").performClick()

    // Verify SearchOnBoarding is displayed
    composeTestRule.onNodeWithTag("searchContent").assertExists()
  }

  @Test
  fun quickFixFinderScreen_searchHidesPagerAndTabRow() {
    composeTestRule.setContent {
      QuickFixFinderScreen(
          navigationActions,
          navigationActionsRoot,
          isUser = true,
          searchViewModel = searchViewModel)
    }

    // Initially, pager and tabs are visible (Search tab is shown)
    composeTestRule.onNodeWithTag("quickFixSearchTabRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchContent").assertIsDisplayed()

    // Perform a search that triggers onSearch callback and sets pager = false
    composeTestRule.onNodeWithTag("searchContent").performTextInput("Painter")

    // After performing a search, pager is false, so TabRow should no longer be visible
    // Since we know that onSearch sets pager to false, we check if tab row disappears
    composeTestRule.onNodeWithTag("quickFixSearchTabRow").assertDoesNotExist()
  }

  @Test
  fun quickFixFinderScreen_emptySearchShowsPagerAndTabRow() {
    composeTestRule.setContent {
      QuickFixFinderScreen(
          navigationActions,
          navigationActionsRoot,
          isUser = true,
          searchViewModel = searchViewModel)
    }

    // Perform a search first
    composeTestRule.onNodeWithTag("searchContent").performTextInput("Painter")
    composeTestRule.waitForIdle()
    // Tab row should be hidden now
    composeTestRule.onNodeWithTag("quickFixSearchTabRow").assertDoesNotExist()

    // Clear the search input to trigger onSearchEmpty, setting pager = true again
    composeTestRule.onNodeWithTag("clearSearchIcon", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()

    // Now tab row should be visible again
    composeTestRule.onNodeWithTag("quickFixSearchTabRow").assertIsDisplayed()
  }
}
