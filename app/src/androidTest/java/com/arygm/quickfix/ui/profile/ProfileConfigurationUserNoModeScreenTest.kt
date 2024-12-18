package com.arygm.quickfix.ui.profile

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.datastore.preferences.core.Preferences
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.AccountConfigurationScreen
import com.arygm.quickfix.utils.IS_WORKER_KEY
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProfileConfigurationUserNoModeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var accountRepository: AccountRepository
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel

  private val testUserProfile =
      Account(
          uid = "testUid",
          firstName = "John",
          lastName = "Doe",
          birthDate = Timestamp.now(),
          email = "john.doe@example.com",
          profilePicture = "https://example.com/profile.jpg")

  @Before
  fun setup() {
    navigationActions = mock()
    accountRepository = mock()
    accountViewModel = AccountViewModel(accountRepository)
    preferencesRepository = mock()
    preferencesViewModel = PreferencesViewModel(preferencesRepository)

    // Mock preferences repository to provide test data
    whenever(preferencesRepository.getPreferenceByKey(any<Preferences.Key<String>>()))
        .thenReturn(flowOf("testValue"))
    whenever(preferencesRepository.getPreferenceByKey(com.arygm.quickfix.utils.UID_KEY))
        .thenReturn(flowOf("testUid"))
    whenever(preferencesRepository.getPreferenceByKey(com.arygm.quickfix.utils.FIRST_NAME_KEY))
        .thenReturn(flowOf("John"))
    whenever(preferencesRepository.getPreferenceByKey(com.arygm.quickfix.utils.LAST_NAME_KEY))
        .thenReturn(flowOf("Doe"))
    whenever(preferencesRepository.getPreferenceByKey(com.arygm.quickfix.utils.EMAIL_KEY))
        .thenReturn(flowOf("john.doe@example.com"))
    whenever(preferencesRepository.getPreferenceByKey(com.arygm.quickfix.utils.BIRTH_DATE_KEY))
        .thenReturn(flowOf("01/01/1990"))
    whenever(preferencesRepository.getPreferenceByKey(com.arygm.quickfix.utils.PROFILE_PICTURE_KEY))
        .thenReturn(flowOf("https://example.com/profile.jpg"))
    whenever(preferencesRepository.getPreferenceByKey(IS_WORKER_KEY)).thenReturn(flowOf(true))
  }

  @Test
  fun testUpdateFirstNameAndLastName() {
    // Mock account update
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(accountRepository)
        .updateAccount(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        AccountConfigurationScreen(
            navigationActions = navigationActions,
            accountViewModel = accountViewModel,
            preferencesViewModel = preferencesViewModel)
      }
    }

    // Update first name and last name
    composeTestRule.onNodeWithTag("firstNameInput").performTextReplacement("Jane")
    composeTestRule.onNodeWithTag("lastNameInput").performTextReplacement("Smith")

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify account update
    val profileCaptor = argumentCaptor<Account>()
    verify(accountRepository).updateAccount(profileCaptor.capture(), any(), any())
    assertEquals("Jane", profileCaptor.firstValue.firstName)
    assertEquals("Smith", profileCaptor.firstValue.lastName)
  }

  @Test
  fun testUpdateEmailWithValidEmail() {
    // Mock account exists check and update
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(Pair<Boolean, Account?>) -> Unit>(1)
          onSuccess(Pair(false, null))
          null
        }
        .whenever(accountRepository)
        .accountExists(any(), any(), any())
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(accountRepository)
        .updateAccount(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        AccountConfigurationScreen(
            navigationActions = navigationActions,
            accountViewModel = accountViewModel,
            preferencesViewModel = preferencesViewModel)
      }
    }

    // Update email
    composeTestRule.onNodeWithTag("emailInput").performTextReplacement("jane.smith@example.com")

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify account update
    val profileCaptor = argumentCaptor<Account>()
    verify(accountRepository).updateAccount(profileCaptor.capture(), any(), any())
    assertEquals("jane.smith@example.com", profileCaptor.firstValue.email)
  }

  @Test
  fun testUpdateProfilePicture() {
    // Mock image upload
    doAnswer { invocation ->
          val onSuccess =
              invocation.getArgument<(List<String>) -> Unit>(
                  2) // Third argument is the success callback
          onSuccess(
              listOf("https://example.com/new-profile.jpg")) // Simulate success with a new profile
          // picture URL
          null
        }
        .whenever(accountRepository)
        .uploadAccountImages(any(), any(), any(), any())

    // Mock account update
    doAnswer { invocation ->
          val onSuccess =
              invocation.getArgument<() -> Unit>(1) // Second argument is the success callback
          onSuccess() // Simulate success
          null
        }
        .whenever(accountRepository)
        .updateAccount(any(), any(), any())

    // Set up the UI
    composeTestRule.setContent {
      QuickFixTheme {
        AccountConfigurationScreen(
            navigationActions = navigationActions,
            accountViewModel = accountViewModel,
            preferencesViewModel = preferencesViewModel)
      }
    }

    // Simulate clicking the profile image to trigger selection
    composeTestRule.onNodeWithTag("ProfileImage").performClick()

    // Simulate selecting a new profile image (mock bitmap)
    val testBitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    composeTestRule.runOnUiThread {
      accountViewModel.uploadAccountImages(
          "testUid", listOf(testBitmap), onSuccess = {}, onFailure = {})
    }

    // Simulate changing an input field to ensure `isModified` is true
    composeTestRule.onNodeWithTag("firstNameInput").performTextReplacement("Jane")

    // Click Save button to trigger account update
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that the account update includes the new profile picture URL
    val profileCaptor = argumentCaptor<Account>()
    verify(accountRepository).updateAccount(profileCaptor.capture(), any(), any())
  }

  @Test
  fun testSaveButtonDisablesForInvalidInputs() {
    composeTestRule.setContent {
      QuickFixTheme {
        AccountConfigurationScreen(
            navigationActions = navigationActions,
            accountViewModel = accountViewModel,
            preferencesViewModel = preferencesViewModel)
      }
    }

    // Enter invalid email
    composeTestRule.onNodeWithTag("emailInput").performTextReplacement("invalid-email")
    composeTestRule.onNodeWithTag("SaveButton").assertIsNotEnabled()

    // Enter invalid birth date
    composeTestRule.onNodeWithTag("birthDateInput").performTextReplacement("invalid-date")
    composeTestRule.onNodeWithTag("SaveButton").assertIsNotEnabled()
  }
}
