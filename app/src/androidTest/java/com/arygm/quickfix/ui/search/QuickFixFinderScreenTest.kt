package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.datastore.preferences.core.Preferences
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.search.AnnouncementRepository
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.QuickFixFinderScreen
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

class QuickFixFinderScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var navigationActionsRoot: NavigationActions
  private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var announcementRepository: AnnouncementRepository
  private lateinit var userProfileRepository: ProfileRepository
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var announcementViewModel: AnnouncementViewModel
  private lateinit var workerProfileRepo: WorkerProfileRepositoryFirestore
  private lateinit var categoryRepo: CategoryRepositoryFirestore
  private lateinit var searchViewModel: SearchViewModel
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var categoryViewModel: CategoryViewModel
  private lateinit var quickFixViewModel: QuickFixViewModel

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    navigationActionsRoot = mock(NavigationActions::class.java)

    preferencesRepository = mock(PreferencesRepository::class.java)
    announcementRepository = mock(AnnouncementRepository::class.java)
    userProfileRepository = mock(ProfileRepository::class.java)
    val mockedPreferenceFlow = MutableStateFlow<Any?>(null)

    whenever(
            preferencesRepository.getPreferenceByKey(
                org.mockito.kotlin.any<Preferences.Key<Any>>()))
        .thenReturn(mockedPreferenceFlow)
    preferencesViewModel = PreferencesViewModel(preferencesRepository)
    announcementViewModel =
        AnnouncementViewModel(announcementRepository, preferencesRepository, userProfileRepository)

    profileViewModel = mock(ProfileViewModel::class.java)

    workerProfileRepo = mockk(relaxed = true)
    categoryRepo = mockk(relaxed = true)
    searchViewModel = SearchViewModel(workerProfileRepo)
    categoryViewModel = CategoryViewModel(categoryRepo)
    accountViewModel = mockk(relaxed = true)
    quickFixViewModel = QuickFixViewModel(mock())
  }

  @Test
  fun quickFixFinderScreenDisplaysCorrectly() {
    composeTestRule.setContent {
      QuickFixFinderScreen(
          navigationActions = navigationActions,
          navigationActionsRoot = navigationActionsRoot,
          isUser = true,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          searchViewModel = searchViewModel,
          announcementViewModel = announcementViewModel,
          categoryViewModel = categoryViewModel,
          preferencesViewModel = preferencesViewModel,
          quickFixViewModel = quickFixViewModel)
    }

    // Assert top bar is displayed
    composeTestRule.onNodeWithTag("QuickFixFinderTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuickFixFinderTopBarTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuickFixFinderTopBarTitle").assertTextEquals("Quickfix")

    // Assert main content is displayed
    composeTestRule.onNodeWithTag("QuickFixFinderContent").assertIsDisplayed()

    // Assert tab row is displayed with both tabs
    composeTestRule.onNodeWithTag("quickFixSearchTabRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tabSearch", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("tabTextSearch", useUnmergedTree = true)
        .assertTextEquals("Search")
    composeTestRule.onNodeWithTag("tabAnnounce", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("tabTextAnnounce", useUnmergedTree = true)
        .assertTextEquals("Announce")

    // Assert pager is displayed
    composeTestRule.onNodeWithTag("quickFixSearchPager").assertIsDisplayed()
  }

  @Test
  fun tabSelectionChangesPagerContent() {
    composeTestRule.setContent {
      QuickFixFinderScreen(
          navigationActions = navigationActions,
          navigationActionsRoot = navigationActionsRoot,
          isUser = true,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          searchViewModel = searchViewModel,
          announcementViewModel = announcementViewModel,
          categoryViewModel = categoryViewModel,
          preferencesViewModel = preferencesViewModel,
          quickFixViewModel = quickFixViewModel)
    }

    composeTestRule.waitForIdle()
    // Initially, SearchScreen should be displayed
    composeTestRule.onNodeWithTag("searchContent").assertExists()

    // Select the Announce tab and verify that AnnouncementScreen is displayed
    composeTestRule.onNodeWithTag("tabAnnounce").performClick()
    composeTestRule.onNodeWithTag("AnnouncementContent").assertIsDisplayed()
  }

  @Test
  fun clickingAnnounceTabLoadsAnnouncementScreen() {
    composeTestRule.setContent {
      QuickFixFinderScreen(
          navigationActions = navigationActions,
          navigationActionsRoot = navigationActionsRoot,
          isUser = true,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          searchViewModel = searchViewModel,
          announcementViewModel = announcementViewModel,
          categoryViewModel = categoryViewModel,
          preferencesViewModel = preferencesViewModel,
          quickFixViewModel = quickFixViewModel)
    }

    // Click on the "Announce" tab
    composeTestRule.onNodeWithTag("tabAnnounce").performClick()

    // Verify that AnnouncementScreen content is loaded
    composeTestRule.onNodeWithTag("announcementButton").assertIsDisplayed()
  }

  @Test
  fun clickingSearchTabLoadsSearchScreen() {
    composeTestRule.setContent {
      QuickFixFinderScreen(
          navigationActions = navigationActions,
          navigationActionsRoot = navigationActionsRoot,
          isUser = true,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          searchViewModel = searchViewModel,
          announcementViewModel = announcementViewModel,
          categoryViewModel = categoryViewModel,
          preferencesViewModel = preferencesViewModel,
          quickFixViewModel = quickFixViewModel)
    }

    // Click on the "Search" tab
    composeTestRule.onNodeWithTag("tabSearch").performClick()

    // Verify that SearchScreen content is loaded
    composeTestRule.onNodeWithTag("searchContent").assertExists()
  }
}
