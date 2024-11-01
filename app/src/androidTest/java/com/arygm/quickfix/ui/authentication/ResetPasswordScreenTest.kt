package com.arygm.quickfix.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
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

class ResetPasswordScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel

  @Before
  fun setup() {
    profileRepository = mock(ProfileRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    profileViewModel = ProfileViewModel(profileRepository)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.RESET_PASSWORD)
  }

  @Test
  fun testInitialUI() {
    composeTestRule.setContent { ResetPasswordScreen(navigationActions, profileViewModel) }

    // Check that the scaffold and content boxes are displayed
    composeTestRule.onNodeWithTag("ForgotPasswordScaffold").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ContentBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BoxDecoration").assertIsDisplayed()

    // Check that the "Reset Password" text is displayed
    composeTestRule.onNodeWithTag("WelcomeText").assertIsDisplayed()

    // Check that the email field is displayed
    composeTestRule.onNodeWithTag("inputEmail").assertIsDisplayed()

    // Check that the reset button is displayed
    composeTestRule.onNodeWithTag("ResetButton").assertIsDisplayed()
  }

  @Test
  fun testResetButtonEnabledWhenEmailIsValid() {
    composeTestRule.setContent { ResetPasswordScreen(navigationActions, profileViewModel) }

    // Input a valid email
    composeTestRule.onNodeWithTag("inputEmail").performTextInput("test@example.com")

    // Check that the reset button is enabled
    composeTestRule.onNodeWithTag("ResetButton").assertIsEnabled()
  }

  @Test
  fun testBackButtonNavigatesBack() {
    composeTestRule.setContent { ResetPasswordScreen(navigationActions, profileViewModel) }

    // Click the back button
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    composeTestRule.mainClock.advanceTimeBy(500L)

    // Verify that the navigation action was triggered
    Mockito.verify(navigationActions).goBack()
  }
}
