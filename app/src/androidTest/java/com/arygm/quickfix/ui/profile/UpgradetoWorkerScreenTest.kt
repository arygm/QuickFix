package com.arygm.quickfix.ui.profile

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.AnnotatedString
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.*

@RunWith(AndroidJUnit4::class)
class BusinessScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockProfileViewModel = mock<ProfileViewModel>()
  private val mockNavigationActions = mock<NavigationActions>()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun validateButton_updatesProfile_whenFieldsAreFilled() = runTest {
    // Créer une instance réelle de ProfileViewModel avec le faux dépôt
    val fakeRepository = FakeProfileRepository()
    val profileViewModel = ProfileViewModel(fakeRepository)

    // Définir le profil connecté
    val testProfile = createTestProfile()
    profileViewModel.setLoggedInProfile(testProfile)

    // Définir le contenu du Composable
    composeTestRule.setContent {
      BusinessScreen(navigationActions = mockNavigationActions, profileViewModel = profileViewModel)
    }

    // Remplir les champs requis
    composeTestRule.onNodeWithTag("occupationInput").performTextInput("Plumber")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Experienced plumber")
    composeTestRule.onNodeWithTag("hourlyRateInput").performTextInput("50")
    composeTestRule.onNodeWithTag("locationInput").performTextInput("Zurich")

    // Cliquer sur le bouton de validation
    composeTestRule.onNodeWithTag("validateButton").performClick()

    // Attendre que les coroutines se terminent
    composeTestRule.waitForIdle()

    // Vérifier que le profil a été mis à jour dans le dépôt
    val updatedProfile = fakeRepository.profiles.first { it.uid == testProfile.uid }
    assert(updatedProfile.isWorker)
    assert(updatedProfile.fieldOfWork == "Plumber")
    assert(updatedProfile.description == "Experienced plumber")
    assert(updatedProfile.hourlyRate == 50.0)
    assert(updatedProfile.location == GeoPoint(0.0, 0.0))
  }

  // Fonction utilitaire pour créer un profil de test
  private fun createTestProfile(): Profile {
    return Profile(
        uid = "testUid",
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        birthDate = Timestamp.now(),
        description = "Test description",
        isWorker = false,
        fieldOfWork = null,
        hourlyRate = null,
        location = null)
  }

  @Test
  fun testInitialUI() {
    composeTestRule.setContent { QuickFixTheme { BusinessScreen(navigationActions) } }

    // Vérifier que tous les champs et boutons sont affichés
    composeTestRule.onNodeWithTag("occupationInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("descriptionInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("hourlyRateInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("locationInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("validateButton").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Account Circle Icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BusinessAccountTitle").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Account Circle Icon").assertIsDisplayed()
  }

  @Test
  fun testValidateButtonEnabledWhenFieldsAreFilled() {
    composeTestRule.setContent { QuickFixTheme { BusinessScreen(navigationActions) } }

    // Vérifier que le titre est affiché avec le bon texte
    composeTestRule
        .onNodeWithTag("BusinessAccountTitle")
        .assertIsDisplayed()
        .assertTextEquals("Business Account")
    // Remplir tous les champs
    composeTestRule.onNodeWithTag("occupationInput").performTextInput("Plumber")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Experienced plumber")
    composeTestRule.onNodeWithTag("hourlyRateInput").performTextInput("50")
    composeTestRule.onNodeWithTag("locationInput").performTextInput("New York")

    // Cliquer sur le bouton de validation
    composeTestRule.onNodeWithTag("validateButton").performClick()

    // Vérifier que l'action de navigation ou de mise à jour a été déclenchée
    // Ici, vous pouvez vérifier l'appel à profileViewModel.updateProfile si vous le moquez
  }

  @Test
  fun testValidateButtonShowsErrorWhenFieldsAreEmpty() {
    composeTestRule.setContent { QuickFixTheme { BusinessScreen(navigationActions) } }
    composeTestRule.onNodeWithTag("occupationInput").assertExists()

    composeTestRule.onNodeWithTag("occupationInput").printToLog("OccupationInputNode")

    // S'assurer que les champs requis sont vides
    composeTestRule
        .onNodeWithTag("occupationInput")
        .assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("")))
    composeTestRule
        .onNodeWithTag("hourlyRateInput")
        .assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("")))
    composeTestRule
        .onNodeWithTag("locationInput")
        .assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("")))

    // Cliquer sur le bouton de validation
    composeTestRule.onNodeWithTag("validateButton").performClick()

    // Vérifier que le message d'erreur est affiché
    composeTestRule.onNodeWithTag("errorMessage").assertTextEquals("Please fill all fields")
  }

  @Test
  fun testOccupationDropdownMenuDisplaysOptions() {
    composeTestRule.setContent { QuickFixTheme { BusinessScreen(navigationActions) } }

    // Cliquer sur le champ "Occupation" pour ouvrir le menu déroulant
    composeTestRule.onNodeWithTag("occupationDropdownIcon").performClick()

    // Vérifier que les options du menu déroulant sont affichées
    val occupations = listOf("Carpenter", "Painter", "Plumber", "Electrician", "Mechanic")
    occupations.forEach { occupation ->
      composeTestRule.onNodeWithText(occupation).assertIsDisplayed()
    }
  }

  @Test
  fun testSelectingOccupationUpdatesField() {
    composeTestRule.setContent { QuickFixTheme { BusinessScreen(navigationActions) } }

    // Ouvrir le menu déroulant
    composeTestRule.onNodeWithTag("occupationDropdownIcon").performClick()

    // Sélectionner "Plumber"
    composeTestRule.onNodeWithText("Plumber").performClick()

    // Vérifier que le champ "Occupation" est mis à jour
    composeTestRule.onNodeWithTag("occupationInput").assertTextEquals("Plumber")
  }

  @Test
  fun testHourlyRateAcceptsOnlyDigits() {
    composeTestRule.setContent { QuickFixTheme { BusinessScreen(navigationActions) } }

    // Entrer une valeur avec des lettres et des chiffres
    composeTestRule.onNodeWithTag("hourlyRateInput").performTextInput("abc123def")

    // Vérifier que seuls les chiffres sont acceptés
    composeTestRule.onNodeWithTag("hourlyRateInput").assertTextContains("123")
    composeTestRule.onNodeWithTag("hourlyRateInput").performTextClearance()

    composeTestRule.onNodeWithTag("hourlyRateInput").performTextInput("123")

    // Vérifier que seuls les chiffres sont acceptés
    composeTestRule.onNodeWithTag("hourlyRateInput").assertTextContains("123")
  }
}

class FakeProfileRepository : ProfileRepository {
  internal val profiles = mutableListOf<Profile>()
  private val loggedInProfileFlow = MutableStateFlow<Profile?>(null)

  override fun init(onSuccess: () -> Unit) {
    onSuccess()
  }

  override fun getProfiles(onSuccess: (List<Profile>) -> Unit, onFailure: (Exception) -> Unit) {
    onSuccess(profiles)
  }

  override fun filterWorkers(
      hourlyRateThreshold: Double?,
      fieldOfWork: String?,
      onSuccess: (List<Profile>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    TODO("Not yet implemented")
  }

  override fun updateProfile(
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    profiles.removeAll { it.uid == profile.uid }
    profiles.add(profile)
    loggedInProfileFlow.value = profile
    onSuccess()
  }

  override fun deleteProfileById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun profileExists(
      email: String,
      onSuccess: (Pair<Boolean, Profile?>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun getProfileById(
      uid: String,
      onSuccess: (Profile?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val profile = profiles.find { it.uid == uid }
    onSuccess(profile)
  }
}
