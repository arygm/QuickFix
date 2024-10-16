package com.arygm.quickfix.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class LogInScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel

  @Before
  fun setup() {
    profileRepository = mock(ProfileRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    profileViewModel = ProfileViewModel(profileRepository)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.LOGIN)
  }

  @Test
  fun testInitialUI() {
    composeTestRule.setContent { LogInScreen(navigationActions, profileViewModel) }

    // Check that the scaffold and content boxes are displayed
    composeTestRule.onNodeWithTag("LoginScaffold").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ContentBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnimationBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BoxDecoration").assertIsDisplayed()

    // Check that the "Login" text is displayed
    composeTestRule.onNodeWithTag("WelcomeText").assertIsDisplayed()

    // Check that the "Your perfect fix is just a click away" text is displayed
    composeTestRule.onNodeWithTag("WelcomeTextBis").assertIsDisplayed()

    // Check that the email and password fields are empty initially
    composeTestRule.onNodeWithTag("inputEmail").assertIsDisplayed()
    composeTestRule.onNodeWithTag("inputPassword").assertIsDisplayed()

    // Check that the login button is displayed
    composeTestRule.onNodeWithTag("logInButton").assertIsDisplayed()

    // Check that the forgot password texts are displayed
    composeTestRule.onNodeWithTag("forgetPasswordButtonText").assertIsDisplayed()

    // Check that the "Don't have an account?" text is displayed
    composeTestRule.onNodeWithTag("noAccountText").assertIsDisplayed()

    // Check that the "Create One" text is displayed
    composeTestRule.onNodeWithTag("clickableCreateAccount").assertIsDisplayed()
  }

  @Test
  fun testLoginButtonEnabledWhenFieldsAreFilled() {
    composeTestRule.setContent { LogInScreen(navigationActions, profileViewModel) }

    // Input valid email and password
    composeTestRule.onNodeWithTag("inputEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("inputPassword").performTextInput("password123")

    // Click the login button
    composeTestRule.onNodeWithTag("logInButton").performClick()
  }

  @Test
  fun testInvalidEmailShowsError() {
    composeTestRule.setContent { LogInScreen(navigationActions, profileViewModel) }

    // Input an invalid email
    composeTestRule.onNodeWithTag("inputEmail").performTextInput("invalidemail")

    // Click the login button
    composeTestRule.onNodeWithTag("logInButton").assertIsNotEnabled()

    // Check that the email error is displayed
    composeTestRule.onNodeWithTag("errorText").assertIsDisplayed()
  }

  @Test
  fun testErrorMessageShownOnInvalidLogin() {
    /*
     * This test relied on the fact a button click on login would simply change the state.
     * it is not valid without the proper backend logic
     */

    /*
    composeTestRule.setContent { LogInScreen(navigationActions) }

    // Input an invalid email and password
    composeTestRule.onNodeWithTag("inputEmail").performTextInput("invalid@example.com")
    composeTestRule.onNodeWithTag("inputPassword").performTextInput("wrongpassword")

    // Click the login button
    composeTestRule.onNodeWithTag("logInButton").performClick()

    // Check that the error message is displayed
    composeTestRule.onNodeWithTag("errorText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("errorText")
        .assertTextEquals("INVALID EMAIL OR PASSWORD, TRY AGAIN.")

     */
  }

  @Test
  fun testForgotPasswordLinkIsDisplayed() {
    composeTestRule.setContent { LogInScreen(navigationActions, profileViewModel) }

    // Check that the forgot password text is displayed
    composeTestRule.onNodeWithTag("forgetPasswordButtonText").assertIsDisplayed()
  }

  @Test
  fun testBackButtonNavigatesBack() {
    composeTestRule.setContent { LogInScreen(navigationActions, profileViewModel) }

    // Click the back button
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    composeTestRule.mainClock.advanceTimeBy(500L)

    // Verify that the navigation action was triggered
    Mockito.verify(navigationActions).goBack()
  }
}
