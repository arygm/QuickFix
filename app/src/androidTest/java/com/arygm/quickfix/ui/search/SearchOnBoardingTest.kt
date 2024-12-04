package com.arygm.quickfix.ui.search

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.google.firebase.Timestamp
import io.mockk.every
import io.mockk.invoke
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
          accountViewModel,
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
          accountViewModel,
          categoryViewModel)
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
          accountViewModel = accountViewModel,
          categoryViewModel = categoryViewModel)
    }

    // Verify initial state (Categories are displayed)
    composeTestRule.onNodeWithText("Categories").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchContent").performTextInput("Painter")

    // Verify state after query input (Categories disappear, Profiles appear)
    composeTestRule.onNodeWithText("Categories").assertDoesNotExist()
    composeTestRule.onNodeWithText("Profiles").assertIsDisplayed()
  }

  @Test
  fun profileContent_displaysWorkerProfiles() {
    // Set up test data
    val testProfiles =
        listOf(
            WorkerProfile(
                uid = "worker1",
                fieldOfWork = "Plumbing",
                rating = 4.5,
                reviews = ArrayDeque(),
                location = null, // Simplify for the test
                price = 50.0,
            ),
            WorkerProfile(
                uid = "worker2",
                fieldOfWork = "Electrical",
                rating = 4.0,
                reviews = ArrayDeque(),
                location = null,
                price = 60.0,
            ))

    // Mock the AccountViewModel to return sample account data
    every { accountViewModel.fetchUserAccount(any(), captureLambda()) } answers
        {
          val uid = firstArg<String>()
          val account =
              when (uid) {
                "worker1" ->
                    Account(
                        uid = "worker1",
                        firstName = "John",
                        lastName = "Doe",
                        email = "",
                        Timestamp.now())
                "worker2" ->
                    Account(
                        uid = "worker2",
                        firstName = "Jane",
                        lastName = "Smith",
                        email = "",
                        Timestamp.now())
                else -> null
              }
          lambda<(Account?) -> Unit>().invoke(account) // Call the callback with the account
        }

    // Set the content with ProfileContent composable
    composeTestRule.setContent {
      ProfileContent(
          profiles = testProfiles,
          listState = rememberLazyListState(),
          searchViewModel = searchViewModel,
          accountViewModel = accountViewModel,
          navigationActions = navigationActions)
    }

    // Advance the clock to allow coroutines to complete
    composeTestRule.waitForIdle()

    // Verify that the profile items are displayed correctly
    composeTestRule.onNodeWithTag("worker_profile_result_0").assertIsDisplayed()
    composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
    composeTestRule.onNodeWithText("Plumbing").assertIsDisplayed()

    composeTestRule.onNodeWithTag("worker_profile_result_1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Jane Smith").assertIsDisplayed()
    composeTestRule.onNodeWithText("Electrical").assertIsDisplayed()
  }
}
