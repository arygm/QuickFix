package com.arygm.quickfix.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class ProfileConfigurationScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var profileViewModel: ProfileViewModel
  private val mockProfile =
      Profile(
          uid = "123",
          firstName = "John",
          lastName = "Doe",
          email = "john.doe@example.com",
          birthDate = Timestamp.now(),
          description = "Test user",
          isWorker = false,
          fieldOfWork = "N/A",
          hourlyRate = 0.0)

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)

    profileViewModel = mock(ProfileViewModel::class.java)

    `when`(profileViewModel.loggedInProfile).thenReturn(MutableStateFlow(mockProfile).asStateFlow())
  }

  @Test
  fun profileConfigurationScreenDisplaysCorrectly() {
    composeTestRule.setContent {
      ProfileConfigurationScreen(navigationActions, profileViewModel = profileViewModel)
    }

    composeTestRule.onNodeWithTag("AccountConfigurationTopAppBar").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("AccountConfigurationTitle")
        .assertTextEquals("Account configuration")

    composeTestRule.onNodeWithTag("ProfileImage").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileName").assertTextEquals("John Doe")
  }

  @Test
  fun inputFieldsAreDisplayed() {
    composeTestRule.setContent {
      ProfileConfigurationScreen(navigationActions, profileViewModel = profileViewModel)
    }

    // Test for input fields
    composeTestRule.onNodeWithTag("firstNameInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lastNameInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("emailInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("birthDateInput").assertIsDisplayed()
  }

  @Test
  fun inputsHaveInitialValue() {
    profileViewModel.setLoggedInProfile(mockProfile)
    composeTestRule.setContent {
      ProfileConfigurationScreen(navigationActions, profileViewModel = profileViewModel)
    }

    Thread.sleep(10000)

    composeTestRule.onNodeWithTag("firstNameInput").assertTextContains(mockProfile.firstName)
    composeTestRule.onNodeWithTag("lastNameInput").assertTextContains(mockProfile.lastName)
    composeTestRule.onNodeWithTag("emailInput").assertTextContains(mockProfile.email)
  }

  @Test
  fun backButtonFunctionality() {
    composeTestRule.setContent {
      ProfileConfigurationScreen(navigationActions, profileViewModel = profileViewModel)
    }

    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }
}
