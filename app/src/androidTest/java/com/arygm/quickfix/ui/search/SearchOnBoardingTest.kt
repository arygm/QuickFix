package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class SearchOnBoardingTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var workerProfileRepo: WorkerProfileRepositoryFirestore
  private lateinit var categoryRepo: CategoryRepositoryFirestore
  private lateinit var searchViewModel: SearchViewModel
  private lateinit var categoryViewModel: CategoryViewModel
  private lateinit var navigationActionsRoot: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    navigationActionsRoot = mock(NavigationActions::class.java)
    workerProfileRepo = mockk(relaxed = true)
    categoryRepo = mockk(relaxed = true)
    searchViewModel = SearchViewModel(workerProfileRepo)
    categoryViewModel = CategoryViewModel(categoryRepo)
  }

  @Test
  fun searchOnBoarding_displaysSearchInput() {
    composeTestRule.setContent {
      SearchOnBoarding(
          navigationActions = navigationActions,
          navigationActionsRoot,
          isUser = true,
          searchViewModel,
          categoryViewModel)
    }

    // Check that the search input field is displayed
    composeTestRule.onNodeWithTag("searchContent").assertIsDisplayed()

    // Enter some text and check if the trailing clear icon appears
    composeTestRule.onNodeWithTag("searchContent").performTextInput("plumbing")
    composeTestRule.onNodeWithTag("clearSearchQueryIcon").assertIsDisplayed()
  }

  @Test
  fun searchOnBoarding_clearsTextOnTrailingIconClick() {
    composeTestRule.setContent {
      SearchOnBoarding(
          navigationActions = navigationActions,
          navigationActionsRoot,
          isUser = true,
          searchViewModel,
          categoryViewModel)
    }

    // Input text into the search field
    val searchInput = composeTestRule.onNodeWithTag("searchContent")
    searchInput.performTextInput("electrician")

    // Click the trailing icon (clear button) and verify the text is cleared
    composeTestRule.onNodeWithTag("clearSearchQueryIcon").performClick()
    searchInput.assertTextEquals("") // Verify the text is cleared
  }
}
