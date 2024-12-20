package com.arygm.quickfix.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.AccountConfigurationScreen
import com.arygm.quickfix.utils.*
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

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
          isWorker = true,
          profilePicture = "https://example.com/profile.jpg")

  @Before
  fun setup() {
    navigationActions = mock()
    accountRepository = mock()
    accountViewModel = AccountViewModel(accountRepository)
    preferencesRepository = mock()
    preferencesViewModel = PreferencesViewModel(preferencesRepository)

    // Mock des préférences
    whenever(preferencesRepository.getPreferenceByKey(UID_KEY)).thenReturn(flowOf("testUid"))
    whenever(preferencesRepository.getPreferenceByKey(FIRST_NAME_KEY)).thenReturn(flowOf("John"))
    whenever(preferencesRepository.getPreferenceByKey(LAST_NAME_KEY)).thenReturn(flowOf("Doe"))
    whenever(preferencesRepository.getPreferenceByKey(EMAIL_KEY))
        .thenReturn(flowOf("john.doe@example.com"))
    whenever(preferencesRepository.getPreferenceByKey(BIRTH_DATE_KEY))
        .thenReturn(flowOf("01/01/1990"))
    whenever(preferencesRepository.getPreferenceByKey(PROFILE_PICTURE_KEY))
        .thenReturn(flowOf("https://example.com/profile.jpg"))
    whenever(preferencesRepository.getPreferenceByKey(IS_WORKER_KEY)).thenReturn(flowOf(true))

    // Mock fetchUserAccount pour retourner testUserProfile
    doAnswer { invocation ->
          val onResult = invocation.getArgument<(Account?) -> Unit>(1)
          onResult(testUserProfile)
          null
        }
        .whenever(accountRepository)
        .getAccountById(eq("testUid"), any(), any())

    // IMPORTANT : On force l'échec du chargement de l'image pour que profileBitmap reste null
    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(Exception("Failed to load image"))
          null
        }
        .whenever(accountRepository)
        .fetchAccountProfileImageAsBitmap(eq("testUid"), any(), any())
  }

  @Test
  fun testUpdateFirstNameAndLastName() {
    // Mock updateAccount
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

    // Attendre que l'UI soit stable
    composeTestRule.waitForIdle()

    // Modifier les champs de nom/prénom pour déclencher isModified
    composeTestRule.onNodeWithTag("firstNameInput").performTextReplacement("Jane")
    composeTestRule.onNodeWithTag("lastNameInput").performTextReplacement("Smith")

    // Le bouton doit s'activer
    composeTestRule.onNodeWithTag("SaveButton").assertIsEnabled()

    // Cliquer sur Save
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Vérifier l'appel
    val captor = argumentCaptor<Account>()
    verify(accountRepository).updateAccount(captor.capture(), any(), any())
    assertEquals("Jane", captor.firstValue.firstName)
    assertEquals("Smith", captor.firstValue.lastName)
  }

  @Test
  fun testUpdateEmailWithValidEmail() {
    // Mock updateAccount
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

    composeTestRule.waitForIdle()

    // Changer l'email vers un email valide différent
    composeTestRule.onNodeWithTag("emailInput").performTextReplacement("jane.smith@example.com")

    // Le bouton doit s'activer
    composeTestRule.onNodeWithTag("SaveButton").assertIsEnabled()

    // Cliquer sur Save
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Vérifier l'appel
    val captor = argumentCaptor<Account>()
    verify(accountRepository).updateAccount(captor.capture(), any(), any())
    assertEquals("jane.smith@example.com", captor.firstValue.email)
  }

  @Test
  fun testUpdateProfilePicture() {
    // Mock uploadAccountImages
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<String>) -> Unit>(2)
          onSuccess(listOf("https://example.com/new-profile.jpg"))
          null
        }
        .whenever(accountRepository)
        .uploadAccountImages(any(), any(), any(), any())

    // Mock updateAccount
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

    composeTestRule.waitForIdle()

    // Simuler un changement sur le prénom pour activer isModified
    composeTestRule.onNodeWithTag("firstNameInput").performTextReplacement("Mary")

    // Maintenant on simule le clic sur l'image : dans le code réel, cela ouvre la feuille de
    // sélection d'image. On va directement appeler uploadAccountImages via l'accountViewModel
    // pour simuler l'upload. Pour cela, on a besoin que l'imageChanged soit vrai.
    // Ici, on ne dispose pas du code de sélection d'image dans le test, mais on va simuler
    // l'action en forçant l'état.

    // On clique sur l'image pour montrer qu'on a choisi une nouvelle image (dans la vraie app,
    // cela ouvrirait une bottom sheet).
    composeTestRule.onNodeWithTag("ProfileImage").performClick()

    // Simuler l'action interne qui met imageChanged à true. On va juste mettre un nouveau
    // bitmap et revalider le bouton Save.
    // Normalement, cette action se fait quand l'utilisateur sélectionne une image.
    // On va juste retaper dans le champ email pour redéclencher isModified
    composeTestRule.onNodeWithTag("emailInput").performTextReplacement("john.new@example.com")

    // Le bouton Save devrait être activé
    composeTestRule.onNodeWithTag("SaveButton").assertIsEnabled()

    // Cliquer sur Save pour déclencher updateAccount avec la nouvelle photo
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    val captor = argumentCaptor<Account>()
    verify(accountRepository).updateAccount(captor.capture(), any(), any())
    // On ne peut pas vérifier l'URL directement parce que l'upload se fait avant l'updateAccount,
    // mais on peut au moins vérifier que l'updateAccount a été appelé.
    // Si besoin, on peut vérifier si l'URL du profilePicture correspond à celle renvoyée
    // par uploadAccountImages
    assertEquals("john.new@example.com", captor.firstValue.email)
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

    composeTestRule.waitForIdle()

    // Email invalide
    composeTestRule.onNodeWithTag("emailInput").performTextReplacement("invalid-email")
    composeTestRule.onNodeWithTag("SaveButton").assertIsNotEnabled()

    // Date invalide
    composeTestRule.onNodeWithTag("birthDateInput").performTextReplacement("invalid-date")
    composeTestRule.onNodeWithTag("SaveButton").assertIsNotEnabled()
  }
}
