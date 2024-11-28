package com.arygm.quickfix.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ProfileScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Declare lateinit variables for dependencies
  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var navigationActions: NavigationActions
  private lateinit var userProfileRepositoryFirestore: UserProfileRepositoryFirestore
  private lateinit var workerProfileRepositoryFirestore: WorkerProfileRepositoryFirestore
  private lateinit var loggedInAccountViewModel: LoggedInAccountViewModel
  private lateinit var navigationActionsRoot: NavigationActions
  private lateinit var firebaseAuth: FirebaseAuth

  @Before
  fun setup() {
    // Mock dependencies
    mockFirestore = mock(FirebaseFirestore::class.java)
    navigationActions = mock(NavigationActions::class.java)
    firebaseAuth = mock(FirebaseAuth::class.java) // Mock FirebaseAuth

    // Create real repository instances using mocked Firestore
    userProfileRepositoryFirestore = UserProfileRepositoryFirestore(mockFirestore)
    workerProfileRepositoryFirestore = WorkerProfileRepositoryFirestore(mockFirestore)

    // Create the actual LoggedInAccountViewModel with the repositories
    loggedInAccountViewModel =
        LoggedInAccountViewModel(
            userProfileRepo = userProfileRepositoryFirestore,
            workerProfileRepo = workerProfileRepositoryFirestore)

    // Mock navigation actions for testing navigation behavior
    navigationActionsRoot = mock(NavigationActions::class.java)
  }

  @Test
  fun profileScreenDisplaysCorrectly() {
    composeTestRule.setContent { ProfileScreen(navigationActions, loggedInAccountViewModel) }

    // Assert components are displayed
    composeTestRule.onNodeWithTag("ProfileContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileDisplayName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileEmail").assertIsDisplayed()

    // Scroll to the Logout Button
    composeTestRule.onNodeWithTag("LogoutButton").performScrollTo().assertIsDisplayed()

    // Check the LogoutText
    composeTestRule.onNodeWithTag("LogoutText", useUnmergedTree = true).assertTextEquals("Log out")
  }

  @Test
  fun logoutButtonClickNavigatesCorrectly() {
    composeTestRule.setContent { ProfileScreen(navigationActions, loggedInAccountViewModel) }

    // Perform click on logout button
    composeTestRule.onNodeWithTag("LogoutButton").performClick()

    // Verify navigation to the welcome screen
    verify(navigationActions).navigateTo(Screen.WELCOME)
  }

  @Test
  fun settingsOptionsAreDisplayedCorrectly() {
    composeTestRule.setContent { ProfileScreen(navigationActions, loggedInAccountViewModel) }

    // Verify settings options
    val settingsOptions = listOf("My Account", "Preferences", "Saved lists")
    settingsOptions.forEach { label ->
      composeTestRule.onNodeWithTag(label.replace(" ", "") + "Option").assertIsDisplayed()
    }
  }

  @Test
  fun resourcesOptionsAreDisplayedCorrectly() {
    composeTestRule.setContent { ProfileScreen(navigationActions, loggedInAccountViewModel) }

    // Verify resources options
    val resourcesOptions = listOf("Support", "Legal", "Become a Worker")
    resourcesOptions.forEach { label ->
      composeTestRule.onNodeWithTag(label.replace(" ", "") + "Option").assertIsDisplayed()
    }
  }
}
