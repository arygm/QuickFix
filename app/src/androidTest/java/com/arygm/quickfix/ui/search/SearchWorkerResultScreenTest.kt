package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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

@RunWith(AndroidJUnit4::class)
class SearchWorkerResultScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var searchViewModel: SearchViewModel
  private lateinit var workerRepository: WorkerProfileRepositoryFirestore
  private lateinit var categoryRepository: CategoryRepositoryFirestore
  private lateinit var accountViewModel: AccountViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    // Mock dependencies and initialize view models
    navigationActions = mock(NavigationActions::class.java)
    workerRepository = mock(WorkerProfileRepositoryFirestore::class.java)
    categoryRepository = mock(CategoryRepositoryFirestore::class.java)
    searchViewModel = SearchViewModel(workerRepository, categoryRepository)
    accountViewModel = mock(AccountViewModel::class.java)

    // Set the composable content
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
  }

  @Test
  fun testTopAppBarIsDisplayed() {
    // Verify that Back and Search icons are present in the top bar
    composeTestRule.onNodeWithContentDescription("Back").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Search").assertExists().assertIsDisplayed()
  }

  @Test
  fun testTitleAndDescriptionAreDisplayed() {
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
    // Verify that the filter icon button is displayed and has a click action
    composeTestRule
        .onNodeWithContentDescription("Filter")
        .assertExists()
        .assertIsDisplayed()
        .assertHasClickAction()
  }

  @Test
  fun testProfileResultsAreDisplayed() {
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
    // Perform click on the back button and verify goBack() is called
    composeTestRule.onNodeWithContentDescription("Back").performClick()
    verify(navigationActions).goBack()
  }
}
