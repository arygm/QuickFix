package com.arygm.quickfix.ui.dashboard

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.bill.BillField
import com.arygm.quickfix.model.bill.Units
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.messaging.ChatRepository
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepositoryDataStore
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixRepository
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.model.search.AnnouncementRepository
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
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
  }

  @Test
  fun dashboardDisplaysQuickFixFilterButtons() {
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
    // Verify that the QuickFixesWidget is displayed by default
    composeTestRule.onNodeWithTag("AllQuickFixes").assertIsDisplayed()

    // Verify that the widget updates based on the selected filter
    composeTestRule.onNodeWithText("Pending").performClick()
    composeTestRule.onNodeWithTag("PendingQuickFixes").assertIsDisplayed()
  }

  @Test
  fun messagesWidgetDisplaysCorrectly() {
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
    // Verify that the MessagesWidget is displayed
    composeTestRule.onNodeWithTag("MessagesWidget").assertIsDisplayed()
  }

  @Test
  fun billsWidgetDisplaysCorrectly() {
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
    // Verify that the BillsWidget is displayed
    composeTestRule.onNodeWithTag("BillsWidget").assertIsDisplayed()
  }

  @Test
  fun announcementsWidgetDisplaysCorrectly() {
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
    // Verify that the AnnouncementsWidget is displayed
    composeTestRule.onNodeWithTag("AnnouncementsWidget").assertIsDisplayed()
  }

  @Test
  fun dashboardLaunchedEffectInitializesCorrectly() {
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
    // Verify user profile fetch
    verify(profileRepository).getProfileById(eq("testUserId"), any(), any())
  }

  @Test
  fun quickFixFilterButtonsUpdateStateCorrectly() {
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
    composeTestRule.onNodeWithText("All").performClick()
    composeTestRule.onNodeWithText("Pending").performClick()
    composeTestRule.onNodeWithText("Unpaid").performClick()

    // Assert only "Unpaid" is selected
    composeTestRule.onNodeWithText("Unpaid").assertIsEnabled()
    composeTestRule.onNodeWithText("All").assertIsNotFocused()
  }

  @Test
  fun quickFixesWidgetDisplaysCorrectQuickFixesBasedOnFilter() {
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
    composeTestRule.onNodeWithText("Pending").performClick()
    composeTestRule.onNodeWithTag("PendingQuickFixes").assertExists()
    composeTestRule.onNodeWithTag("UnpaidQuickFixes").assertDoesNotExist()

    composeTestRule.onNodeWithText("Paid").performClick()
    composeTestRule.onNodeWithTag("PaidQuickFixes").assertExists()
  }

  @Test
  fun chatWidgetFiltersChatsBasedOnMode() {
    // Mock chats for USER mode
    val userChats = MutableStateFlow(listOf(Chat(workeruid = "worker1", useruid = "testUserId")))
    runBlocking {
      doAnswer { invocationOnMock ->
            val onSuccess = invocationOnMock.arguments[1] as (List<Chat>) -> Unit
            onSuccess(userChats.value)
          }
          .whenever(chatViewRepository)
          .getChatByChatUid(any(), any(), any())
    }

    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (Account?) -> Unit
          onSuccess(
              Account(
                  uid = "worker1",
                  firstName = "worker1",
                  lastName = "worker1",
                  email = "",
                  birthDate = Timestamp.now()))
        }
        .whenever(accountRepository)
        .getAccountById(any(), any(), any())

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
    composeTestRule.onNodeWithTag("MessagesWidget").assertExists()
  }

  @Test
  fun announcementsWidgetDisplaysForUserMode() {
    appModeFlow.value = "USER"
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

    composeTestRule.onNodeWithTag("AnnouncementsWidget").assertIsDisplayed()
  }

  @Test
  fun navigationTriggersOnQuickFixItemClickPending() {
    // Mock the profile repository to return a profile with a quick fix ID
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (Profile?) -> Unit
          onSuccess(WorkerProfile(uid = "testUserId", quickFixes = listOf("quickFix1")))
          null
        }
        .whenever(profileRepository)
        .getProfileById(any(), any(), any())

    // Mock the quick fix repository to return a QuickFix object
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (QuickFix?) -> Unit
          onSuccess(QuickFix(uid = "quickFix1", status = Status.PENDING, title = "QuickFix Title"))
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixById(any(), any(), any())
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

    // Simulate clicking the "Pending" filter button
    composeTestRule.onNodeWithText("Pending").performClick()

    // Wait for the UI to display the QuickFix item
    composeTestRule.waitUntil {
      composeTestRule.onAllNodesWithText("QuickFix Title").fetchSemanticsNodes().isNotEmpty()
    }

    // Click on the displayed QuickFix
    composeTestRule.onNodeWithText("QuickFix Title").performClick()

    // Verify that the navigation action was triggered
    verify(navigationActions).navigateTo(UserScreen.QUICKFIX_ONBOARDING)
  }

  @Test
  fun navigationTriggersOnQuickFixItemClickUnpaid() {
    // Mock the profile repository to return a profile with a quick fix ID
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (Profile?) -> Unit
          onSuccess(WorkerProfile(uid = "testUserId", quickFixes = listOf("quickFix1")))
          null
        }
        .whenever(profileRepository)
        .getProfileById(any(), any(), any())

    // Mock the quick fix repository to return a QuickFix object
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (QuickFix?) -> Unit
          onSuccess(QuickFix(uid = "quickFix1", status = Status.UNPAID, title = "QuickFix Title"))
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixById(any(), any(), any())
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

    // Simulate clicking the "Pending" filter button
    composeTestRule.onNodeWithText("Unpaid").performClick()

    // Wait for the UI to display the QuickFix item
    composeTestRule.waitUntil {
      composeTestRule.onAllNodesWithText("QuickFix Title").fetchSemanticsNodes().isNotEmpty()
    }

    // Click on the displayed QuickFix
    composeTestRule.onNodeWithText("QuickFix Title").performClick()

    // Verify that the navigation action was triggered
    verify(navigationActions).navigateTo(UserScreen.QUICKFIX_ONBOARDING)
  }

  @Test
  fun navigationTriggersOnQuickFixItemClickAll() {
    // Mock the profile repository to return a profile with a quick fix ID
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (Profile?) -> Unit
          onSuccess(WorkerProfile(uid = "testUserId", quickFixes = listOf("quickFix1")))
          null
        }
        .whenever(profileRepository)
        .getProfileById(any(), any(), any())

    // Mock the quick fix repository to return a QuickFix object
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (QuickFix?) -> Unit
          onSuccess(QuickFix(uid = "quickFix1", status = Status.PENDING, title = "QuickFix Title"))
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixById(any(), any(), any())
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

    // Simulate clicking the "Pending" filter button
    composeTestRule.onNodeWithText("All").performClick()

    // Wait for the UI to display the QuickFix item
    composeTestRule.waitUntil {
      composeTestRule.onAllNodesWithText("QuickFix Title").fetchSemanticsNodes().isNotEmpty()
    }

    // Click on the displayed QuickFix
    composeTestRule.onNodeWithText("QuickFix Title").performClick()

    // Verify that the navigation action was triggered
    verify(navigationActions).navigateTo(UserScreen.QUICKFIX_ONBOARDING)
  }

  @Test
  fun navigationTriggersOnQuickFixItemClickPaid() {
    // Mock the profile repository to return a profile with a quick fix ID
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (Profile?) -> Unit
          onSuccess(WorkerProfile(uid = "testUserId", quickFixes = listOf("quickFix1")))
          null
        }
        .whenever(profileRepository)
        .getProfileById(any(), any(), any())

    // Mock the quick fix repository to return a QuickFix object
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (QuickFix?) -> Unit
          onSuccess(QuickFix(uid = "quickFix1", status = Status.PAID, title = "QuickFix Title"))
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixById(any(), any(), any())
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

    // Simulate clicking the "Pending" filter button
    composeTestRule.onNodeWithText("Paid").performClick()

    // Wait for the UI to display the QuickFix item
    composeTestRule.waitUntil {
      composeTestRule.onAllNodesWithText("QuickFix Title").fetchSemanticsNodes().isNotEmpty()
    }

    // Click on the displayed QuickFix
    composeTestRule.onNodeWithText("QuickFix Title").performClick()

    // Verify that the navigation action was triggered
    verify(navigationActions).navigateTo(UserScreen.QUICKFIX_ONBOARDING)
  }

  @Test
  fun navigationTriggersOnQuickFixItemClickUpcoming() {
    // Mock the profile repository to return a profile with a quick fix ID
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (Profile?) -> Unit
          onSuccess(WorkerProfile(uid = "testUserId", quickFixes = listOf("quickFix1")))
          null
        }
        .whenever(profileRepository)
        .getProfileById(any(), any(), any())

    // Mock the quick fix repository to return a QuickFix object
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (QuickFix?) -> Unit
          onSuccess(QuickFix(uid = "quickFix1", status = Status.UPCOMING, title = "QuickFix Title"))
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixById(any(), any(), any())
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

    composeTestRule.onNodeWithTag("QuickFixFilterButtons").performScrollToIndex(4)
    // Simulate clicking the "Pending" filter button
    composeTestRule.onNodeWithText("Upcoming").performClick()

    // Wait for the UI to display the QuickFix item
    composeTestRule.waitUntil {
      composeTestRule.onAllNodesWithText("QuickFix Title").fetchSemanticsNodes().isNotEmpty()
    }

    // Click on the displayed QuickFix
    composeTestRule.onNodeWithText("QuickFix Title").performClick()

    // Verify that the navigation action was triggered
    verify(navigationActions).navigateTo(UserScreen.QUICKFIX_ONBOARDING)
  }

  @Test
  fun navigationTriggersOnQuickFixItemClickCompleted() {
    // Mock the profile repository to return a profile with a quick fix ID
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (Profile?) -> Unit
          onSuccess(WorkerProfile(uid = "testUserId", quickFixes = listOf("quickFix1")))
          null
        }
        .whenever(profileRepository)
        .getProfileById(any(), any(), any())

    // Mock the quick fix repository to return a QuickFix object
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (QuickFix?) -> Unit
          onSuccess(
              QuickFix(uid = "quickFix1", status = Status.COMPLETED, title = "QuickFix Title"))
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixById(any(), any(), any())
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

    composeTestRule.onNodeWithTag("QuickFixFilterButtons").performScrollToIndex(5)
    // Simulate clicking the "Pending" filter button
    composeTestRule.onNodeWithText("Completed").performClick()

    // Wait for the UI to display the QuickFix item
    composeTestRule.waitUntil {
      composeTestRule.onAllNodesWithText("QuickFix Title").fetchSemanticsNodes().isNotEmpty()
    }

    // Click on the displayed QuickFix
    composeTestRule.onNodeWithText("QuickFix Title").performClick()

    // Verify that the navigation action was triggered
    verify(navigationActions).navigateTo(UserScreen.QUICKFIX_ONBOARDING)
  }

  @Test
  fun navigationTriggersOnQuickFixItemClickCanceled() {
    // Mock the profile repository to return a profile with a quick fix ID
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (Profile?) -> Unit
          onSuccess(WorkerProfile(uid = "testUserId", quickFixes = listOf("quickFix1")))
          null
        }
        .whenever(profileRepository)
        .getProfileById(any(), any(), any())

    // Mock the quick fix repository to return a QuickFix object
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (QuickFix?) -> Unit
          onSuccess(QuickFix(uid = "quickFix1", status = Status.CANCELED, title = "QuickFix Title"))
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixById(any(), any(), any())
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

    composeTestRule.onNodeWithTag("QuickFixFilterButtons").performScrollToIndex(6)
    // Simulate clicking the "Pending" filter button
    composeTestRule.onNodeWithText("Canceled").performClick()

    // Wait for the UI to display the QuickFix item
    composeTestRule.waitUntil {
      composeTestRule.onAllNodesWithText("QuickFix Title").fetchSemanticsNodes().isNotEmpty()
    }

    // Click on the displayed QuickFix
    composeTestRule.onNodeWithText("QuickFix Title").performClick()

    // Verify that the navigation action was triggered
    verify(navigationActions).navigateTo(UserScreen.QUICKFIX_ONBOARDING)
  }

  @Test
  fun navigationTriggersOnBillItemClick() {
    // Mock the profile repository to return a profile with a quick fix ID
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (Profile?) -> Unit
          onSuccess(WorkerProfile(uid = "testUserId", quickFixes = listOf("quickFix1")))
          null
        }
        .whenever(profileRepository)
        .getProfileById(any(), any(), any())

    // Mock the quick fix repository to return a QuickFix object
    doAnswer { invocationOnMock ->
          val onSuccess = invocationOnMock.arguments[1] as (QuickFix?) -> Unit
          onSuccess(
              QuickFix(
                  uid = "quickFix1",
                  status = Status.CANCELED,
                  title = "QuickFix Title",
                  bill =
                      listOf(
                          BillField("Bill 1", Units.M2, 4.0, 25.0),
                      )))
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixById(any(), any(), any())
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

    // Wait for the UI to display the QuickFix item
    composeTestRule.waitUntil {
      composeTestRule.onAllNodesWithText("QuickFix Title").fetchSemanticsNodes().isNotEmpty()
    }

    // Click on the displayed QuickFix
    composeTestRule.onNodeWithText("$00.00").performClick()

    // Verify that the navigation action was triggered
    verify(navigationActions).navigateTo(UserScreen.QUICKFIX_ONBOARDING)
  }
}
