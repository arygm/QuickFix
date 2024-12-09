package com.arygm.quickfix.ui.profile.becomeWorker

import androidx.compose.ui.test.*
import com.arygm.quickfix.model.profile.*
import org.mockito.kotlin.*


/*
class BusinessScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var accountRepository: AccountRepository
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var loggedInAccountViewModel: LoggedInAccountViewModel
  private lateinit var userProfileRepositoryFirestore: ProfileRepository
  private lateinit var workerProfileRepositoryFirestore: ProfileRepository
  private lateinit var categoryRepo: CategoryRepositoryFirestore
  private lateinit var workerViewModel: ProfileViewModel
  private lateinit var categoryViewModel: CategoryViewModel
  private lateinit var locationViewModel: LocationViewModel
  private lateinit var preferencesViewModel: PreferencesViewModel

  private val testUserProfile =
      Account(
          uid = "testUid",
          firstName = "John",
          lastName = "Doe",
          birthDate = Timestamp.now(),
          email = "john.doe@example.com",
          isWorker = false)

  @Before
  fun setup() {
      preferencesViewModel = mock()
      locationViewModel = mock()
    navigationActions = mock()
    userProfileRepositoryFirestore = mock()
    workerProfileRepositoryFirestore = mock()
    accountRepository = mock()
    categoryRepo = mock()
    accountViewModel = AccountViewModel(accountRepository)
    workerViewModel = ProfileViewModel(workerProfileRepositoryFirestore)
    loggedInAccountViewModel =
        LoggedInAccountViewModel(userProfileRepositoryFirestore, workerProfileRepositoryFirestore)
    loggedInAccountViewModel.setLoggedInAccount(testUserProfile)
    categoryViewModel = CategoryViewModel(categoryRepo)
  }

  @Test
  fun testInitialUI() {
    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(
            navigationActions,
            accountViewModel,
            workerViewModel,
            loggedInAccountViewModel,
            preferencesViewModel,
            categoryViewModel,
            locationViewModel)
      }
    }

    // Check UI elements are displayed
    composeTestRule.onNodeWithTag(C.Tag.upgradeToWorkerScaffold).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.upgradeToWorkerTopBar).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.upgradeToWorkerPager).assertIsDisplayed()
  }

  @Test
  fun testBackButtonNavigatesBack() {
    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(
            navigationActions,
            accountViewModel,
            workerViewModel,
            loggedInAccountViewModel,
            preferencesViewModel,
            categoryViewModel,
            locationViewModel)
      }
    }

    composeTestRule.onNodeWithTag("goBackButton").performClick()
    Mockito.verify(navigationActions).goBack()
  }
}

 */
