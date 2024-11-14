package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.account.AccountRepositoryFirestore
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class SearchWorkerResultScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var searchViewModel: SearchViewModel
  private lateinit var workerRepository: WorkerProfileRepositoryFirestore
  private lateinit var categoryRepository: CategoryRepositoryFirestore
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var accountRepository: AccountRepositoryFirestore

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    // Initialize Mockito
    MockitoAnnotations.openMocks(this)

    // Mock dependencies
    navigationActions = mock(NavigationActions::class.java)
    workerRepository = mock(WorkerProfileRepositoryFirestore::class.java)
    categoryRepository = mock(CategoryRepositoryFirestore::class.java)
    accountRepository = mock(AccountRepositoryFirestore::class.java)

    // Initialize ViewModels with mocked repositories
    searchViewModel = SearchViewModel(workerRepository, categoryRepository)
    accountViewModel = AccountViewModel(accountRepository)
  }

  @Test
  fun testTopAppBarIsDisplayed() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
    // Verify that Back and Search icons are present in the top bar
    composeTestRule.onNodeWithContentDescription("Back").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Search").assertExists().assertIsDisplayed()
  }

  @Test
  fun testTitleAndDescriptionAreDisplayed() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
    // Set the search query and verify that the title and description match the query
    searchViewModel.setSearchQuery("Construction Carpentry")

    // Check if the title with search query text is displayed
    composeTestRule.onNodeWithText("Construction Carpentry").assertExists().assertIsDisplayed()

    // Check if the description with the query text is displayed
    composeTestRule
        .onNodeWithText("This is a sample description for the Construction Carpentry result")
        .assertExists()
        .assertIsDisplayed()
  }

  @Test
  fun testFilterButtonsAreDisplayed() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
    // Verify that all filter buttons in the LazyRow are visible and clickable
    val filterButtonsRow = composeTestRule.onNodeWithTag("filter_buttons_row")

    listOfButtons.forEachIndexed { index, button ->
      // Scroll to each button and check if it's displayed with a click action
      filterButtonsRow.performScrollToIndex(index)
      composeTestRule
          .onNodeWithTag("filter_button_${button.text}")
          .assertExists()
          .assertIsDisplayed()
          .assertHasClickAction()
    }
  }

  @Test
  fun testFilterIconButtonIsDisplayedAndClickable() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
    // Verify that the filter icon button is displayed and has a click action
    composeTestRule
        .onNodeWithContentDescription("Filter")
        .assertExists()
        .assertIsDisplayed()
        .assertHasClickAction()
  }

  @Test
  fun testProfileResultsAreDisplayed() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
    // Scroll through the LazyColumn and verify each profile result is displayed
    val workerProfilesList = composeTestRule.onNodeWithTag("worker_profiles_list")

    repeat(searchViewModel.workerProfiles.value.size) { index ->
      workerProfilesList.performScrollToIndex(index)
      composeTestRule
          .onNodeWithTag("worker_profile_result$index")
          .assertExists()
          .assertIsDisplayed()
    }
  }

  @Test
  fun testEachProfileDisplaysCorrectInfo() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
    // Check that each profile displays the correct information like name, rating, reviews, etc.
    val profile = searchViewModel.workerProfiles.value.firstOrNull()

    profile?.let {

      // Check field of work, rating, and review count are displayed
      composeTestRule.onNodeWithText(profile.fieldOfWork).assertExists().assertIsDisplayed()
      composeTestRule.onNodeWithText(profile.rating.toString()).assertExists().assertIsDisplayed()

      profile.reviews.size.toString().let {
        composeTestRule.onNodeWithText("$it reviews").assertExists().assertIsDisplayed()
      }

      // Check hourly rate if it exists
      profile.hourlyRate?.toString()?.let {
        composeTestRule.onNodeWithText("$it/hr").assertExists().assertIsDisplayed()
      }
    }
  }

  @Test
  fun testNoLocationPlaceholderIsDisplayedWhenLocationIsUnknown() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
    // Verify that profiles with unknown location display "Unknown"
    searchViewModel.workerProfiles.value.forEachIndexed { index, profile ->
      if (profile.location?.name.isNullOrEmpty()) {
        composeTestRule
            .onNodeWithTag("worker_profile_result$index")
            .onChild()
            .assert(hasText("Unknown"))
      }
    }
  }

  @Test
  fun testNavigationBackActionIsInvokedOnBackButtonClick() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
    // Perform click on the back button and verify goBack() is called
    composeTestRule.onNodeWithContentDescription("Back").performClick()
    verify(navigationActions).goBack()
  }

  /*  @Test
  fun testProfileDistanceCalculation() {
    // Mock current location and worker location to test distance calculation
    val currentLocation = Location(
      latitude = 37.7749,
      longitude = -122.4194

    )

    val workerLocation = Location(
      latitude = 37.7740,
      longitude = -122.4310
    )
    searchViewModel.setWorkerProfiles(
      listOf(
        WorkerProfile(
          uid = "123",
          location = workerLocation,
          fieldOfWork = "Electrician",
          rating = 4.8,
          reviews = listOf("Great service"),
          hourlyRate = 25.0
        )
      )
    )
    searchViewModel.setSearchQuery("Electrician")
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
    val distance = searchViewModel.calculateDistance(currentLocation.latitude, currentLocation.longitude, workerLocation.latitude, workerLocation.longitude)

    // Simulate setting current location and triggering distance calculation
    composeTestRule.waitForIdle()

    // Verify that the distance is displayed correctly (mock distance based on locations set above)
    composeTestRule.onNodeWithText("${distance.toInt()} km away").assertExists().assertIsDisplayed()
  }*/

  /*  @Test
  fun testAccountDetailsFetchedAndDisplayed() {
    val rating = 4.5
    val workerProfile = WorkerProfile(
      uid = "123",
      fieldOfWork = "Plumber",
      rating = rating,
      reviews = listOf("Excellent work", "Quick and efficient"),
      hourlyRate = 20.0
    )

    // Set the profile in searchViewModel
    searchViewModel.setWorkerProfiles(listOf(workerProfile))
    searchViewModel.setSearchQuery("Plumber")

    // Trigger UI update
    composeTestRule.waitForIdle()

    // Check that account details are displayed correctly
    composeTestRule.onNodeWithText("Plumber").assertExists().assertIsDisplayed()
  }*/

  /*  @Test
  fun testAvailabilityOfPricePerHourForWorkerProfiles() {
    val hourlyRate = 30.0
    // Set profile in searchViewModel
    searchViewModel.setWorkerProfiles(listOf(WorkerProfile(
      uid = "123",
      fieldOfWork = "Handyman",
      rating = 4.7,
      reviews = listOf("Quick response", "Reliable"),
      hourlyRate = hourlyRate
    )))
    searchViewModel.setSearchQuery("Handyman")
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
    composeTestRule.waitForIdle()

    // Verify that the hourly rate is displayed correctly
    composeTestRule.onNodeWithTag("price").assertExists().assertIsDisplayed().assertTextContains("$hourlyRate")
  }*/

  /*  @Test
  fun testUnknownLocationDisplayedWhenLocationIsNull() {
    val workerProfile = WorkerProfile(
      uid = "123",
      fieldOfWork = "Carpenter",
      rating = 4.2,
      reviews = listOf("Neat and clean work"),
      hourlyRate = 18.0,
      location = null
    )

    // Set profile in searchViewModel
    searchViewModel.setWorkerProfiles(listOf(workerProfile))
    searchViewModel.setSearchQuery("Carpenter")
    composeTestRule.waitForIdle()

    // Verify "Unknown" location text is displayed
    composeTestRule.onNodeWithText("Unknown").assertExists().assertIsDisplayed()
  }*/

  /*  @Test
  fun testProfileDisplaysReviewCount() {
    val workerProfile = WorkerProfile(
      uid = "123",
      fieldOfWork = "Painter",
      rating = 4.3,
      reviews = listOf("Good finish", "Affordable price"),
      hourlyRate = 22.0
    )

    // Set profile in searchViewModel
    searchViewModel.setWorkerProfiles(listOf(workerProfile))
    searchViewModel.setSearchQuery("Painter")
    composeTestRule.waitForIdle()

    // Check that the review count is displayed correctly
    composeTestRule.onNodeWithText("(2+)").assertExists().assertIsDisplayed()
  }*/

  /*
  @Test
  fun testBookButtonClickable() {
    val workerProfile = WorkerProfile(
      uid = "123",
      fieldOfWork = "Electrician",
      rating = 4.8,
      reviews = listOf("Highly skilled"),
      hourlyRate = 35.0
    )

    // Set profile in searchViewModel
    searchViewModel.setWorkerProfiles(listOf(workerProfile))
    composeTestRule.waitForIdle()

    // Ensure the "Book" button is displayed and clickable
    composeTestRule.onNodeWithText("Book").assertExists().assertIsDisplayed().assertHasClickAction()
  }
   */

}
