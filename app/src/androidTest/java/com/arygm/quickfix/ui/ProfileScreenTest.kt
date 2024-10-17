package com.arygm.quickfix.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class ProfileScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.PROFILE)
  }

  @Test
  fun activityScreenUserDisplaysCorrectly() {
    composeTestRule.setContent { ProfileScreen(navigationActions, true) }

    composeTestRule.onNodeWithTag("ProfileTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileText").assertTextContains("Welcome to the PROFILE Screen")
  }

  // Can Remove this tbh
  @Test
  fun activityScreenWorkerDisplaysCorrectly() {
    composeTestRule.setContent { ProfileScreen(navigationActions, true) }

    composeTestRule.onNodeWithTag("ProfileTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileText").assertTextContains("Welcome to the PROFILE Screen")
  }
}
