package com.arygm.quickfix.ui.profile.becomeWorker

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.profile.*
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.*

class BusinessScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var accountRepository: AccountRepository
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var loggedInAccountViewModel: LoggedInAccountViewModel
  private lateinit var userProfileRepositoryFirestore: ProfileRepository
  private lateinit var workerProfileRepositoryFirestore: ProfileRepository
  private lateinit var workerViewModel: ProfileViewModel

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
    navigationActions = mock()
    userProfileRepositoryFirestore = mock()
    workerProfileRepositoryFirestore = mock()
    accountRepository = mock()
    accountViewModel = AccountViewModel(accountRepository)
    workerViewModel = ProfileViewModel(workerProfileRepositoryFirestore)
    loggedInAccountViewModel =
        LoggedInAccountViewModel(userProfileRepositoryFirestore, workerProfileRepositoryFirestore)
    loggedInAccountViewModel.setLoggedInAccount(testUserProfile)
  }
/*
  @Test
  fun testInitialUI() {
    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(
            navigationActions, accountViewModel, workerViewModel, loggedInAccountViewModel)
      }
    }

    // Check UI elements are displayed
    composeTestRule.onNodeWithTag(C.Tag.upgradeToWorkerScaffold).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.upgradeToWorkerTopBar).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.upgradeToWorkerPager).assertIsDisplayed()
  }

 */
/*
  @Test
  fun testBackButtonNavigatesBack() {
    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(
            navigationActions, accountViewModel, workerViewModel, loggedInAccountViewModel)
      }
    }

    composeTestRule.onNodeWithTag("goBackButton").performClick()
    Mockito.verify(navigationActions).goBack()
  }

 */
}
