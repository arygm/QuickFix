package com.arygm.quickfix.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ProfileConfigurationScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun profileConfigurationScreenDisplaysCorrectly() {
    composeTestRule.setContent { ProfileConfigurationScreen(navigationActions = navigationActions) }

    // Verify title and content
    composeTestRule.onNodeWithTag("AccountConfigurationTitle").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("AccountConfigurationTitle")
        .assertTextEquals("Account configuration")

    composeTestRule.onNodeWithTag("ProfileConfigurationContent").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("ProfileConfigurationText")
        .assertTextEquals("Welcome to the ProfileConfiguration Screen")
  }

  @Test
  fun goBackButtonWorksCorrectly() {
    composeTestRule.setContent { ProfileConfigurationScreen(navigationActions = navigationActions) }

    // Simulate back button click
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    // Verify navigation back action is called
    verify(navigationActions).goBack()
  }
}
