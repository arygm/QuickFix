package com.arygm.quickfix.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.navigation.TopLevelDestinations
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
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
    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()
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
    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()
  }

  @Test
  fun bottomNavMenuWorkerNavigatesCorrectly() {
    composeTestRule.setContent { OtherScreen(navigationActions, false) }

    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()

    composeTestRule.onNodeWithTag("BottomNavMenu").performTouchInput { click(Offset(300f, 100f)) }

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    verify(navigationActions).navigateTo(TopLevelDestinations.CALENDAR)
  }

  @Test
  fun bottomNavMenuUserNavigatesCorrectly() {
    composeTestRule.setContent { OtherScreen(navigationActions, true) }

    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()

    composeTestRule.onNodeWithTag("BottomNavMenu").performTouchInput { click(Offset(100f, 100f)) }

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    verify(navigationActions).navigateTo(TopLevelDestinations.HOME)
  }
}
