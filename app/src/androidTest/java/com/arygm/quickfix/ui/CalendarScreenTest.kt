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

class CalendarScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.CALENDAR)
  }

  @Test
  fun calendarScreenUserDisplaysCorrectly() {
    composeTestRule.setContent { CalendarScreen(navigationActions, true) }

    composeTestRule.onNodeWithTag("CalendarTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("CALENDAR").assertIsDisplayed()
    composeTestRule.onNodeWithTag("CALENDAR").assertTextEquals("CALENDAR")
    composeTestRule.onNodeWithTag("CalendarContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("CalendarText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("CalendarText")
        .assertTextContains("Welcome to the CALENDAR Screen")
    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()
  }

  // Can Remove this tbh
  @Test
  fun calendarScreenWorkerDisplaysCorrectly() {
    composeTestRule.setContent { CalendarScreen(navigationActions, false) }

    composeTestRule.onNodeWithTag("CalendarTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("CALENDAR").assertIsDisplayed()
    composeTestRule.onNodeWithTag("CALENDAR").assertTextEquals("CALENDAR")
    composeTestRule.onNodeWithTag("CalendarContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("CalendarText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("CalendarText")
        .assertTextContains("Welcome to the CALENDAR Screen")
    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()
  }

  @Test
  fun bottomNavMenuWorkerNavigatesCorrectly() {
    composeTestRule.setContent { CalendarScreen(navigationActions, false) }

    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()

    composeTestRule.onNodeWithTag("BottomNavMenu").performTouchInput { click(Offset(500f, 100f)) }

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    verify(navigationActions).navigateTo(TopLevelDestinations.MAP)
  }

  @Test
  fun bottomNavMenuUserNavigatesCorrectly() {
    composeTestRule.setContent { CalendarScreen(navigationActions, true) }

    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()

    composeTestRule.onNodeWithTag("BottomNavMenu").performTouchInput { click(Offset(400f, 100f)) }

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    verify(navigationActions).navigateTo(TopLevelDestinations.ANNOUNCEMENT)
  }
}
