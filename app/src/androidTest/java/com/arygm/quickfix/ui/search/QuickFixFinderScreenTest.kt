package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.navigation.TopLevelDestinations
import com.arygm.quickfix.ui.navigation.getBottomBarId
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class QuickFixFinderScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var navigationActionsRoot: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.SEARCH)
    navigationActionsRoot = mock(NavigationActions::class.java)
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun quickFixFinderScreenUserDisplaysCorrectly() {
    composeTestRule.setContent {
      QuickFixFinderScreen(navigationActions, navigationActionsRoot, isUser = true)
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
      QuickFixFinderScreen(navigationActions, navigationActionsRoot, isUser = false)
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
      QuickFixFinderScreen(navigationActions, navigationActionsRoot, isUser = true)
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
      QuickFixFinderScreen(navigationActions, navigationActionsRoot, isUser = true)
    }

    // Click the "Cancel" button
    composeTestRule.onNodeWithText("Cancel").performClick()

    // Verify that the navigation action was triggered to the home screen
    verify(navigationActionsRoot).navigateTo(TopLevelDestinations.HOME)

    // As the bottom bar get updated only if the currentRoute is updated check that it has the right
    // value
    assertEquals(1, getBottomBarId(Route.HOME, true))
  }
}
