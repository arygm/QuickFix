package com.arygm.quickfix.ui.search

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextReplacement
import androidx.core.app.ActivityCompat
import androidx.datastore.preferences.core.Preferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepositoryFirestore
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.category.Subcategory
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.dataFields.Review
import com.arygm.quickfix.model.quickfix.QuickFixRepositoryFirestore
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.SearchWorkerResult
import com.arygm.quickfix.utils.LocationHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class SearchWorkerResultScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var searchViewModel: SearchViewModel
  private lateinit var workerRepository: WorkerProfileRepositoryFirestore
  private lateinit var categoryRepository: CategoryRepositoryFirestore
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var accountRepository: AccountRepositoryFirestore
  private lateinit var userProfileRepositoryFirestore: ProfileRepository
  private lateinit var userViewModel: ProfileViewModel
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var preferencesRepositoryDataStore: PreferencesRepository
  private lateinit var quickFixRepositoryFirestore: QuickFixRepositoryFirestore
  private lateinit var quickFixViewModel: QuickFixViewModel
  private lateinit var workerViewModel: ProfileViewModel
  private lateinit var context: Context
  private lateinit var activity: Activity
  private lateinit var locationHelper: LocationHelper
  private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    // Initialize Mockito
    MockitoAnnotations.openMocks(this)
    workerRepository = mock(WorkerProfileRepositoryFirestore::class.java)
    // Mock dependencies
    navigationActions = mock(NavigationActions::class.java)
    workerRepository = mock(WorkerProfileRepositoryFirestore::class.java)
    categoryRepository = mock(CategoryRepositoryFirestore::class.java)
    accountRepository = mock(AccountRepositoryFirestore::class.java)
    quickFixRepositoryFirestore = mock(QuickFixRepositoryFirestore::class.java)
    userProfileRepositoryFirestore = mock(ProfileRepository::class.java)
    preferencesRepositoryDataStore = mock(PreferencesRepository::class.java)
    workerViewModel = ProfileViewModel(workerRepository)

    // Mock the flow returned by the repository
    val mockedPreferenceFlow = MutableStateFlow<Any?>(null)
    whenever(preferencesRepositoryDataStore.getPreferenceByKey(any<Preferences.Key<Any>>()))
        .thenReturn(mockedPreferenceFlow)

    // Initialize PreferencesViewModel with mocked repository
    preferencesViewModel = PreferencesViewModel(preferencesRepositoryDataStore)

    // Initialize other ViewModels with mocked repositories
    searchViewModel = SearchViewModel(workerRepository)
    accountViewModel = AccountViewModel(accountRepository)
    quickFixViewModel = QuickFixViewModel(quickFixRepositoryFirestore)
    userViewModel = ProfileViewModel(userProfileRepositoryFirestore)

    // Provide test data to SearchViewModel
    searchViewModel._subCategoryWorkerProfiles.value =
        listOf(
            WorkerProfile(
                uid = "test_uid_1",
                price = 1.0,
                fieldOfWork = "Carpentry",
                rating = 3.0,
                description = "I hate my job",
                location = Location(40.7128, -74.0060),
                displayName = "Ramo"))

    // Mock the getAccountById method to always return a test Account
    doAnswer { invocation ->
          val uid = invocation.arguments[0] as String
          val onSuccess = invocation.arguments[1] as (Account?) -> Unit
          val onFailure = invocation.arguments[2] as (Exception) -> Unit

          // Create a test Account object
          val testAccount =
              Account(
                  uid = uid,
                  firstName = "TestFirstName",
                  lastName = "TestLastName",
                  email = "test@example.com",
                  birthDate = com.google.firebase.Timestamp.now(),
                  isWorker = true,
                  activeChats = emptyList())
          onSuccess(testAccount)
          null
        }
        .`when`(accountRepository)
        .getAccountById(anyString(), any(), any())

    // Mock fetchUserProfile so that it returns a UserProfile with a "Home" location
    doAnswer { invocation ->
          val uid = invocation.arguments[0] as String
          val onSuccess = invocation.arguments[1] as (Profile?) -> Unit
          val onFailure = invocation.arguments[2] as (Exception) -> Unit

          // Return a user profile with a "Home" location
          val testUserProfile =
              UserProfile(
                  locations = listOf(Location(latitude = 40.0, longitude = -74.0, name = "Home")),
                  announcements = emptyList(),
                  uid = uid)
          onSuccess(testUserProfile)
          null
        }
        .`when`(userProfileRepositoryFirestore)
        .getProfileById(anyString(), any(), any())

    workerViewModel = mockk(relaxed = true)

    // Mock fetchProfileImageAsBitmap
    every { workerViewModel.fetchProfileImageAsBitmap(any(), any(), any()) } answers
        {
          val onSuccess = arg<(Bitmap) -> Unit>(1)
          // Provide a dummy bitmap here (e.g. a solid color bitmap or decode from resources)
          val dummyBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
          onSuccess(dummyBitmap) // Simulate success callback
        }

    // Mock fetchBannerImageAsBitmap
    every { workerViewModel.fetchBannerImageAsBitmap(any(), any(), any()) } answers
        {
          val onSuccess = arg<(Bitmap) -> Unit>(1)
          // Provide another dummy bitmap
          val dummyBitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888)
          onSuccess(dummyBitmap) // Simulate success callback
        }
  }

  @Test
  fun testTopAppBarIsDisplayed() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }
    // Verify that Back and Search icons are present in the top bar
    composeTestRule.onNodeWithContentDescription("Back").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Search").assertExists().assertIsDisplayed()
  }

  @Test
  fun testTitleAndDescriptionAreDisplayed() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }
    // Set the search query and verify that the title and description match the query
    searchViewModel.setSearchQuery("Unknown")

    // Check if the description with the query text is displayed
    composeTestRule.onAllNodesWithText("Unknown").assertCountEquals(3)
  }

  @Test
  fun testFilterButtonsAreDisplayed() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Wait for the UI to settle
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    // Verify that the LazyRow for filter buttons is visible
    val filterButtonsRow = composeTestRule.onNodeWithTag("lazy_filter_row")
    filterButtonsRow.assertExists().assertIsDisplayed()

    // Define the expected button texts
    val expectedButtons =
        listOf("Location", "Service Type", "Availability", "Highest Rating", "Price Range")

    // Verify each button exists, is displayed, and clickable
    expectedButtons.forEachIndexed { index, buttonText ->
      // Scroll to the button index (important for last two buttons in LazyRow)
      filterButtonsRow.performScrollToIndex(index)

      // Assert button properties
      composeTestRule
          .onNodeWithText(buttonText)
          .assertExists()
          .assertIsDisplayed()
          .assertHasClickAction()
    }
  }

  @Test
  fun testFilterIconButtonIsDisplayedAndClickable() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
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
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
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
  fun testNavigationBackActionIsInvokedOnBackButtonClick() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }
    // Perform click on the back button and verify goBack() is called
    composeTestRule.onNodeWithContentDescription("Back").performClick()
    verify(navigationActions).goBack()
  }

  @Test
  fun testSlidingWindowAppearsOnBookClick() {
    // Set up the content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Wait for the UI to settle
    composeTestRule.waitForIdle()

    // Scroll to ensure the item is composed
    composeTestRule.onNodeWithTag("worker_profiles_list").performScrollToIndex(0)

    // Click on the "Book" button
    composeTestRule.onNodeWithTag("book_button").assertExists().performClick()

    // Wait for the sliding window to appear
    composeTestRule.waitForIdle()

    // Check that the sliding window content is displayed
    composeTestRule.onNodeWithTag("sliding_window_content").assertExists().assertIsDisplayed()
  }

  @Test
  fun testBannerImageIsDisplayed() {
    // Set up the content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Wait until the worker profiles are displayed
    composeTestRule.waitForIdle()

    // Click on the "Book" button of the first item
    composeTestRule.onAllNodesWithTag("book_button")[0].assertExists().performClick()

    // Wait for the sliding window to appear
    composeTestRule.waitForIdle()

    // Verify the banner image is displayed
    composeTestRule.onNodeWithTag("sliding_window_banner_image").assertExists().assertIsDisplayed()
  }

  @Test
  fun testProfilePictureIsDisplayed() {
    // Set up the content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Wait until the worker profiles are displayed
    composeTestRule.waitForIdle()

    // Click on the "Book" button of the first item
    composeTestRule.onAllNodesWithTag("book_button")[0].assertExists().performClick()

    // Wait for the sliding window to appear
    composeTestRule.waitForIdle()

    // Verify the profile picture is displayed
    composeTestRule
        .onNodeWithTag("sliding_window_profile_picture")
        .assertExists()
        .assertIsDisplayed()
  }

  @Test
  fun testWorkerCategoryAndAddressAreDisplayed() {
    // Set up the content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Wait until the worker profiles are displayed
    composeTestRule.waitForIdle()

    // Click on the "Book" button of the first item
    composeTestRule.onAllNodesWithTag("book_button")[0].assertExists().performClick()

    // Wait for the sliding window to appear
    composeTestRule.waitForIdle()

    // Verify the worker category is displayed
    composeTestRule
        .onNodeWithTag("sliding_window_worker_category")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Ramo") // Replace with expected category

    // Verify the worker address is displayed
    composeTestRule
        .onNodeWithTag("sliding_window_worker_address")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("New York") // Replace with expected address
  }

  @Test
  fun testIncludedServicesAreDisplayed() {
    // Set up the content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Wait until the worker profiles are displayed
    composeTestRule.waitForIdle()

    // Click on the "Book" button of the first item
    composeTestRule.onAllNodesWithTag("book_button")[0].assertExists().performClick()

    // Wait for the sliding window to appear
    composeTestRule.waitForIdle()

    // Verify the included services section is displayed
    composeTestRule
        .onNodeWithTag("sliding_window_included_services_column")
        .assertExists()
        .assertIsDisplayed()

    // Check for each included service
    val includedServices = listOf("Basic Consultation", "Service Inspection")

    includedServices.forEach { service ->
      composeTestRule.onNodeWithText("• $service").assertExists().assertIsDisplayed()
    }
  }

  @Test
  fun testAddOnServicesAreDisplayed() {
    // Set up the content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Wait until the worker profiles are displayed
    composeTestRule.waitForIdle()

    // Click on the "Book" button of the first item
    composeTestRule.onAllNodesWithTag("book_button")[0].assertExists().performClick()

    // Wait for the sliding window to appear
    composeTestRule.waitForIdle()

    // Verify the add-on services section is displayed
    composeTestRule
        .onNodeWithTag("sliding_window_addon_services_column")
        .assertExists()
        .assertIsDisplayed()

    // Check for each add-on service
    val addOnServices = listOf("Express Delivery", "Premium Materials")

    addOnServices.forEach { service ->
      composeTestRule.onNodeWithText("• $service").assertExists().assertIsDisplayed()
    }
  }

  @Test
  fun testContinueButtonIsDisplayedAndClickable() {
    // Set up the content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Wait until the worker profiles are displayed
    composeTestRule.waitForIdle()

    // Click on the "Book" button of the first item
    composeTestRule.onAllNodesWithTag("book_button")[0].assertExists().performClick()

    // Wait for the sliding window to appear
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("starsRow").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithTag("Star_0").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Star_1").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Star_2").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Star_3").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Star_4").assertExists().assertIsDisplayed()

    // Verify the "Continue" button is displayed and clickable
    composeTestRule
        .onNodeWithTag("sliding_window_continue_button")
        .assertExists()
        .assertIsDisplayed()
        .assertHasClickAction()
  }

  @Test
  fun testTagsAreDisplayed() {
    // Set up the content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Wait until the worker profiles are displayed
    composeTestRule.waitForIdle()

    // Click on the "Book" button of the first item
    composeTestRule.onAllNodesWithTag("book_button")[0].assertExists().performClick()

    // Wait for the sliding window to appear
    composeTestRule.waitForIdle()

    // Verify the tags section is displayed
    composeTestRule.onNodeWithTag("sliding_window_tags_flow_row").assertExists().assertIsDisplayed()

    // Check for each tag
    val tags = listOf("Reliable", "Experienced", "Professional")

    tags.forEach { tag -> composeTestRule.onNodeWithText(tag).assertExists().assertIsDisplayed() }
  }

  @Test
  fun testWorkerFilteringByAvailabilityDays() {
    // Set up test worker profiles with specific working hours and unavailability
    val worker1 =
        WorkerProfile(
            uid = "worker1",
            fieldOfWork = "Painter",
            rating = 4.5,
            workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
            unavailability_list = listOf(LocalDate.now()),
            location = Location(40.7128, -74.0060))

    val worker2 =
        WorkerProfile(
            uid = "worker2",
            fieldOfWork = "Electrician",
            rating = 4.0,
            workingHours = Pair(LocalTime.of(8, 0), LocalTime.of(16, 0)),
            unavailability_list = emptyList(),
            location = Location(40.7128, -74.0060))

    val worker3 =
        WorkerProfile(
            uid = "worker3",
            fieldOfWork = "Plumber",
            rating = 5.0,
            workingHours = Pair(LocalTime.of(10, 0), LocalTime.of(18, 0)),
            unavailability_list = emptyList(),
            location = Location(40.7128, -74.0060))

    // Update the searchViewModel with these test workers
    searchViewModel._subCategoryWorkerProfiles.value = listOf(worker1, worker2, worker3)

    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Initially, all workers should be displayed
    composeTestRule.waitForIdle()

    // Verify that all 3 workers are displayed
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(3)

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(3)
    // Simulate clicking the "Availability" filter button
    composeTestRule.onNodeWithText("Availability").performClick()

    // Wait for the bottom sheet to appear
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("availabilityBottomSheet").assertIsDisplayed()
    composeTestRule.onNodeWithTag("timePickerColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("timeInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomSheetColumn").assertIsDisplayed()
    composeTestRule.onNodeWithText("Enter time").assertIsDisplayed()

    val today = LocalDate.now()
    val day = today.dayOfMonth

    val textFields =
        composeTestRule.onAllNodes(hasSetTextAction()).filter(hasParent(hasTestTag("timeInput")))

    // Ensure that we have at least two text fields
    assert(textFields.fetchSemanticsNodes().size >= 2)

    // Set the hour to "07"
    textFields[0].performTextReplacement("10")

    // Set the minute to "00"
    textFields[1].performTextReplacement("00")

    // Find the node representing today's date and perform a click
    composeTestRule
        .onNode(hasText(day.toString()) and hasClickAction() and !hasSetTextAction())
        .performClick()

    composeTestRule.onNodeWithText("OK").performClick()

    composeTestRule.waitForIdle()

    // Verify that 2 workers are displayed
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(2)
  }

  @Test
  fun testWorkerFilteringByAvailabilityHours() {
    // Set up test worker profiles with specific working hours and unavailability
    val worker1 =
        WorkerProfile(
            uid = "worker1",
            fieldOfWork = "Painter",
            rating = 4.5,
            workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
            unavailability_list = emptyList(),
            location = Location(40.7128, -74.0060))

    val worker2 =
        WorkerProfile(
            uid = "worker2",
            fieldOfWork = "Electrician",
            rating = 4.0,
            workingHours = Pair(LocalTime.of(8, 0), LocalTime.of(16, 0)),
            unavailability_list = emptyList(),
            location = Location(40.7128, -74.0060))

    val worker3 =
        WorkerProfile(
            uid = "worker3",
            fieldOfWork = "Plumber",
            rating = 5.0,
            workingHours = Pair(LocalTime.of(10, 0), LocalTime.of(18, 0)),
            unavailability_list = emptyList(),
            location = Location(40.7128, -74.0060))

    // Update the searchViewModel with these test workers
    searchViewModel._subCategoryWorkerProfiles.value = listOf(worker1, worker2, worker3)

    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Initially, all workers should be displayed
    composeTestRule.waitForIdle()

    // Verify that all 3 workers are displayed
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(3)

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(3)
    // Simulate clicking the "Availability" filter button
    composeTestRule.onNodeWithText("Availability").performClick()

    // Wait for the bottom sheet to appear
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("availabilityBottomSheet").assertIsDisplayed()
    composeTestRule.onNodeWithTag("timePickerColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("timeInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomSheetColumn").assertIsDisplayed()
    composeTestRule.onNodeWithText("Enter time").assertIsDisplayed()

    val today = LocalDate.now()
    val day = today.dayOfMonth

    val textFields =
        composeTestRule.onAllNodes(hasSetTextAction()).filter(hasParent(hasTestTag("timeInput")))

    // Ensure that we have at least two text fields
    assert(textFields.fetchSemanticsNodes().size >= 2)

    // Set the hour to "07"
    textFields[0].performTextReplacement("08")

    // Set the minute to "00"
    textFields[1].performTextReplacement("00")

    // Find the node representing today's date and perform a click
    composeTestRule
        .onNode(hasText(day.toString()) and hasClickAction() and !hasSetTextAction())
        .performClick()

    composeTestRule.onNodeWithText("OK").performClick()

    composeTestRule.waitForIdle()

    // Verify that one worker is displayed
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(1)
  }

  @Test
  fun testWorkerFilteringByAvailabilityMinutes() {
    // Set up test worker profiles with specific working hours and unavailability
    val worker1 =
        WorkerProfile(
            uid = "worker1",
            fieldOfWork = "Painter",
            rating = 4.5,
            workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
            unavailability_list = emptyList(),
            location = Location(40.7128, -74.0060))

    val worker2 =
        WorkerProfile(
            uid = "worker2",
            fieldOfWork = "Electrician",
            rating = 4.0,
            workingHours = Pair(LocalTime.of(8, 30), LocalTime.of(16, 0)),
            unavailability_list = emptyList(),
            location = Location(40.7128, -74.0060))

    val worker3 =
        WorkerProfile(
            uid = "worker3",
            fieldOfWork = "Plumber",
            rating = 5.0,
            workingHours = Pair(LocalTime.of(10, 0), LocalTime.of(18, 0)),
            unavailability_list = emptyList(),
            location = Location(40.7128, -74.0060))

    // Update the searchViewModel with these test workers
    searchViewModel._subCategoryWorkerProfiles.value = listOf(worker1, worker2, worker3)

    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Initially, all workers should be displayed
    composeTestRule.waitForIdle()

    // Verify that all 3 workers are displayed
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(3)

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(3)
    // Simulate clicking the "Availability" filter button
    composeTestRule.onNodeWithText("Availability").performClick()

    // Wait for the bottom sheet to appear
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("availabilityBottomSheet").assertIsDisplayed()
    composeTestRule.onNodeWithTag("timePickerColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("timeInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomSheetColumn").assertIsDisplayed()
    composeTestRule.onNodeWithText("Enter time").assertIsDisplayed()

    val today = LocalDate.now()
    val day = today.dayOfMonth

    val textFields =
        composeTestRule.onAllNodes(hasSetTextAction()).filter(hasParent(hasTestTag("timeInput")))

    // Ensure that we have at least two text fields
    assert(textFields.fetchSemanticsNodes().size >= 2)

    // Set the hour to "07"
    textFields[0].performTextReplacement("08")

    // Set the minute to "00"
    textFields[1].performTextReplacement("00")

    // Find the node representing today's date and perform a click
    composeTestRule
        .onNode(hasText(day.toString()) and hasClickAction() and !hasSetTextAction())
        .performClick()

    composeTestRule.onNodeWithText("OK").performClick()

    composeTestRule.waitForIdle()

    // Verify that no workers are displayed
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(0)
  }

  @Test
  fun testWorkerFilteringByServices() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                tags = listOf("Exterior Painter", "Interior Painter"),
                rating = 4.5,
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker2",
                tags = listOf("Interior Painter", "Electrician"),
                rating = 4.0,
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker3",
                tags = listOf("Plumber"),
                rating = 5.0,
                location = Location(40.7128, -74.0060)))

    // Set up subcategory tags
    searchViewModel._searchSubcategory.value =
        Subcategory(tags = listOf("Exterior Painter", "Interior Painter", "Electrician", "Plumber"))

    searchViewModel._subCategoryWorkerProfiles.value = workers

    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(1)
    // Click on the "Service Type" filter button
    composeTestRule.onNodeWithText("Service Type").performClick()

    // Wait for the bottom sheet to appear
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("chooseServiceTypeModalSheet").assertIsDisplayed()

    // Simulate selecting "Interior Painter"
    composeTestRule.onNodeWithText("Interior Painter").performClick()
    composeTestRule.onNodeWithText("Apply").performClick()

    composeTestRule.waitForIdle()

    // Verify filtered workers
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(2)
  }

  @Test
  fun testWorkerSortingByRating() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                displayName = "Worker One",
                tags = listOf("Electrician"),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 3.5))),
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker2",
                displayName = "Worker Two",
                tags = listOf("Electrician"),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 4.8))),
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker3",
                displayName = "Worker Three",
                tags = listOf("Electrician"),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 2.9))),
                location = Location(40.7128, -74.0060)))

    // Provide test data to the searchViewModel
    searchViewModel._subCategoryWorkerProfiles.value = workers
    searchViewModel._searchSubcategory.value =
        Subcategory(tags = listOf("Exterior Painter", "Interior Painter", "Electrician", "Plumber"))

    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    // Scroll to the "Highest Rating" button in the LazyRow
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(3)

    // Click on the "Highest Rating" filter button
    composeTestRule.onNodeWithText("Highest Rating").performClick()

    composeTestRule.waitForIdle()

    // Verify that workers are sorted by rating in descending order
    val sortedWorkers = workers.sortedByDescending { it.rating }
    val workerNodes = composeTestRule.onNodeWithTag("worker_profiles_list").onChildren()

    workerNodes.assertCountEquals(sortedWorkers.size)

    sortedWorkers.forEachIndexed { index, worker ->
      workerNodes[index].assert(
          hasAnyChild(hasText("${worker.price.roundToInt()}", substring = true)))
    }
  }

  @Test
  fun testCombinedFilters() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                displayName = "Worker One",
                tags = listOf("Electrician"),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 5.5))),
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker2",
                displayName = "Worker Two",
                tags = listOf("Electrician", "Plumber"),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 4.8))),
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker3",
                displayName = "Worker Three",
                tags = listOf("Plumber"),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 2.9))),
                location = Location(40.7128, -74.0060)))

    // Provide test data to the searchViewModel
    searchViewModel._subCategoryWorkerProfiles.value = workers
    searchViewModel._searchSubcategory.value =
        Subcategory(tags = listOf("Exterior Painter", "Interior Painter", "Electrician", "Plumber"))

    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(1)
    // Apply Service Type filter
    composeTestRule.onNodeWithText("Service Type").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Electrician").performClick()
    composeTestRule.onNodeWithText("Apply").performClick()
    composeTestRule.waitForIdle()

    // Scroll to the "Highest Rating" button in the LazyRow
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(3)

    // Apply Highest Rating filter
    composeTestRule.onNodeWithText("Highest Rating").performClick()
    composeTestRule.waitForIdle()

    // Verify filtered workers
    val filteredWorkers =
        workers.filter { it.tags.contains("Electrician") }.sortedByDescending { it.rating }
    val workerNodes = composeTestRule.onNodeWithTag("worker_profiles_list").onChildren()

    workerNodes.assertCountEquals(filteredWorkers.size)

    filteredWorkers.forEachIndexed { index, worker ->
      workerNodes[index].assert(
          hasAnyChild(hasText("${worker.price.roundToInt()}", substring = true)))
    }
  }

  @Test
  fun testNoMatchingWorkers() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                displayName = "Worker One",
                tags = listOf("Electrician"),
                rating = 4.5,
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker2",
                displayName = "Worker Two",
                tags = listOf("Electrician", "Plumber"),
                rating = 4.8,
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker3",
                displayName = "Worker Three",
                tags = listOf("Plumber"),
                rating = 2.9,
                location = Location(40.7128, -74.0060)))

    // Provide test data to the searchViewModel
    searchViewModel._subCategoryWorkerProfiles.value = workers
    searchViewModel._searchSubcategory.value =
        Subcategory(
            tags =
                listOf(
                    "Carpenter", "Exterior Painter", "Interior Painter", "Electrician", "Plumber"))

    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(1)

    // Apply Service Type filter for a tag that doesn't exist
    composeTestRule.onNodeWithText("Service Type").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Carpenter").performClick() // No workers with "Carpenter" tag
    composeTestRule.onNodeWithText("Apply").performClick()
    composeTestRule.waitForIdle()

    // Verify no workers are displayed
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(0)
  }

  @Test
  fun testPriceRangeFilterDisplaysBottomSheet() {
    // Set the content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(4)
    // Click on the "Price Range" filter button
    composeTestRule.onNodeWithText("Price Range").performClick()

    // Wait for the bottom sheet to appear
    composeTestRule.waitForIdle()

    // Verify that the price range bottom sheet is displayed
    composeTestRule.onNodeWithTag("priceRangeModalSheet").assertExists().assertIsDisplayed()
  }

  @Test
  fun testPriceRangeFilterUpdatesResults() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                price = 150.0,
                fieldOfWork = "Painter",
                rating = 4.5,
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker2",
                price = 560.0,
                fieldOfWork = "Electrician",
                rating = 4.8,
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker3",
                price = 3010.0,
                fieldOfWork = "Plumber",
                rating = 3.9,
                location = Location(40.7128, -74.0060)))

    // Provide test data to the searchViewModel
    searchViewModel._subCategoryWorkerProfiles.value = workers

    // Set the content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(4)
    // Click on the "Price Range" filter button
    composeTestRule.onNodeWithText("Price Range").performClick()

    // Wait for the bottom sheet to appear
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Apply").performClick()

    // Wait for the UI to update
    composeTestRule.waitForIdle()

    val sortedWorkers = listOf(workers[1])
    val workerNodes = composeTestRule.onNodeWithTag("worker_profiles_list").onChildren()

    workerNodes.assertCountEquals(sortedWorkers.size)

    sortedWorkers.forEachIndexed { index, worker ->
      workerNodes[index].assert(
          hasAnyChild(hasText("${worker.price.roundToInt()}", substring = true)))
    }
  }

  @Test
  fun testPriceRangeFilterExcludesWorkersOutsideRange() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                price = 150.0,
                fieldOfWork = "Painter",
                rating = 4.5,
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker2",
                price = 500.0,
                fieldOfWork = "Electrician",
                rating = 4.8,
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker3",
                price = 3001.0,
                fieldOfWork = "Plumber",
                rating = 3.9,
                location = Location(40.7128, -74.0060)))

    // Provide test data to the searchViewModel
    searchViewModel._subCategoryWorkerProfiles.value = workers

    // Set the content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(4)
    // Click on the "Price Range" filter button
    composeTestRule.onNodeWithText("Price Range").performClick()

    // Wait for the bottom sheet to appear
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Apply").performClick()

    // Wait for the UI to update
    composeTestRule.waitForIdle()

    val sortedWorkers = listOf(workers[1])
    val workerNodes = composeTestRule.onNodeWithTag("worker_profiles_list").onChildren()

    workerNodes.assertCountEquals(sortedWorkers.size)

    sortedWorkers.forEachIndexed { index, worker ->
      workerNodes[index].assert(
          hasAnyChild(hasText("${worker.price.roundToInt()}", substring = true)))
    }
  }

  @Test
  fun testLocationFilterApplyAndClear() {
    // Set up test workers with various locations
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                location = Location(40.0, -74.0, "Home"),
                fieldOfWork = "Painter",
                rating = 4.5),
            WorkerProfile(
                uid = "worker2",
                location = Location(45.0, -75.0, "Far"),
                fieldOfWork = "Electrician",
                rating = 4.0))

    // Provide test data to the searchViewModel
    searchViewModel._subCategoryWorkerProfiles.value = workers

    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Initially, both workers should be displayed
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(2)

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    // Scroll to the "Location" button in the LazyRow if needed
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(1)

    // Open the Location filter bottom sheet
    composeTestRule.onNodeWithText("Location").performClick()
    composeTestRule.waitForIdle()

    // Verify bottom sheet is displayed
    composeTestRule.onNodeWithTag("locationFilterModalSheet").assertIsDisplayed()

    // Select "Home" location
    composeTestRule.onNodeWithText("Home").performClick()

    // Click Apply
    composeTestRule.onNodeWithTag("applyButton").performClick()
    composeTestRule.waitForIdle()

    // Verify that only the worker at "Home" is displayed (worker1)
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(1)

    // Open Location filter again to clear
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(1)
    composeTestRule.onNodeWithText("Location").performClick()
    composeTestRule.waitForIdle()

    // Verify bottom sheet
    composeTestRule.onNodeWithTag("locationFilterModalSheet").assertIsDisplayed()

    // Clear the filter
    composeTestRule.onNodeWithTag("resetButton").performClick()
    composeTestRule.waitForIdle()

    // Verify that we are back to the initial state (2 workers displayed)
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(2)
  }

  @Test
  fun testClearingOneFilterWhileKeepingOthers() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                fieldOfWork = "Painter",
                rating = 4.5,
                location = Location(40.0, -74.0, "Home"),
                tags = listOf("Interior Painter")),
            WorkerProfile(
                uid = "worker2",
                fieldOfWork = "Electrician",
                rating = 4.0,
                location = Location(45.0, -75.0, "Far"),
                tags = listOf("Electrician")),
            WorkerProfile(
                uid = "worker3",
                fieldOfWork = "Plumber",
                rating = 3.5,
                location = Location(42.0, -74.5, "Work"),
                tags = listOf("Plumber")))

    searchViewModel._subCategoryWorkerProfiles.value = workers
    searchViewModel._searchSubcategory.value =
        Subcategory(tags = listOf("Interior Painter", "Electrician", "Plumber"))

    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    composeTestRule.waitForIdle()
    // Initially, all 3 workers
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(3)

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(1)
    // Apply Service Type filter = "Interior Painter"
    composeTestRule.onNodeWithText("Service Type").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Interior Painter").performClick()
    composeTestRule.onNodeWithText("Apply").performClick()
    composeTestRule.waitForIdle()

    // Now only worker1 matches
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(1)

    // Apply Location filter to get even more specific (Assume "Home")
    composeTestRule
        .onNodeWithTag("lazy_filter_row")
        .performScrollToIndex(1) // scroll to "Location" if needed
    composeTestRule.onNodeWithText("Location").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Home").performClick()
    composeTestRule.onNodeWithTag("applyButton").performClick()
    composeTestRule.waitForIdle()

    // Still only worker1 (since it was the only one anyway)
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(1)

    // Now clear the Location filter but keep the Service Type filter
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(1)
    composeTestRule.onNodeWithText("Location").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("resetButton").performClick()
    composeTestRule.waitForIdle()

    // After clearing Location, we should still have only the Service Type filter applied
    // That means still only worker1 should be visible
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(1)
  }

  @Test
  fun testClearAvailabilityFilter() {
    val worker1 =
        WorkerProfile(
            uid = "worker1",
            fieldOfWork = "Painter",
            rating = 4.5,
            workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
            unavailability_list = listOf(LocalDate.now()),
            location = Location(40.7128, -74.0060))
    val worker2 =
        WorkerProfile(
            uid = "worker2",
            fieldOfWork = "Electrician",
            rating = 4.0,
            workingHours = Pair(LocalTime.of(8, 0), LocalTime.of(16, 0)),
            unavailability_list = emptyList(),
            location = Location(40.7128, -74.0060))

    searchViewModel._subCategoryWorkerProfiles.value = listOf(worker1, worker2)

    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    composeTestRule.waitForIdle()
    // Initially 2 workers
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(2)

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(3)
    // Apply Availability filter for today at 10:00 (both should be available)
    composeTestRule.onNodeWithText("Availability").performClick()
    composeTestRule.waitForIdle()

    val today = LocalDate.now().dayOfMonth
    val textFields =
        composeTestRule.onAllNodes(hasSetTextAction()).filter(hasParent(hasTestTag("timeInput")))

    // Ensure that we have at least two text fields
    assert(textFields.fetchSemanticsNodes().size >= 2)

    textFields[0].performTextReplacement("10")

    textFields[1].performTextReplacement("00")

    // Find the node representing today's date and perform a click
    composeTestRule
        .onNode(hasText(today.toString()) and hasClickAction() and !hasSetTextAction())
        .performClick()

    composeTestRule.onNodeWithText("OK").performClick()

    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(1)

    // Clear the Availability filter
    composeTestRule.onNodeWithText("Availability").performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onAllNodes(hasText("Clear"))
        .filter(!hasTestTag("filter_button_Clear"))[0]
        .performClick()
    composeTestRule.waitForIdle()

    // With availability cleared and no other filters applied, we should still see 2 workers
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(2)
  }

  @Test
  fun testTogglingRatingFilterOff() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "w1",
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 3.0))),
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "w2",
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 4.5))),
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "w3",
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 2.0))),
                location = Location(40.7128, -74.0060)))

    searchViewModel._subCategoryWorkerProfiles.value = workers
    // Initially, no rating filter applied, workers are in initial order
    // We'll toggle the rating filter on, then off.

    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    composeTestRule.waitForIdle()
    // Show filter buttons
    composeTestRule.onNodeWithTag("tuneButton").performClick()

    // Apply Highest Rating filter
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(3)
    composeTestRule.onNodeWithText("Highest Rating").performClick()
    composeTestRule.waitForIdle()

    // Now workers should be sorted by rating descending: w2(4.5), w1(3.0), w3(2.0)
    val workerNodes = composeTestRule.onNodeWithTag("worker_profiles_list").onChildren()
    workerNodes.assertCountEquals(workers.size)
    // Verify order by rating text
    workerNodes[0].assert(hasAnyChild(hasText("4.5 ★", substring = true)))
    workerNodes[1].assert(hasAnyChild(hasText("3.0 ★", substring = true)))
    workerNodes[2].assert(hasAnyChild(hasText("2.0 ★", substring = true)))

    // Click again to remove Highest Rating filter
    composeTestRule.onNodeWithText("Highest Rating").performClick()
    composeTestRule.waitForIdle()

    // With the rating filter removed, `reapplyFilters()` is called, and no filters are applied.
    // The default implementation should revert to the original order (the order in
    // `_subCategoryWorkerProfiles`).
    // Check that the initial worker (w1) is now first again.

    val workerNodesAfterRevert = composeTestRule.onNodeWithTag("worker_profiles_list").onChildren()
    workerNodesAfterRevert[0].assert(
        hasAnyChild(hasText("3.0 ★", substring = true))) // w1 first again
    workerNodesAfterRevert[1].assert(hasAnyChild(hasText("4.5 ★", substring = true)))
    workerNodesAfterRevert[2].assert(hasAnyChild(hasText("2.0 ★", substring = true)))
  }

  @Test
  fun testTogglingFilterButtonsVisibility() {
    // Set some workers just so the UI loads normally
    searchViewModel._subCategoryWorkerProfiles.value = listOf(WorkerProfile(uid = "test"))

    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    composeTestRule.waitForIdle()

    // Initially, the lazy_filter_row might not be visible until we click the tune button
    composeTestRule.onNodeWithTag("lazy_filter_row").assertDoesNotExist()

    // Click the tune button to show filter buttons
    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").assertIsDisplayed()

    // Click the tune button again to hide filter buttons
    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("lazy_filter_row").assertDoesNotExist()
  }

  @Test
  fun testServiceTypeSheetNotShownWhenSubcategoryIsNull() {
    // No subcategory set
    searchViewModel._searchSubcategory.value = null
    // Workers to display something
    searchViewModel._subCategoryWorkerProfiles.value = listOf(WorkerProfile(uid = "w1"))

    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    composeTestRule.waitForIdle()
    // Show filter buttons
    composeTestRule.onNodeWithTag("tuneButton").performClick()

    // Attempt to open the Service Type filter
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(1)
    composeTestRule.onNodeWithText("Service Type").performClick()

    composeTestRule.waitForIdle()

    // Since searchSubcategory is null, ChooseServiceTypeSheet should not appear
    composeTestRule.onNodeWithTag("chooseServiceTypeModalSheet").assertDoesNotExist()
  }

  @Test
  fun testWorkerFilteringByServicesTwiceBehavesCorrectly() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                tags = listOf("Exterior Painter", "Interior Painter"),
                rating = 4.5,
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker2",
                tags = listOf("Interior Painter", "Electrician"),
                rating = 4.0,
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker3",
                price = 777.0,
                tags = listOf("Plumber"),
                rating = 5.0,
                location = Location(40.7128, -74.0060)))

    // Set up subcategory tags
    searchViewModel._searchSubcategory.value =
        Subcategory(tags = listOf("Exterior Painter", "Interior Painter", "Plumber", "Electrician"))

    searchViewModel._subCategoryWorkerProfiles.value = workers

    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(1)
    // Click on the "Service Type" filter button
    composeTestRule.onNodeWithText("Service Type").performClick()

    // Wait for the bottom sheet to appear
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("chooseServiceTypeModalSheet").assertIsDisplayed()

    // Simulate selecting "Interior Painter"
    composeTestRule.onNodeWithText("Interior Painter").performClick()
    composeTestRule.onNodeWithText("Apply").performClick()

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(2)

    composeTestRule.onNodeWithText("Service Type").performClick()

    // Wait for the bottom sheet to appear
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("chooseServiceTypeModalSheet").assertIsDisplayed()

    // Simulate selecting "Interior Painter"
    composeTestRule.onNodeWithText("Interior Painter").performClick()
    composeTestRule.onNodeWithText("Plumber").performClick()
    composeTestRule.onNodeWithText("Apply").performClick()

    composeTestRule.waitForIdle()

    // Verify filtered workers
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(1)

    val sortedWorkers = listOf(workers[2])
    val workerNodes = composeTestRule.onNodeWithTag("worker_profiles_list").onChildren()

    workerNodes.assertCountEquals(sortedWorkers.size)

    sortedWorkers.forEachIndexed { index, worker ->
      workerNodes[index].assert(
          hasAnyChild(hasText("${worker.price.roundToInt()}", substring = true)))
    }
  }

  @Test
  fun testWorkerFilteringByAvailabilityTwiceBehavesCorrectly() {
    // Set up test worker profiles with specific working hours and unavailability
    val worker1 =
        WorkerProfile(
            uid = "worker1",
            fieldOfWork = "Painter",
            rating = 4.5,
            workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
            unavailability_list = listOf(LocalDate.now()),
            location = Location(40.7128, -74.0060))

    val worker2 =
        WorkerProfile(
            uid = "worker2",
            fieldOfWork = "Electrician",
            rating = 4.0,
            workingHours = Pair(LocalTime.of(8, 0), LocalTime.of(16, 0)),
            unavailability_list = listOf(LocalDate.now().plusDays(1)),
            location = Location(40.7128, -74.0060))

    val worker3 =
        WorkerProfile(
            uid = "worker3",
            fieldOfWork = "Plumber",
            rating = 5.0,
            workingHours = Pair(LocalTime.of(10, 0), LocalTime.of(18, 0)),
            unavailability_list = listOf(LocalDate.now()),
            location = Location(40.7128, -74.0060))

    // Update the searchViewModel with these test workers
    searchViewModel._subCategoryWorkerProfiles.value = listOf(worker1, worker2, worker3)

    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Initially, all workers should be displayed
    composeTestRule.waitForIdle()

    // Verify that all 3 workers are displayed
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(3)

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(3)
    // Simulate clicking the "Availability" filter button
    composeTestRule.onNodeWithText("Availability").performClick()

    // Wait for the bottom sheet to appear
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("availabilityBottomSheet").assertIsDisplayed()
    composeTestRule.onNodeWithTag("timePickerColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("timeInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomSheetColumn").assertIsDisplayed()
    composeTestRule.onNodeWithText("Enter time").assertIsDisplayed()

    val today = LocalDate.now()
    val day = today.dayOfMonth

    val textFields =
        composeTestRule.onAllNodes(hasSetTextAction()).filter(hasParent(hasTestTag("timeInput")))

    // Ensure that we have at least two text fields
    assert(textFields.fetchSemanticsNodes().size >= 2)

    // Set the hour to "07"
    textFields[0].performTextReplacement("10")

    // Set the minute to "00"
    textFields[1].performTextReplacement("00")

    // Find the node representing today's date and perform a click
    composeTestRule
        .onNode(hasText(day.toString()) and hasClickAction() and !hasSetTextAction())
        .performClick()

    composeTestRule.onNodeWithText("OK").performClick()

    composeTestRule.waitForIdle()

    // Verify that 2 workers are displayed
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(1)

    composeTestRule.onNodeWithText("Availability").performClick()

    composeTestRule.waitForIdle()

    val textFields2 =
        composeTestRule.onAllNodes(hasSetTextAction()).filter(hasParent(hasTestTag("timeInput")))

    // Ensure that we have at least two text fields
    assert(textFields2.fetchSemanticsNodes().size >= 2)

    // Set the hour to "07"
    textFields2[0].performTextReplacement("10")

    // Set the minute to "00"
    textFields2[1].performTextReplacement("00")

    // Find the node representing today's date and perform a click
    composeTestRule
        .onNode(
            hasText(LocalDate.now().dayOfMonth.toString()) and
                hasClickAction() and
                !hasSetTextAction())
        .performClick()

    composeTestRule.onNodeWithText("OK").performClick()

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(1)
  }

  @Test
  fun testLocationFilterReselectBehavesCorrectly() {
    // Set up test workers with various locations
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                location = Location(40.0, -74.0, "Home"),
                fieldOfWork = "Painter",
                rating = 4.5),
            WorkerProfile(
                uid = "worker2",
                location = Location(45.0, -75.0, "Far"),
                fieldOfWork = "Electrician",
                rating = 4.0))

    // Provide test data to the searchViewModel
    searchViewModel._subCategoryWorkerProfiles.value = workers

    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }

    // Initially, both workers should be displayed
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(2)

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    // Scroll to the "Location" button in the LazyRow if needed
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(1)

    // Open the Location filter bottom sheet
    composeTestRule.onNodeWithText("Location").performClick()
    composeTestRule.waitForIdle()

    // Verify bottom sheet is displayed
    composeTestRule.onNodeWithTag("locationFilterModalSheet").assertIsDisplayed()

    composeTestRule.onNodeWithTag("applyButton").assertIsNotEnabled()

    // Select "Home" location
    composeTestRule.onNodeWithText("Home").performClick()

    // Click Apply
    composeTestRule.onNodeWithTag("applyButton").performClick()
    composeTestRule.waitForIdle()

    // Verify that only the worker at "Home" is displayed (worker1)
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(1)

    // Open Location filter again to clear
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(1)
    composeTestRule.onNodeWithText("Location").performClick()
    composeTestRule.waitForIdle()

    // Verify bottom sheet
    composeTestRule.onNodeWithTag("locationFilterModalSheet").assertIsDisplayed()

    composeTestRule.onNodeWithTag("applyButton").assertIsEnabled()

    composeTestRule.onNodeWithText("Home").performClick()

    // Clear the filter
    composeTestRule.onNodeWithTag("resetButton").performClick()
    composeTestRule.waitForIdle()

    // Verify that we are back to the initial state (2 workers displayed)
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(2)

    composeTestRule.onNodeWithText("Location").performClick()
    composeTestRule.waitForIdle()

    // Verify bottom sheet
    composeTestRule.onNodeWithTag("locationFilterModalSheet").assertIsDisplayed()

    composeTestRule.onNodeWithTag("applyButton").assertIsNotEnabled()
    composeTestRule.onNodeWithTag("resetButton").assertIsNotEnabled()
  }

  @Test
  fun testEmergencyUpdatesResults() {
    context = mock(Context::class.java)
    activity = mock(Activity::class.java)
    fusedLocationProviderClient = mock(FusedLocationProviderClient::class.java)
    // Create a spy of LocationHelper
    locationHelper = spy(LocationHelper(context, activity, fusedLocationProviderClient))

    `when`(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)
    `when`(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    // Mock location enabled
    val locationManager = mock(LocationManager::class.java)
    `when`(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManager)
    `when`(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true)
    `when`(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true)

    // Mock permissions check
    `when`(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)
    `when`(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    // Mock fusedLocationProviderClient.lastLocation
    val mockLocation = mock(android.location.Location::class.java)
    `when`(mockLocation.latitude).thenReturn(0.0)
    `when`(mockLocation.longitude).thenReturn(0.0)

    val mockTask = mock(Task::class.java) as Task<android.location.Location>
    `when`(mockTask.isSuccessful).thenReturn(true)
    `when`(mockTask.result).thenReturn(mockLocation)
    `when`(fusedLocationProviderClient.lastLocation).thenReturn(mockTask)

    // Mock addOnCompleteListener
    `when`(mockTask.addOnCompleteListener(Mockito.any())).thenAnswer { invocation ->
      val listener = invocation.arguments[0] as OnCompleteListener<android.location.Location>
      listener.onComplete(mockTask)
      mockTask
    }

    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                price = 150.0,
                fieldOfWork = "Painter",
                rating = 4.5,
                workingHours = Pair(LocalTime.of(0, 0), LocalTime.of(23, 59)),
                location = Location(45.0, -75.0)),
            WorkerProfile(
                uid = "worker2",
                price = 560.0,
                fieldOfWork = "Electrician",
                rating = 4.8,
                workingHours = Pair(LocalTime.of(0, 0), LocalTime.of(23, 59)),
                location = Location(40.7128, -74.0060)),
            WorkerProfile(
                uid = "worker3",
                price = 600.0,
                fieldOfWork = "Plumber",
                rating = 3.9,
                workingHours = Pair(LocalTime.of(0, 0), LocalTime.of(23, 59)),
                location = Location(40.0, -74.0)))

    // Provide test data to the searchViewModel
    searchViewModel._subCategoryWorkerProfiles.value = workers

    // Set the content
    composeTestRule.setContent {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          locationHelper = locationHelper,
          workerViewModel = workerViewModel)
    }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(3)

    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("lazy_filter_row").performScrollToIndex(5)
    // Click on the "Price Range" filter button
    composeTestRule.onNodeWithText("Emergency").performClick()

    // Wait for the UI to update
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(3)

    val sortedWorkers = listOf(workers[2], workers[1], workers[0])
    val workerNodes = composeTestRule.onNodeWithTag("worker_profiles_list").onChildren()

    workerNodes.assertCountEquals(sortedWorkers.size)

    sortedWorkers.forEachIndexed { index, worker ->
      workerNodes[index].assert(
          hasAnyChild(hasText("${worker.price.roundToInt()}", substring = true)))
    }

    composeTestRule.onNodeWithText("Emergency").performClick()
    composeTestRule.onNodeWithTag("worker_profiles_list").onChildren().assertCountEquals(3)

    val sortedWorkers1 = listOf(workers[0], workers[1], workers[2])
    val workerNodes1 = composeTestRule.onNodeWithTag("worker_profiles_list").onChildren()

    workerNodes1.assertCountEquals(sortedWorkers.size)

    sortedWorkers1.forEachIndexed { index, worker ->
      workerNodes[index].assert(
          hasAnyChild(hasText("${worker.price.roundToInt()}", substring = true)))
    }
  }
}
