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
}
