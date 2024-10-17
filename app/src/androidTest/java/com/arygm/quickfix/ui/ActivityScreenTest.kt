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

class ActivityScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.ACTIVITY)
  }

  @Test
  fun activityScreenUserDisplaysCorrectly() {
    composeTestRule.setContent { ActivityScreen(navigationActions, true) }

    composeTestRule.onNodeWithTag("ActivityTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ACTIVITY").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ACTIVITY").assertTextEquals("ACTIVITY")
    composeTestRule.onNodeWithTag("ActivityContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ActivityText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("ActivityText")
        .assertTextContains("Welcome to the ACTIVITY Screen")
  }

  // Can Remove this tbh
  @Test
  fun activityScreenWorkerDisplaysCorrectly() {
    composeTestRule.setContent { ActivityScreen(navigationActions, false) }

    composeTestRule.onNodeWithTag("ActivityTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ACTIVITY").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ACTIVITY").assertTextEquals("ACTIVITY")
    composeTestRule.onNodeWithTag("ActivityContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ActivityText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("ActivityText")
        .assertTextContains("Welcome to the ACTIVITY Screen")
  }
}
