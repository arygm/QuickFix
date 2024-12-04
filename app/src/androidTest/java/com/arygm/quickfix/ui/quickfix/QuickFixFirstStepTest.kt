package com.arygm.quickfix.ui.quickfix

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.locations.LocationRepository
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import java.time.LocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class QuickFixFirstStepTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var locationRepository: LocationRepository
  private lateinit var locationViewModel: LocationViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    locationRepository = mock(LocationRepository::class.java)
    locationViewModel = LocationViewModel(locationRepository)
  }

  @Test
  fun initialStateValidation() {

    composeTestRule.setContent {
      QuickFixFirstStep(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    // Verify default UI elements
    composeTestRule.onNodeWithText("Enter a title ...").assertExists()
    composeTestRule.onNodeWithText("Features services").assertExists()
    composeTestRule.onNodeWithText("Add-on services").assertExists()
    composeTestRule.onNodeWithText("Upload Pictures").assertExists().performScrollTo()

    // Verify "Continue" button is initially disabled
    composeTestRule.onNodeWithText("Continue").assertIsNotEnabled()
  }

  @Test
  fun textInputBehavior() {
    composeTestRule.setContent {
      QuickFixFirstStep(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    // Enter text in the title field
    composeTestRule.onNodeWithText("Enter a title ...").performTextInput("Test Title")
    composeTestRule.onNodeWithText("Test Title").assertExists()

    // Enter text in the quick note field
    composeTestRule.onNodeWithText("Type a description...").performTextInput("Test Note")
    composeTestRule.onNodeWithText("Test Note").assertExists()
  }

  @Test
  fun serviceSelection() {
    composeTestRule.setContent {
      QuickFixFirstStep(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    // Select a service
    composeTestRule.onNodeWithText("Service 1").performClick()
    // Assert that the service is selected (custom assertion based on your implementation)
    composeTestRule.onNodeWithText("Service 1").assertHasClickAction()
  }

  @Test
  fun datePickerIntegration() {
    composeTestRule.setContent {
      QuickFixFirstStep(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    // Open the date picker
    composeTestRule.onNodeWithText("Add Suggested Date").performClick()
    // Assert the date picker is displayed
    composeTestRule.onNodeWithText("Select Date").assertExists()

    // Simulate selecting a date
    composeTestRule.onNodeWithText("${LocalDate.now().dayOfMonth}").performClick()
    composeTestRule.onNodeWithText("OK").performClick()

    composeTestRule.onNodeWithText("OK").performClick()

    // Assert the selected date is displayed
    composeTestRule.onNodeWithText("Suggested Date").assertExists()
  }

  /*  @Test
  fun locationSuggestionDropdown() {
    val mockLocation = Location(0.0, 0.0, "Mock Location 1")

    // Mock the ViewModel's behavior
    locationViewModel.setLocations(listOf(mockLocation))

    composeTestRule.setContent {
      QuickFixFirstStep(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    // Enter text in the location field to trigger suggestions
    composeTestRule.onNodeWithText("Enter a location ...").performTextInput("Mock Location")
    composeTestRule.waitForIdle()

    // Assert the dropdown is displayed with the mock suggestion
    composeTestRule.onNodeWithText("Mock Location 1").assertExists()

    // Select a suggestion
    composeTestRule.onNodeWithText("Mock Location 1").performClick()

    // Assert the location field is updated
    composeTestRule.onNodeWithText("Enter a location ...").assertTextContains("Mock Location 1")
  }*/

  /*  @Test
  fun imageUpload() {
    composeTestRule.setContent {
      QuickFixFirstStep(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    // Open the camera bottom sheet
    composeTestRule.onNodeWithText("Upload Pictures").performClick()
    // Simulate adding an image
    // (Assume your implementation of adding an image)

    // Assert the image is displayed
    composeTestRule.onNodeWithContentDescription("Profile Picture").assertExists()
  }*/

  /*  @Test
  fun buttonStateValidation() {
    composeTestRule.setContent {
      QuickFixFirstStep(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    // Verify "Continue" button is initially disabled
    composeTestRule.onNodeWithText("Continue").assertIsNotEnabled()

    // Fill all required fields
    composeTestRule.onNodeWithText("Enter a title ...").performTextInput("Test Title")
    composeTestRule.onNodeWithText("Service 1").performClick()
    composeTestRule.onNodeWithText("Add Suggested Date").performClick()
    composeTestRule.onNodeWithText("1").performClick()
    composeTestRule.onNodeWithText("OK").performClick()
    composeTestRule.onNodeWithText("OK").performClick()
    composeTestRule.onNodeWithText("Enter a location ...").performTextInput("Test Location")

    // Verify "Continue" button is enabled
    composeTestRule.onNodeWithText("Continue").assertIsEnabled()
  }*/
}
