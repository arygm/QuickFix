package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepositoryFirestore
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer

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

    // Provide test data to searchViewModel
    searchViewModel._workerProfiles.value =
        listOf(
            WorkerProfile(
                uid = "test_uid_1",
                hourlyRate = 1.0,
                reviews = listOf("I hate writing tests"),
                fieldOfWork = "Carpentry",
                rating = 3.0,
                description = "I hate my job",
                location = Location(40.7128, -74.0060)),
        )

    // Mock the getAccountById method to always return a test Account
    doAnswer { invocation ->
          val uid = invocation.arguments[0] as String
          val onSuccess = invocation.arguments[1] as (Account?) -> Unit
          val onFailure = invocation.arguments[2] as (Exception) -> Unit

          // Create a test Account object  import org.mockito.ArgumentMatchers.anyString
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
  fun testNavigationBackActionIsInvokedOnBackButtonClick() {
    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
    // Perform click on the back button and verify goBack() is called
    composeTestRule.onNodeWithContentDescription("Back").performClick()
    verify(navigationActions).goBack()
  }
  
  @Test
  fun testSlidingWindowAppearsOnBookClick() {
    // Set up the content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
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
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
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
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
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
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
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
        .assertTextContains("Exterior Painter") // Replace with expected category

    // Verify the worker address is displayed
    composeTestRule
        .onNodeWithTag("sliding_window_worker_address")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Ecublens, VD") // Replace with expected address
  }

  @Test
  fun testIncludedServicesAreDisplayed() {
    // Set up the content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
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
    val includedServices =
        listOf(
            "Initial Consultation",
            "Basic Surface Preparation",
            "Priming of Surfaces",
            "High-Quality Paint Application",
            "Two Coats of Paint",
            "Professional Cleanup")

    includedServices.forEach { service ->
      composeTestRule.onNodeWithText("• $service").assertExists().assertIsDisplayed()
    }
  }

  @Test
  fun testAddOnServicesAreDisplayed() {
    // Set up the content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
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
    val addOnServices =
        listOf(
            "Detailed Color Consultation",
            "Premium paint Upgrade",
            "Extensive Surface Preparation",
            "Extra Coats for added Durability",
            "Power Washing and Deep Cleaning")

    addOnServices.forEach { service ->
      composeTestRule.onNodeWithText("• $service").assertExists().assertIsDisplayed()
    }
  }

  @Test
  fun testContinueButtonIsDisplayedAndClickable() {
    // Set up the content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }

    // Wait until the worker profiles are displayed
    composeTestRule.waitForIdle()

    // Click on the "Book" button of the first item
    composeTestRule.onAllNodesWithTag("book_button")[0].assertExists().performClick()

    // Wait for the sliding window to appear
    composeTestRule.waitForIdle()

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
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
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
    val tags =
        listOf(
            "Exterior Painting",
            "Interior Painting",
            "Cabinet Painting",
            "Licensed & Insured",
            "Local Worker")

    tags.forEach { tag -> composeTestRule.onNodeWithText(tag).assertExists().assertIsDisplayed() }
  }

  @Test
  fun testSaveButtonTogglesBetweenSaveAndSaved() {
    // Set up the content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }

    // Wait until the worker profiles are displayed
    composeTestRule.waitForIdle()

    // Click on the "Book" button of the first item
    composeTestRule.onAllNodesWithTag("book_button")[0].assertExists().performClick()

    // Wait for the sliding window to appear
    composeTestRule.waitForIdle()

    // Verify the "save" button is displayed
    composeTestRule
        .onNodeWithTag("sliding_window_save_button")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("save")

    // Click on the "save" button
    composeTestRule.onNodeWithTag("sliding_window_save_button").performClick()

    // Wait for the UI to update
    composeTestRule.waitForIdle()

    // Verify the button text changes to "saved"
    composeTestRule
        .onNodeWithTag("sliding_window_save_button")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("saved")

    // Click again to toggle back to "save"
    composeTestRule.onNodeWithTag("sliding_window_save_button").performClick()

    // Wait for the UI to update
    composeTestRule.waitForIdle()

    // Verify the button text changes back to "save"
    composeTestRule
        .onNodeWithTag("sliding_window_save_button")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("save")
  }
}
