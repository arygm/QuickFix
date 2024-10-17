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

class OtherScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.OTHER)
  }

  @Test
  fun otherScreenUserDisplaysCorrectly() {
    composeTestRule.setContent { OtherScreen(navigationActions, true) }

    composeTestRule.onNodeWithTag("OtherTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OTHER").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OTHER").assertTextEquals("OTHER")
    composeTestRule.onNodeWithTag("OtherContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OtherText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("OtherText")
        .assertTextContains("Welcome to the other features Screen")
  }

  // Can Remove this tbh
  @Test
  fun otherScreenWorkerDisplaysCorrectly() {
    composeTestRule.setContent { OtherScreen(navigationActions, false) }

    composeTestRule.onNodeWithTag("OtherTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OTHER").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OTHER").assertTextEquals("OTHER")
    composeTestRule.onNodeWithTag("OtherContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("OtherText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("OtherText")
        .assertTextContains("Welcome to the other features Screen")
  }
}
