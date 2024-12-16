package com.arygm.quickfix.ui.dashboard

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.dashboard.DashboardScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class DashboardScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var announcementViewModel: AnnouncementViewModel

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    announcementViewModel = mock(AnnouncementViewModel::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(UserScreen.DASHBOARD)
  }

  @Test
  fun dashboardDisplaysContentContainer() {
    composeTestRule.setContent {
      DashboardScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          isUser = true)
    }

    // Verify main container is displayed
    composeTestRule.onNodeWithTag("DashboardContent").assertIsDisplayed()
  }

  @Test
  fun dashboardDisplaysQuickFixFilterButtons() {
    composeTestRule.setContent {
      DashboardScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          isUser = true)
    }

    // Verify that all QuickFix filter buttons are displayed
    composeTestRule.onNodeWithText("All").assertIsDisplayed()
    composeTestRule.onNodeWithText("Upcoming").assertIsDisplayed()
    composeTestRule.onNodeWithText("Canceled").assertIsDisplayed()
    composeTestRule.onNodeWithText("Unpaid").assertIsDisplayed()
    composeTestRule.onNodeWithText("Finished").assertIsDisplayed()
  }

  @Test
  fun quickFixFilterButtonsToggleCorrectly_All() {
    composeTestRule.setContent {
      DashboardScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          isUser = true)
    }

    // Click "All"
    composeTestRule.onNodeWithText("All").performClick()
    composeTestRule.onNodeWithTag("AllQuickFixes").assertIsDisplayed()
  }

  @Test
  fun quickFixFilterButtonsToggleCorrectly_Upcoming() {
    composeTestRule.setContent {
      DashboardScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          isUser = true)
    }

    // "Upcoming" is selected by default in the code, just verify
    composeTestRule.onNodeWithTag("UpcomingQuickFixes").assertIsDisplayed()

    // Click another button and back to Upcoming to ensure toggling works
    composeTestRule.onNodeWithText("Canceled").performClick()
    composeTestRule.onNodeWithTag("CanceledQuickFixes").assertIsDisplayed()

    // Click back on Upcoming
    composeTestRule.onNodeWithText("Upcoming").performClick()
    composeTestRule.onNodeWithTag("UpcomingQuickFixes").assertIsDisplayed()
  }

  @Test
  fun quickFixFilterButtonsToggleCorrectly_Canceled() {
    composeTestRule.setContent {
      DashboardScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          isUser = true)
    }

    composeTestRule.onNodeWithText("Canceled").performClick()
    composeTestRule.onNodeWithTag("CanceledQuickFixes").assertIsDisplayed()
  }

  @Test
  fun quickFixFilterButtonsToggleCorrectly_Unpaid() {
    composeTestRule.setContent {
      DashboardScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          isUser = true)
    }

    composeTestRule.onNodeWithText("Unpaid").performClick()
    composeTestRule.onNodeWithTag("UnpaidQuickFixes").assertIsDisplayed()
  }

  @Test
  fun quickFixFilterButtonsToggleCorrectly_Finished() {
    composeTestRule.setContent {
      DashboardScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          isUser = true)
    }

    composeTestRule.onNodeWithText("Finished").performClick()
    composeTestRule.onNodeWithTag("FinishedQuickFixes").assertIsDisplayed()
  }

  @Test
  fun announcementsWidgetDisplaysCorrectly() {
    composeTestRule.setContent {
      DashboardScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          isUser = true)
    }

    // Verify that the AnnouncementsWidget is displayed
    composeTestRule.onNodeWithTag("AnnouncementsWidget").assertIsDisplayed()
  }

  @Test
  fun messagesWidgetDisplaysCorrectly() {
    composeTestRule.setContent {
      DashboardScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          isUser = true)
    }

    // Verify that the MessagesWidget is displayed
    composeTestRule.onNodeWithTag("MessagesWidget").assertIsDisplayed()
  }

  @Test
  fun billsWidgetDisplaysCorrectly() {
    composeTestRule.setContent {
      DashboardScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          isUser = true)
    }

    // Verify that the BillsWidget is displayed
    composeTestRule.onNodeWithTag("BillsWidget").assertIsDisplayed()
  }

  @Test
  fun quickFixesWidgetDisplaysAndInteractsCorrectly() {
    composeTestRule.setContent {
      DashboardScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          isUser = true)
    }

    // By default "Upcoming" is selected
    composeTestRule.onNodeWithTag("UpcomingQuickFixes").assertIsDisplayed()

    // Interact with a QuickFix item (assuming tags like "QuickFixItem_Adam")
    // This test assumes that your QuickFix items set a unique testTag including the name.
    composeTestRule.onNodeWithTag("QuickFixItem_Adam").assertExists()
    composeTestRule.onNodeWithTag("QuickFixItem_Adam").assertHasClickAction()
    composeTestRule.onNodeWithTag("QuickFixItem_Adam").performClick()
    // Add assertions for expected navigation or behavior if necessary
  }

  @Test
  fun messagesWidgetInteractsCorrectly() {
    composeTestRule.setContent {
      DashboardScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          isUser = true)
    }

    // Interact with a Message item (assuming tags like "MessageItem_Ramy Hatimy")
    composeTestRule.onNodeWithTag("MessageItem_Ramy Hatimy").assertExists()
    composeTestRule.onNodeWithTag("MessageItem_Ramy Hatimy").assertHasClickAction()
    composeTestRule.onNodeWithTag("MessageItem_Ramy Hatimy").performClick()
    // Add assertion for expected navigation or behavior
  }

  @Test
  fun billsWidgetInteractsCorrectly() {
    composeTestRule.setContent {
      DashboardScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          isUser = true)
    }

    // Interact with a Bill item (assuming tags like "BillItem_Adam")
    composeTestRule.onNodeWithTag("BillItem_Adam").assertExists()
    composeTestRule.onNodeWithTag("BillItem_Adam").assertHasClickAction()
    composeTestRule.onNodeWithTag("BillItem_Adam").performClick()
    // Add assertion for expected navigation or behavior
  }
}
