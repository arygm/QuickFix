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
    navigationActions = mock(NavigationActions::class.java)
    workerRepository = mock(WorkerProfileRepositoryFirestore::class.java)
    categoryRepository = mock(CategoryRepositoryFirestore::class.java)
    searchViewModel = SearchViewModel(workerRepository, categoryRepository)
    accountViewModel = mock(AccountViewModel::class.java)
    composeTestRule.setContent {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
  }

  @Test
  fun testTopAppBarIsDisplayed() {

    composeTestRule.onNodeWithContentDescription("Back").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription("Search").assertExists().assertIsDisplayed()
  }

  @Test
  fun testTitleAndDescriptionAreDisplayed() {
    searchViewModel.setSearchQuery("Construction Carpentry")
    composeTestRule
        .onNodeWithText(searchViewModel.searchQuery.value)
        .assertExists()
        .assertIsDisplayed()

    composeTestRule
        .onNodeWithText(
            "This is a sample description for the ${searchViewModel.searchQuery.value} result")
        .assertExists()
        .assertIsDisplayed()
  }

  @Test
  fun testFilterButtonsAreDisplayed() {
    // Target the LazyRow with the test tag "filter_buttons_row"
    val filterButtonsRow = composeTestRule.onNodeWithTag("filter_buttons_row")

    // Scroll to each button and check if it's displayed
    listOfButtons.forEachIndexed { index, button ->
      // Scroll to the specific index in the LazyRow
      filterButtonsRow.performScrollToIndex(index)

      // Check if the button is displayed after scrolling to its index
      composeTestRule
          .onNodeWithTag("filter_button_${button.text}")
          .assertExists()
          .assertIsDisplayed()
    }
  }

  @Test
  fun testProfileResultsAreDisplayed() {
    // Target the LazyColumn with the test tag "worker_profiles_list"
    val workerProfilesList = composeTestRule.onNodeWithTag("worker_profiles_list")

    // Scroll through the LazyColumn to load each item and verify it’s displayed
    repeat(searchViewModel.workerProfiles.value.size) { index ->
      // Scroll to the specific index in the LazyColumn
      workerProfilesList.performScrollToIndex(index)

      composeTestRule.onNodeWithTag("worker_profile_result$index").assertIsDisplayed()
    }
  }
}
