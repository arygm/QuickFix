package com.arygm.quickfix.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.DASHBOARD)
  }

  @Test
  fun mapScreenUserDisplaysCorrectly() {
    composeTestRule.setContent { DashboardScreen(navigationActions, true) }

    composeTestRule.onNodeWithTag("DashboardTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DASHBOARD").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DASHBOARD").assertTextEquals("DASHBOARD")
    composeTestRule.onNodeWithTag("DashboardContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DashboardText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DashboardText").assertTextContains("Welcome to the DASHBOARD Screen")
    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()
  }

  // Can Remove this tbh
  @Test
  fun mapScreenWorkerDisplaysCorrectly() {
    composeTestRule.setContent { DashboardScreen(navigationActions, false) }

    composeTestRule.onNodeWithTag("DashboardTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DASHBOARD").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DASHBOARD").assertTextEquals("DASHBOARD")
    composeTestRule.onNodeWithTag("DashboardContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DashboardText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DashboardText").assertTextContains("Welcome to the DASHBOARD Screen")
    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()
  }
}
