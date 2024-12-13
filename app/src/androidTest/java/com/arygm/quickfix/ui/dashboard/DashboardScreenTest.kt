package com.arygm.quickfix.ui.dashboard

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.messaging.ChatRepository
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepositoryDataStore
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.quickfix.QuickFixRepository
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.search.AnnouncementRepository
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.dashboard.DashboardScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever

class DashboardScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  // Class-level MutableStateFlows for userId and appMode
  private val userIdFlow = MutableStateFlow("testUserId")
  private val appModeFlow = MutableStateFlow("USER")

  private lateinit var navigationActions: NavigationActions
  private lateinit var preferencesRepositoryDataStore: PreferencesRepositoryDataStore
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var quickFixRepository: QuickFixRepository
  private lateinit var quickFixViewModel: QuickFixViewModel
  private lateinit var profileRepository: ProfileRepository
  private lateinit var userViewModel: ProfileViewModel
  private lateinit var workerViewModel: ProfileViewModel
  private lateinit var chatViewRepository: ChatRepository
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var accountRepository: AccountRepository
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var announcementRepository: AnnouncementRepository
  private lateinit var announcementViewModel: AnnouncementViewModel
  private lateinit var categoryRepositoryFirestore: CategoryRepositoryFirestore
  private lateinit var categoryViewModel: CategoryViewModel

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(UserScreen.DASHBOARD)

    preferencesRepositoryDataStore = mock(PreferencesRepositoryDataStore::class.java)
    preferencesViewModel = PreferencesViewModel(preferencesRepositoryDataStore)
    quickFixRepository = mock(QuickFixRepository::class.java)
    quickFixViewModel = QuickFixViewModel(quickFixRepository)
    profileRepository = mock(ProfileRepository::class.java)
    userViewModel = ProfileViewModel(profileRepository)
    workerViewModel = ProfileViewModel(profileRepository)
    chatViewRepository = mock(ChatRepository::class.java)
    chatViewModel = ChatViewModel(chatViewRepository)
    accountRepository = mock(AccountRepository::class.java)
    accountViewModel = AccountViewModel(accountRepository)
    announcementRepository = mock(AnnouncementRepository::class.java)
    announcementViewModel =
        AnnouncementViewModel(
            announcementRepository, preferencesRepositoryDataStore, profileRepository)
    categoryRepositoryFirestore = mock(CategoryRepositoryFirestore::class.java)
    categoryViewModel = CategoryViewModel(categoryRepositoryFirestore)

    // Mock getPreferenceByKey for user_id
    val userIdKey = stringPreferencesKey("user_id")
    whenever(preferencesRepositoryDataStore.getPreferenceByKey(userIdKey)).thenReturn(userIdFlow)

    // Mock getPreferenceByKey for app_mode
    val appModeKey = stringPreferencesKey("app_mode")
    whenever(preferencesRepositoryDataStore.getPreferenceByKey(appModeKey)).thenReturn(appModeFlow)
    composeTestRule.setContent {
      DashboardScreen(
          navigationActions,
          userViewModel,
          workerViewModel,
          accountViewModel,
          quickFixViewModel,
          chatViewModel,
          preferencesViewModel,
          announcementViewModel,
          categoryViewModel)
    }
  }

  @Test
  fun dashboardDisplaysQuickFixFilterButtons() {
    // Verify that the QuickFix filter buttons are displayed
    composeTestRule.onNodeWithText("All").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuickFixFilterButtons").performScrollToIndex(1)
    composeTestRule.onNodeWithText("Pending").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuickFixFilterButtons").performScrollToIndex(2)
    composeTestRule.onNodeWithText("Unpaid").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuickFixFilterButtons").performScrollToIndex(3)
    composeTestRule.onNodeWithText("Paid").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuickFixFilterButtons").performScrollToIndex(4)
    composeTestRule.onNodeWithText("Upcoming").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuickFixFilterButtons").performScrollToIndex(5)
    composeTestRule.onNodeWithText("Completed").assertIsDisplayed().performScrollTo()
    composeTestRule.onNodeWithTag("QuickFixFilterButtons").performScrollToIndex(6)
    composeTestRule.onNodeWithText("Canceled").assertIsDisplayed()
  }

  @Test
  fun quickFixFilterButtonsToggleCorrectly() {
    // Verify that clicking "Pending" selects it and deselects "All"
    composeTestRule.onNodeWithText("All").assertIsDisplayed()
    composeTestRule.onNodeWithText("Pending").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("All").assertHasClickAction().assertIsNotFocused()
    composeTestRule.onNodeWithText("All").assertExists()
    composeTestRule.onNodeWithText("Unpaid").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Pending").assertHasClickAction().assertIsNotFocused()
    composeTestRule.onNodeWithText("Pending").assertExists()
    composeTestRule.onNodeWithText("Paid").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Unpaid").assertHasClickAction().assertIsNotFocused()
    composeTestRule.onNodeWithText("Unpaid").assertExists()
    composeTestRule.onNodeWithTag("QuickFixFilterButtons").performScrollToIndex(4)
    composeTestRule.onNodeWithText("Upcoming").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Paid").assertHasClickAction().assertIsNotFocused()
    composeTestRule.onNodeWithText("Paid").assertExists()
    composeTestRule.onNodeWithTag("QuickFixFilterButtons").performScrollToIndex(5)
    composeTestRule.onNodeWithText("Completed").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Upcoming").assertHasClickAction().assertIsNotFocused()
    composeTestRule.onNodeWithText("Upcoming").assertExists()
    composeTestRule.onNodeWithTag("QuickFixFilterButtons").performScrollToIndex(6)
    composeTestRule.onNodeWithText("Canceled").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Completed").assertHasClickAction().assertIsNotFocused()
    composeTestRule.onNodeWithText("Completed").assertExists()
  }

  @Test
  fun quickFixesWidgetDisplaysCorrectly() {
    // Verify that the QuickFixesWidget is displayed by default
    composeTestRule.onNodeWithTag("AllQuickFixes").assertIsDisplayed()

    // Verify that the widget updates based on the selected filter
    composeTestRule.onNodeWithText("Pending").performClick()
    composeTestRule.onNodeWithTag("PendingQuickFixes").assertIsDisplayed()
  }

  @Test
  fun messagesWidgetDisplaysCorrectly() {
    // Verify that the MessagesWidget is displayed
    composeTestRule.onNodeWithTag("MessagesWidget").assertIsDisplayed()
  }

  @Test
  fun billsWidgetDisplaysCorrectly() {
    // Verify that the BillsWidget is displayed
    composeTestRule.onNodeWithTag("BillsWidget").assertIsDisplayed()
  }

  @Test
  fun announcementsWidgetDisplaysCorrectly() {
    // Verify that the AnnouncementsWidget is displayed
    composeTestRule.onNodeWithTag("AnnouncementsWidget").assertIsDisplayed()
  }
}
