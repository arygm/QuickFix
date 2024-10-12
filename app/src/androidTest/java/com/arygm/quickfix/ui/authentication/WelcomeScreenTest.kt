package com.arygm.quickfix.ui.authentication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class WelcomeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.WELCOME)
  }

  @Test
  fun testInitialState() {
    composeTestRule.setContent { WelcomeScreen(navigationActions) }

    // Check if the background image is displayed
    composeTestRule.onNodeWithTag("welcomeBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("boxDecoration1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("boxDecoration2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("workerBackground").assertIsDisplayed()

    // Check that the QuickFix logo is displayed
    composeTestRule.onNodeWithTag("quickFixLogo").assertIsDisplayed()

    // Check that the QuickFix text is displayed
    composeTestRule.onNodeWithTag("quickFixText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("quickFixText").assertTextEquals("QuickFix")

    // Check that the buttons are displayed
    composeTestRule.onNodeWithTag("logInButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("logInButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("logInButton").assertTextEquals("LOG IN TO QUICKFIX")
    composeTestRule.onNodeWithTag("RegistrationButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RegistrationButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("RegistrationButton").assertTextEquals("REGISTER TO QUICKFIX")

    // Check if Google button and logo are displayed
    composeTestRule.onNodeWithTag("googleButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("googleButton").assertTextEquals("CONTINUE WITH GOOGLE")
  }

  @Test
  fun testLogInButtonClickNavigatesToLogin() {
    composeTestRule.setContent { WelcomeScreen(navigationActions) }

    // Click the "LOG IN TO QUICKFIX" button
    composeTestRule.onNodeWithTag("logInButton").performClick()

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    // Verify that the navigation action is triggered for the login screen
    Mockito.verify(navigationActions).navigateTo(Screen.LOGIN)
  }

  @Test
  fun testRegistrationButtonClickNavigatesToInfo() {
    composeTestRule.setContent { WelcomeScreen(navigationActions) }

    // Click the "REGISTER TO QUICKFIX" button
    composeTestRule.onNodeWithTag("RegistrationButton").performClick()

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    // Verify that the navigation action is triggered for the registration/info screen
    Mockito.verify(navigationActions).navigateTo(Screen.INFO)
  }

  @Test
  fun testGoogleButtonClick() {
    composeTestRule.setContent { WelcomeScreen(navigationActions) }

    // Click the "CONTINUE WITH GOOGLE" button
    composeTestRule.onNodeWithTag("googleButton").performClick()

    // TODO: Add logic here for Google button click behavior when implemented
  }
}
