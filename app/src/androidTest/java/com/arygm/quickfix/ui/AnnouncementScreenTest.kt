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

class AnnouncementScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.ANNOUNCEMENT)
  }

  @Test
  fun announcementScreenUserDisplaysCorrectly() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions, true) }

    composeTestRule.onNodeWithTag("AnnouncementTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ANNOUNCEMENT").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ANNOUNCEMENT").assertTextEquals("ANNOUNCEMENT")
    composeTestRule.onNodeWithTag("AnnouncementContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnnouncementText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("AnnouncementText")
        .assertTextContains("Welcome to the ANNOUNCEMENT Screen")
    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()
  }

  // Can Remove this tbh
  @Test
  fun announcementScreenWorkerDisplaysCorrectly() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions, false) }

    composeTestRule.onNodeWithTag("AnnouncementTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ANNOUNCEMENT").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ANNOUNCEMENT").assertTextEquals("ANNOUNCEMENT")
    composeTestRule.onNodeWithTag("AnnouncementContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnnouncementText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("AnnouncementText")
        .assertTextContains("Welcome to the ANNOUNCEMENT Screen")
    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()
  }

  @Test
  fun bottomNavMenuWorkerNavigatesCorrectly() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions, false) }

    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()

    composeTestRule.onNodeWithTag("BottomNavMenu").performTouchInput { click(Offset(300f, 100f)) }

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    verify(navigationActions).navigateTo(TopLevelDestinations.CALENDAR)
  }

  @Test
  fun bottomNavMenuUserNavigatesCorrectly() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions, true) }

    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()

    composeTestRule.onNodeWithTag("BottomNavMenu").performTouchInput { click(Offset(100f, 100f)) }

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    verify(navigationActions).navigateTo(TopLevelDestinations.HOME)
  }
}
