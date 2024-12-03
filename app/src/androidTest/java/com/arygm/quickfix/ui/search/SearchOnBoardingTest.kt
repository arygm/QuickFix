package com.arygm.quickfix.ui.search

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfile
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
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var navigationActionsRoot: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    navigationActionsRoot = mock(NavigationActions::class.java)
    workerProfileRepo = mockk(relaxed = true)
    categoryRepo = mockk(relaxed = true)
    searchViewModel = SearchViewModel(workerProfileRepo, categoryRepo)
    accountViewModel = mockk(relaxed = true)
  }

  @Test
  fun searchOnBoarding_displaysSearchInput() {
    composeTestRule.setContent {
      SearchOnBoarding(
          navigationActions = navigationActions,
          navigationActionsRoot,
          isUser = true,
          searchViewModel,
          accountViewModel)
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
          accountViewModel)
    }

    // Input text into the search field
    val searchInput = composeTestRule.onNodeWithTag("searchContent")
    searchInput.performTextInput("electrician")

    // Click the trailing icon (clear button) and verify the text is cleared
    composeTestRule.onNodeWithTag("clearSearchQueryIcon").performClick()
    searchInput.assertTextEquals("") // Verify the text is cleared
  }

    @Test
    fun searchOnBoarding_switchesFromCategoriesToProfiles() {
        composeTestRule.setContent {
            SearchOnBoarding(
                navigationActions = navigationActions,
                navigationActionsRoot = navigationActionsRoot,
                isUser = true,
                searchViewModel = searchViewModel,
                accountViewModel = accountViewModel
            )
        }

        // Verify initial state (Categories are displayed)
        composeTestRule.onNodeWithText("Categories").assertIsDisplayed()
        composeTestRule.onNodeWithTag("searchContent").performTextInput("Painter")

        // Verify state after query input (Categories disappear, Profiles appear)
        composeTestRule.onNodeWithText("Categories").assertDoesNotExist()
        composeTestRule.onNodeWithText("Profiles").assertIsDisplayed()
    }

}
