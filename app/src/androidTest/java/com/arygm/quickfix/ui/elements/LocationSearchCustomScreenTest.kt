package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationRepository
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer

@RunWith(AndroidJUnit4::class)
class LocationSearchCustomScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var locationViewModel: LocationViewModel
  private lateinit var locationRepository: LocationRepository

  @Before
  fun setup() {
    // Mock dependencies
    navigationActions = mock(NavigationActions::class.java)
    locationRepository = mock(LocationRepository::class.java)

    // Create an instance of LocationViewModel with the mocked repository
    locationViewModel = LocationViewModel(locationRepository)
  }

  @Test
  fun testInitialState() {
    composeTestRule.setContent {
      LocationSearchCustomScreen(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    composeTestRule.onNodeWithTag("input_search_field").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithTag("use_current_location").assertExists().assertIsDisplayed()
  }

  @Test
  fun testSearchQueryUpdatesSuggestions() {
    val testQuery = "Paris"
    val testSuggestions =
        listOf(
            Location(name = "Paris, France", latitude = 48.8566, longitude = 2.3522),
            Location(name = "Paris, Texas, USA", latitude = 33.6609, longitude = -95.5555))

    // Mock the repository's searchLocations method
    doAnswer { invocation ->
          val query = invocation.arguments[0] as String
          val onSuccess = invocation.arguments[1] as (List<Location>) -> Unit

          if (query == testQuery) {
            onSuccess(testSuggestions)
          } else {
            onSuccess(emptyList())
          }
          null
        }
        .`when`(locationRepository)
        .search(any(), any(), any())

    // Set the composable content
    composeTestRule.setContent {
      LocationSearchCustomScreen(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    composeTestRule.onNodeWithTag("input_search_field").performTextInput(testQuery)

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Paris, France").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText("Paris, Texas, USA").assertExists().assertIsDisplayed()
  }

  @Test
  fun testClickingOnSuggestionSavesLocationAndNavigatesBack() {
    val testLocation = Location(name = "Paris, France", latitude = 48.8566, longitude = 2.3522)

    // Mock the repository to return the test location
    doAnswer { invocation ->
          val query = invocation.arguments[0] as String
          val onSuccess = invocation.arguments[1] as (List<Location>) -> Unit

          onSuccess(listOf(testLocation))
          null
        }
        .`when`(locationRepository)
        .search(any(), any(), any())

    // Set the composable content
    composeTestRule.setContent {
      LocationSearchCustomScreen(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    composeTestRule.onNodeWithTag("input_search_field").performTextInput("Any Query")

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Paris, France").assertExists().performClick()

    verify(navigationActions).saveToBackStack("selectedLocation", testLocation)
    verify(navigationActions).goBack()
  }

  @Test
  fun testErrorMessageIsDisplayedWhenErrorOccurs() {
    val testErrorMessage = "No results found"

    // Mock the repository to trigger an error
    doAnswer { invocation ->
          val onError = invocation.arguments[2] as (Throwable) -> Unit
          onError(Exception(testErrorMessage))
          null
        }
        .`when`(locationRepository)
        .search(any(), any(), any())

    // Set the composable content
    composeTestRule.setContent {
      LocationSearchCustomScreen(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    composeTestRule.onNodeWithTag("input_search_field").performTextInput("Some Query")

    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag("no_results_message")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains(testErrorMessage)
  }

  @Test
  fun testNoResultsMessageIsDisplayedWhenNoSuggestions() {
    // Mock the repository to return no suggestions
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (List<Location>) -> Unit
          onSuccess(emptyList())
          null
        }
        .`when`(locationRepository)
        .search(any(), any(), any())

    // Set the composable content
    composeTestRule.setContent {
      LocationSearchCustomScreen(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    composeTestRule.onNodeWithTag("input_search_field").performTextInput("Unknown Place")

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("no_results_message").assertExists().assertIsDisplayed()
  }

  @Test
  fun backButtonIsClickable() {
    composeTestRule.setContent {
      LocationSearchCustomScreen(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }
    composeTestRule
        .onNodeWithTag("input_search_field_clickable")
        .assertExists()
        .assertHasClickAction()
  }
}
