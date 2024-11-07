package com.arygm.quickfix.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.TopLevelDestinations
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class GoogleInfoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var accountRepository: AccountRepository
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var loggedInAccountViewModel: LoggedInAccountViewModel
    private lateinit var userProfileRepository: ProfileRepository
    private lateinit var userProfileViewModel: ProfileViewModel
    private lateinit var navigationActions: NavigationActions

    private val testAccount = Account(
        uid = "test_uid",
        firstName = "",
        lastName = "",
        email = "test@example.com",
        birthDate = Timestamp.now(),
        isWorker = false
    )

    @Before
    fun setup() {
        // Initialize Mocked Repositories
        accountRepository = mock()
        userProfileRepository = mock()
        val workerProfileRepository: ProfileRepository = mock()

        // Initialize ViewModels with mocked repositories
        accountViewModel = AccountViewModel(accountRepository)
        userProfileViewModel = ProfileViewModel(userProfileRepository)

        // Create an instance of LoggedInAccountViewModel with mocked repositories
        loggedInAccountViewModel = LoggedInAccountViewModel(
            userProfileRepo = userProfileRepository,
            workerProfileRepo = workerProfileRepository
        )

        // Set the loggedInAccount in the ViewModel
        loggedInAccountViewModel.loggedInAccount_.value = testAccount

        // Mock the navigation actions
        navigationActions = mock()

        // Ensure that we're on the main thread
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() {
        composeTestRule.setContent {
            GoogleInfoScreen(
                navigationActions = navigationActions,
                loggedInAccountViewModel = loggedInAccountViewModel,
                accountViewModel = accountViewModel,
                userViewModel = userProfileViewModel
            )
        }

        composeTestRule.onNodeWithTag("InfoBox").assertIsDisplayed()
        composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("contentBox").assertIsDisplayed()
        composeTestRule.onNodeWithTag("decorationBox").assertIsDisplayed()
        composeTestRule.onNodeWithTag("welcomeText").assertIsDisplayed()
        // Check that all input fields are present
        composeTestRule.onNodeWithTag("firstNameInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("lastNameInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("birthDateInput").assertIsDisplayed()

        // Check that the button exists but is initially disabled
        composeTestRule.onNodeWithTag("nextButton").assertIsDisplayed().assertIsNotEnabled()
    }

    @Test
    fun testInvalidDateShowsError() {
        composeTestRule.setContent {
            GoogleInfoScreen(
                navigationActions = navigationActions,
                loggedInAccountViewModel = loggedInAccountViewModel,
                accountViewModel = accountViewModel,
                userViewModel = userProfileViewModel
            )
        }

        // Input an invalid birth date
        composeTestRule.onNodeWithTag("birthDateInput").performTextInput("99/99/9999")

        // Assert that the date error is shown
        composeTestRule.onNodeWithText("INVALID DATE").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nextButton").assertIsNotEnabled()
    }

    @Test
    fun testNextButtonEnabledWhenFormIsValid() {
        // Mock the accountViewModel.updateAccount to call onSuccess
        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(accountRepository).updateAccount(any(), any(), any())

        // Mock the loggedInAccountViewModel.setLoggedInAccount
        // It's a real method, so we don't need to mock it

        composeTestRule.setContent {
            GoogleInfoScreen(
                navigationActions = navigationActions,
                loggedInAccountViewModel = loggedInAccountViewModel,
                accountViewModel = accountViewModel,
                userViewModel = userProfileViewModel
            )
        }

        // Fill out valid inputs
        composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
        composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
        composeTestRule.onNodeWithTag("birthDateInput").performTextInput("01/01/1990")

        // Assert that the "NEXT" button is now enabled
        composeTestRule.onNodeWithTag("nextButton").assertIsEnabled()

        // Click the button
        composeTestRule.onNodeWithTag("nextButton").performClick()

        // Verify that accountViewModel.updateAccount was called
        verify(accountRepository).updateAccount(any(), any(), any())

        // Verify that navigationActions.navigateTo was called
        verify(navigationActions).navigateTo(TopLevelDestinations.HOME)
    }

    @Test
    fun testNextButtonDisabledWhenFormIncomplete() {
        composeTestRule.setContent {
            GoogleInfoScreen(
                navigationActions = navigationActions,
                loggedInAccountViewModel = loggedInAccountViewModel,
                accountViewModel = accountViewModel,
                userViewModel = userProfileViewModel
            )
        }

        // Fill only partial inputs
        composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
        composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")

        // Assert that the "NEXT" button is still disabled
        composeTestRule.onNodeWithTag("nextButton").assertIsNotEnabled()
    }

    @Test
    fun testBackButtonNavigatesBack() {
        // Mock accountViewModel.deleteAccountById to do nothing
        doAnswer {}.`when`(accountRepository).deleteAccountById(any(), any(), any())

        // Mock userViewModel.deleteProfileById to do nothing
        doAnswer {}.`when`(userProfileRepository).deleteProfileById(any(), any(), any())

        composeTestRule.setContent {
            GoogleInfoScreen(
                navigationActions = navigationActions,
                loggedInAccountViewModel = loggedInAccountViewModel,
                accountViewModel = accountViewModel,
                userViewModel = userProfileViewModel
            )
        }

        // Click the back button
        composeTestRule.onNodeWithTag("goBackButton").performClick()

        // Verify that accountViewModel.deleteAccountById was called
        verify(accountRepository).deleteAccountById(eq("test_uid"), any(), any())

        // Verify that userViewModel.deleteProfileById was called
        verify(userProfileRepository).deleteProfileById(eq("test_uid"), any(), any())

    }
}
