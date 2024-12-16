package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.model.account.AccountRepositoryFirestore
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserRoute
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserTopLevelDestinations
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.getBottomBarIdUser
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.QuickFixFinderScreen
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
  private lateinit var accountRepositoryFirestore: AccountRepositoryFirestore
  private lateinit var searchViewModel: SearchViewModel
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var announcementViewModel: AnnouncementViewModel
  private lateinit var categoryViewModel: CategoryViewModel
  private lateinit var loggedInAccountViewModel: LoggedInAccountViewModel
  private lateinit var quickFixViewModel: QuickFixViewModel
  private lateinit var userViewModel: ProfileViewModel

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(UserScreen.SEARCH)
    navigationActionsRoot = mock(NavigationActions::class.java)
    workerProfileRepo = mockk(relaxed = true)
    categoryRepo = mockk(relaxed = true)
    accountRepositoryFirestore = mock(AccountRepositoryFirestore::class.java)
    searchViewModel = SearchViewModel(workerProfileRepo)
    accountViewModel = AccountViewModel(mock())
    announcementViewModel = AnnouncementViewModel(mock())
    categoryViewModel = CategoryViewModel(categoryRepo)
    loggedInAccountViewModel = LoggedInAccountViewModel(mock(), mock())
    quickFixViewModel = QuickFixViewModel(mock())
    userViewModel = ProfileViewModel(mock())
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun quickFixFinderScreenUserDisplaysCorrectly() {
    composeTestRule.setContent {
      QuickFixFinderScreen(
          navigationActions,
          navigationActionsRoot,
          userViewModel = userViewModel,
          accountViewModel = accountViewModel,
          quickFixViewModel = quickFixViewModel,
          categoryViewModel = categoryViewModel,
          announcementViewModel = announcementViewModel,
          loggedInAccountViewModel = loggedInAccountViewModel,
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
          userViewModel = userViewModel,
          accountViewModel = accountViewModel,
          quickFixViewModel = quickFixViewModel,
          categoryViewModel = categoryViewModel,
          announcementViewModel = announcementViewModel,
          loggedInAccountViewModel = loggedInAccountViewModel,
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
  fun tabSelectionChangesPagerContent() {
    composeTestRule.setContent {
      QuickFixFinderScreen(
          navigationActions,
          navigationActionsRoot,
          userViewModel = userViewModel,
          accountViewModel = accountViewModel,
          quickFixViewModel = quickFixViewModel,
          categoryViewModel = categoryViewModel,
          announcementViewModel = announcementViewModel,
          loggedInAccountViewModel = loggedInAccountViewModel,
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
          userViewModel = userViewModel,
          accountViewModel = accountViewModel,
          quickFixViewModel = quickFixViewModel,
          categoryViewModel = categoryViewModel,
          announcementViewModel = announcementViewModel,
          loggedInAccountViewModel = loggedInAccountViewModel,
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
}
