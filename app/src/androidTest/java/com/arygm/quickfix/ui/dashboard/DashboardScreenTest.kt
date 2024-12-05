package com.arygm.quickfix.ui.dashboard

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import com.arygm.quickfix.model.account.AccountRepositoryFirestore
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.messaging.ChatRepositoryFirestore
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.quickfix.QuickFixRepositoryFirestore
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class DashboardScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var quickFixViewModel: QuickFixViewModel
  private lateinit var quickFixRepositoryFirestore: QuickFixRepositoryFirestore
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var accountRepositoryFirestore: AccountRepositoryFirestore
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var chatRepositoryFirestore: ChatRepositoryFirestore
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var profileRepositoryFirestore: WorkerProfileRepositoryFirestore

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    quickFixRepositoryFirestore = mock(QuickFixRepositoryFirestore::class.java)
    quickFixViewModel = QuickFixViewModel(quickFixRepositoryFirestore)
    accountRepositoryFirestore = mock(AccountRepositoryFirestore::class.java)
    accountViewModel = AccountViewModel(accountRepositoryFirestore)
    chatRepositoryFirestore = mock(ChatRepositoryFirestore::class.java)
    chatViewModel = ChatViewModel(chatRepositoryFirestore)
    profileRepositoryFirestore = mock(WorkerProfileRepositoryFirestore::class.java)
    profileViewModel = ProfileViewModel(profileRepositoryFirestore)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.DASHBOARD)
    composeTestRule.setContent {
      DashboardScreen(
          true,
          navigationActions,
          quickFixViewModel,
          chatViewModel,
          accountViewModel,
          profileViewModel)
    }
  }

  @Test
  fun dashboardDisplaysQuickFixFilterButtons() {

    // Verify that the QuickFix filter buttons are displayed
    composeTestRule.onNodeWithText("All").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LazyRowTag").performScrollToIndex(1)
    composeTestRule.onNodeWithText("Upcoming").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LazyRowTag").performScrollToIndex(2)
    composeTestRule.onNodeWithText("Paid").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LazyRowTag").performScrollToIndex(3)
    composeTestRule.onNodeWithText("Unpaid").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LazyRowTag").performScrollToIndex(4)
    composeTestRule.onNodeWithText("Pending").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LazyRowTag").performScrollToIndex(5)
    composeTestRule.onNodeWithText("Completed").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LazyRowTag").performScrollToIndex(6)
    composeTestRule.onNodeWithText("Canceled").assertIsDisplayed()
  }

  @Test
  fun quickFixFilterButtonsToggleCorrectly() {
    // Verify that clicking "Canceled" selects it and deselects "Upcoming"
    composeTestRule.onNodeWithText("All").performClick()
    composeTestRule.onNodeWithText("All").assertHasClickAction()
    composeTestRule.onNodeWithText("All").assertExists()

    composeTestRule.onNodeWithText("Upcoming").assertHasClickAction()
    composeTestRule.onNodeWithText("Upcoming").assertExists()
  }

  @Test
  fun quickFixesWidgetDisplaysCorrectly() {
    // Verify that the QuickFixesWidget is displayed by default
    composeTestRule.onNodeWithTag("UPCOMINGQuickFixes").assertIsDisplayed()

    // Verify that the widget updates based on the selected filter

    composeTestRule.onNodeWithTag("LazyRowTag").performScrollToIndex(6)
    composeTestRule.onNodeWithText("Canceled").performClick()
    composeTestRule.onNodeWithTag("CANCELEDQuickFixes").assertIsDisplayed()
  }

  @Test
  fun messagesWidgetDisplaysCorrectly() {
    // Verify that the MessagesWidget is displayed
    composeTestRule.onNodeWithTag("MessagesWidget").assertIsDisplayed()
  }
}
