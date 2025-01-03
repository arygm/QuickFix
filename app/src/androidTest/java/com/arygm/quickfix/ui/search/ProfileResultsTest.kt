package com.arygm.quickfix.ui.search

import android.graphics.Bitmap
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepositoryFirestore
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.ProfileResults
import com.google.firebase.Timestamp
import io.mockk.every
import io.mockk.invoke
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class ProfileResultsTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var workerProfileRepo: WorkerProfileRepositoryFirestore
  private lateinit var accountRepositoryFirestore: AccountRepositoryFirestore
  private lateinit var categoryRepo: CategoryRepositoryFirestore
  private lateinit var searchViewModel: SearchViewModel
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var categoryViewModel: CategoryViewModel
  private lateinit var navigationActionsRoot: NavigationActions
  private lateinit var workerRepository: ProfileRepository
  private lateinit var workerViewModel: ProfileViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    navigationActionsRoot = mock(NavigationActions::class.java)
    workerProfileRepo = mockk(relaxed = true)
    categoryRepo = mockk(relaxed = true)
    accountRepositoryFirestore = mock(AccountRepositoryFirestore::class.java)
    searchViewModel = SearchViewModel(workerProfileRepo)
    categoryViewModel = CategoryViewModel(categoryRepo)
    accountViewModel = mockk(relaxed = true)

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
  fun profileContent_displaysWorkerProfiles() {
    // Set up test data
    val testProfiles =
        listOf(
            WorkerProfile(
                uid = "worker0",
                fieldOfWork = "Plumbing",
                rating = 3.5,
                reviews = ArrayDeque(),
                location = null, // Simplify for the test
                price = 49.0,
            ),
            WorkerProfile(
                uid = "worker1",
                fieldOfWork = "Electrical",
                rating = 3.0,
                reviews = ArrayDeque(),
                location = null,
                price = 59.0,
            ))

    // Mock the AccountViewModel to return sample account data
    every { accountViewModel.fetchUserAccount(any(), captureLambda()) } answers
        {
          val uid = firstArg<String>()
          val account =
              when (uid) {
                "worker0" ->
                    Account(
                        uid = "worker0",
                        firstName = "John",
                        lastName = "Doe",
                        email = "",
                        Timestamp.now())
                "worker1" ->
                    Account(
                        uid = "worker1",
                        firstName = "Jane",
                        lastName = "Smith",
                        email = "",
                        Timestamp.now())
                else -> null
              }
          lambda<(Account?) -> Unit>().invoke(account)
        }

    // Set the content with ProfileContent composable
    composeTestRule.setContent {
      ProfileResults(
          profiles = testProfiles,
          listState = rememberLazyListState(),
          searchViewModel = searchViewModel,
          accountViewModel = accountViewModel,
          heightRatio = 1.0f,
          workerViewModel = workerViewModel,
          onBookClick = { _, _ -> })
    }

    // Allow coroutines to complete
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
