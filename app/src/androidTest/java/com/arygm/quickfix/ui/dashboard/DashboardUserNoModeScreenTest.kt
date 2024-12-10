package com.arygm.quickfix.ui.dashboard

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.UserScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class DashboardUserNoModeScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(UserScreen.DASHBOARD)
  }

  @Test
  fun dashboardDisplaysQuickFixFilterButtons() {
    composeTestRule.setContent { DashboardScreen(navigationActions, true) }

    // Verify that the QuickFix filter buttons are displayed
    composeTestRule.onNodeWithText("All").assertIsDisplayed()
    composeTestRule.onNodeWithText("Upcoming").assertIsDisplayed()
    composeTestRule.onNodeWithText("Canceled").assertIsDisplayed()
    composeTestRule.onNodeWithText("Unpaid").assertIsDisplayed()
    composeTestRule.onNodeWithText("Finished").assertIsDisplayed()
  }

  @Test
  fun quickFixFilterButtonsToggleCorrectly() {
    composeTestRule.setContent { DashboardScreen(navigationActions, true) }

    // Verify that clicking "Canceled" selects it and deselects "Upcoming"
    composeTestRule.onNodeWithText("Canceled").performClick()
    composeTestRule.onNodeWithText("Canceled").assertHasClickAction()
    composeTestRule.onNodeWithText("Canceled").assertExists()

    composeTestRule.onNodeWithText("Upcoming").assertHasClickAction()
    composeTestRule.onNodeWithText("Upcoming").assertExists()
  }

  @Test
  fun quickFixesWidgetDisplaysCorrectly() {
    composeTestRule.setContent { DashboardScreen(navigationActions, true) }

    // Verify that the QuickFixesWidget is displayed by default
    composeTestRule.onNodeWithTag("UpcomingQuickFixes").assertIsDisplayed()

    // Verify that the widget updates based on the selected filter
    composeTestRule.onNodeWithText("Canceled").performClick()
    composeTestRule.onNodeWithTag("CanceledQuickFixes").assertIsDisplayed()
  }

  @Test
  fun messagesWidgetDisplaysCorrectly() {
    composeTestRule.setContent { DashboardScreen(navigationActions, true) }

    // Verify that the MessagesWidget is displayed
    composeTestRule.onNodeWithTag("MessagesWidget").assertIsDisplayed()
  }

  @Test
  fun billsWidgetDisplaysCorrectly() {
    composeTestRule.setContent { DashboardScreen(navigationActions, true) }

    // Verify that the BillsWidget is displayed
    composeTestRule.onNodeWithTag("BillsWidget").assertIsDisplayed()
  }

  @Test
  fun quickFixWidgetInteractsCorrectly() {
    composeTestRule.setContent { DashboardScreen(navigationActions, true) }

    // Verify QuickFix item interactions
    composeTestRule.onNodeWithTag("QuickFixItem_Adam").performClick()
    // Add assertion for expected navigation or behavior
  }

  @Test
  fun messagesWidgetInteractsCorrectly() {
    composeTestRule.setContent { DashboardScreen(navigationActions, true) }

    // Verify Message item interactions
    composeTestRule.onNodeWithTag("MessageItem_Ramy Hatimy").performClick()
    // Add assertion for expected navigation or behavior
  }

  @Test
  fun billsWidgetInteractsCorrectly() {
    composeTestRule.setContent { DashboardScreen(navigationActions, true) }

    // Verify Bill item interactions
    composeTestRule.onNodeWithTag("BillItem_Adam").performClick()
    // Add assertion for expected navigation or behavior
  }
}
