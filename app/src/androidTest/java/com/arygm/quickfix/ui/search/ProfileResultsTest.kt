package com.arygm.quickfix.ui.search

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.ProfileResults
import com.google.firebase.Timestamp
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileResultsTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var accountViewModel: AccountViewModel
  private lateinit var searchViewModel: SearchViewModel

  @Before
  fun setup() {
    accountViewModel = mockk(relaxed = true)
    searchViewModel = mockk(relaxed = true)

    // Mock calculateDistance to return a fixed distance
    every { searchViewModel.calculateDistance(any(), any(), any(), any()) } returns 10.0

    // Mock AccountViewModel fetchUserAccount
    every { accountViewModel.fetchUserAccount(any(), captureLambda()) } answers
        {
          val uid = firstArg<String>()
          val lambda = secondArg<(Account?) -> Unit>()
          lambda(
              when (uid) {
                "worker0" ->
                    Account(
                        uid = "worker0",
                        firstName = "John",
                        lastName = "Doe",
                        email = "",
                        birthDate = Timestamp.now())
                "worker1" ->
                    Account(
                        uid = "worker1",
                        firstName = "Jane",
                        lastName = "Smith",
                        email = "",
                        birthDate = Timestamp.now())
                else -> null
              })
        }
  }

  @Test
  fun profileResults_displaysWorkerProfiles_correctly() {
    // Test data: profiles, images, and location
    val testProfiles =
        listOf(
            WorkerProfile(
                uid = "worker0",
                displayName = "John Doe",
                fieldOfWork = "Plumbing",
                reviews = ArrayDeque(listOf()),
                location = Location(40.0, 70.0),
                price = 49.0),
            WorkerProfile(
                uid = "worker1",
                displayName = "Jane Smith",
                fieldOfWork = "Electrical",
                reviews = ArrayDeque(listOf()),
                location = Location(41.0, 71.0),
                price = 59.0))

    val dummyBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    val profileImagesMap = mapOf("worker0" to dummyBitmap, "worker1" to dummyBitmap)
    val bannerImagesMap = mapOf("worker0" to dummyBitmap, "worker1" to dummyBitmap)
    val baseLocation = Location(0.0, 0.0)

    // Set the ProfileResults composable
    composeTestRule.setContent {
      ProfileResults(
          profiles = testProfiles,
          searchViewModel = searchViewModel,
          accountViewModel = accountViewModel,
          onBookClick = { _, _, _, _ -> },
          profileImagesMap = profileImagesMap,
          bannerImagesMap = bannerImagesMap,
          baseLocation = baseLocation,
          screenHeight = 1000.dp)
    }

    // Allow coroutines to complete
    composeTestRule.waitForIdle()

    // Verify first profile
    composeTestRule.onNodeWithTag("worker_profile_result0").assertIsDisplayed()
    composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
    composeTestRule.onNodeWithText("Plumbing").assertIsDisplayed()
    composeTestRule.onNodeWithText("49.0").assertIsDisplayed()

    // Verify second profile
    composeTestRule.onNodeWithTag("worker_profile_result1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Jane Smith").assertIsDisplayed()
    composeTestRule.onNodeWithText("Electrical").assertIsDisplayed()
    composeTestRule.onNodeWithText("59.0").assertIsDisplayed()
  }
}
