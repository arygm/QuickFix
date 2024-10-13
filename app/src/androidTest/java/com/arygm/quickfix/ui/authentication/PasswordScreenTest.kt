package com.arygm.quickfix.ui.authentication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class PasswordScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = Mockito.mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.PASSWORD)
  }

  @Test
  fun testInitialState() {
    composeTestRule.setContent { PasswordScreen(navigationActions) }

    // Check that the input fields, password conditions, and button are displayed
    composeTestRule.onNodeWithTag("passwordBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnimationBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("contentBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("boxDecoration").assertIsDisplayed()
    composeTestRule.onNodeWithTag("passwordText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("passwordInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("repeatPasswordInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noMatchText").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("registerButton").assertIsDisplayed().assertIsNotEnabled()

    // Check that all password condition messages are displayed
    composeTestRule.onNodeWithTag("PASSWORD SHOULD BE AT LEAST 8 CHARACTERS").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("PASSWORD SHOULD CONTAIN AN UPPERCASE LETTER (A-Z)")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("PASSWORD SHOULD CONTAIN A LOWERCASE LETTER (a-z)")
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("PASSWORD SHOULD CONTAIN A DIGIT (0-9)").assertIsDisplayed()
  }

  @Test
  fun testPasswordConditionsUpdateOnInput() {
    composeTestRule.setContent { PasswordScreen(navigationActions) }

    // Input a password that meets all conditions
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password1")

    // Assert that all conditions are met (colored as tertiary)
    composeTestRule.onNodeWithTag("PASSWORD SHOULD BE AT LEAST 8 CHARACTERS").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("PASSWORD SHOULD CONTAIN AN UPPERCASE LETTER (A-Z)")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("PASSWORD SHOULD CONTAIN A LOWERCASE LETTER (a-z)")
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("PASSWORD SHOULD CONTAIN A DIGIT (0-9)").assertIsDisplayed()
    composeTestRule.onNodeWithTag("registerButton").assertIsDisplayed().assertIsNotEnabled()
  }

  @Test
  fun testPasswordMismatchShowsError() {
    composeTestRule.setContent { PasswordScreen(navigationActions) }

    // Input a password and a mismatching repeated password
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password1")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password2")

    // Assert that the mismatch error is displayed
    composeTestRule.onNodeWithTag("noMatchText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("registerButton").assertIsDisplayed().assertIsNotEnabled()
  }

  @Test
  fun testRegisterButtonEnabledWhenConditionsAreMet() {
    composeTestRule.setContent { PasswordScreen(navigationActions) }

    // Input matching passwords that meet all conditions
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password1")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password1")

    // Assert that the register button is now enabled
    composeTestRule.onNodeWithTag("noMatchText").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("registerButton").assertIsEnabled()
  }

  @Test
  fun testRegisterButtonDisabledWhenConditionsAreNotMet() {
    composeTestRule.setContent { PasswordScreen(navigationActions) }

    // Input a password that does not meet all conditions (no digit)
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password")

    // Assert that the register button is still disabled
    composeTestRule.onNodeWithTag("registerButton").assertIsNotEnabled()
  }

  @Test
  fun testBackButtonNavigatesBack() {
    composeTestRule.setContent { PasswordScreen(navigationActions) }

    // Click the back button
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    // Verify that the navigation action was triggered
    Mockito.verify(navigationActions).goBack()
  }
}
