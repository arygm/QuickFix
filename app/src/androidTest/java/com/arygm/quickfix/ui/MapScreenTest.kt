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

class MapScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAP)
  }

  @Test
  fun mapScreenUserDisplaysCorrectly() {
    composeTestRule.setContent { MapScreen(navigationActions, true) }

    composeTestRule.onNodeWithTag("MapTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MAP").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MAP").assertTextEquals("MAP")
    composeTestRule.onNodeWithTag("MapContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapText").assertTextContains("Welcome to the MAP Screen")
    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()
  }

  // Can Remove this tbh
  @Test
  fun mapScreenWorkerDisplaysCorrectly() {
    composeTestRule.setContent { MapScreen(navigationActions, false) }

    composeTestRule.onNodeWithTag("MapTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MAP").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MAP").assertTextEquals("MAP")
    composeTestRule.onNodeWithTag("MapContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapText").assertTextContains("Welcome to the MAP Screen")
    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()
  }

  @Test
  fun bottomNavMenuWorkerNavigatesCorrectly() {
    composeTestRule.setContent { MapScreen(navigationActions, false) }

    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()

    composeTestRule.onNodeWithTag("BottomNavMenu").performTouchInput { click(Offset(300f, 100f)) }

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    verify(navigationActions).navigateTo(TopLevelDestinations.CALENDAR)
  }

  @Test
  fun bottomNavMenuUserNavigatesCorrectly() {
    composeTestRule.setContent { MapScreen(navigationActions, true) }

    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()

    composeTestRule.onNodeWithTag("BottomNavMenu").performTouchInput { click(Offset(100f, 100f)) }

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    verify(navigationActions).navigateTo(TopLevelDestinations.HOME)
  }
}
