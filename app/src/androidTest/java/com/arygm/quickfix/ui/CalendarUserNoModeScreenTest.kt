package com.arygm.quickfix.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.userModeUI.CalendarScreen
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class CalendarUserNoModeScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(UserScreen.CALENDAR)
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
  }
}
