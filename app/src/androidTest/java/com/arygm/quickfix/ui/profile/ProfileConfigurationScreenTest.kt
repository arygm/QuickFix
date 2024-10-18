import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.profile.ProfileConfigurationScreen
import com.google.firebase.Timestamp
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class ProfileConfigurationScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var profileViewModel: ProfileViewModel
  private val mockProfile =
      Profile(
          uid = "123",
          firstName = "John",
          lastName = "Doe",
          email = "john.doe@example.com",
          birthDate = Timestamp.now(),
          description = "Test user",
          isWorker = false,
          fieldOfWork = "N/A",
          hourlyRate = 0.0)

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)

    profileViewModel = mock(ProfileViewModel::class.java)

    `when`(profileViewModel.loggedInProfile).thenReturn(MutableStateFlow(mockProfile).asStateFlow())
  }

  @Test
  fun profileConfigurationScreenDisplaysCorrectly() {
    composeTestRule.setContent {
      ProfileConfigurationScreen(navigationActions, profileViewModel = profileViewModel)
    }

    composeTestRule.onNodeWithTag("AccountConfigurationTopAppBar").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("AccountConfigurationTitle")
        .assertTextEquals("Account configuration")
    composeTestRule.onNodeWithTag("ProfileImage").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileName").assertTextEquals("John Doe")
  }

  @Test
  fun inputFieldsAreDisplayed() {
    composeTestRule.setContent {
      ProfileConfigurationScreen(navigationActions, profileViewModel = profileViewModel)
    }

    composeTestRule.onNodeWithTag("firstNameInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lastNameInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("emailInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("birthDateInput").assertIsDisplayed()
  }

  @Test
  fun inputsHaveInitialValue() {
    profileViewModel.setLoggedInProfile(mockProfile)
    composeTestRule.setContent {
      ProfileConfigurationScreen(navigationActions, profileViewModel = profileViewModel)
    }

    composeTestRule.onNodeWithTag("firstNameInput").assertTextEquals(mockProfile.firstName)
    composeTestRule.onNodeWithTag("lastNameInput").assertTextEquals(mockProfile.lastName)
    composeTestRule.onNodeWithTag("emailInput").assertTextEquals(mockProfile.email)
  }

  @Test
  fun backButtonFunctionality() {
    composeTestRule.setContent {
      ProfileConfigurationScreen(navigationActions, profileViewModel = profileViewModel)
    }

    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }

  @Test
  fun invalidEmailDisplaysError() {
    composeTestRule.setContent {
      ProfileConfigurationScreen(navigationActions, profileViewModel = profileViewModel)
    }

    composeTestRule.onNodeWithTag("emailInput").performTextInput("invalid-email")
    composeTestRule.onNodeWithTag("emailInput").assert(hasText("INVALID EMAIL"))
  }

  @Test
  fun invalidBirthdateDisplaysError() {
    composeTestRule.setContent {
      ProfileConfigurationScreen(navigationActions, profileViewModel = profileViewModel)
    }

    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("invalid-date")
    composeTestRule.onNodeWithTag("birthDateInput").assert(hasText("INVALID DATE"))
  }

  @Test
  fun saveButtonWithInvalidDateShowsToast() {
    composeTestRule.setContent {
      ProfileConfigurationScreen(navigationActions, profileViewModel = profileViewModel)
    }

    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("invalid-date")
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that the toast message is displayed
    composeTestRule
        .onNodeWithTag("ToastMessage") // Assuming you have a way to identify the Toast
        .assertIsDisplayed() // Adjust based on how you implement Toast handling in the UI
  }

  @Test
  fun profileNameIsDisplayedCorrectly() {
    val expectedName = "John Doe"
    composeTestRule.setContent {
      ProfileConfigurationScreen(navigationActions, profileViewModel = profileViewModel)
    }

    composeTestRule.onNodeWithTag("ProfileName").assertTextEquals(expectedName)
  }

  @Test
  fun testUpdateProfileAndNavigation() {
    // Mock data for the profile fields
    val uid = "sampleUid"
    val firstName = "John"
    val lastName = "Doe"
    val email = "johndoe@example.com"
    val calendar = Calendar.getInstance()
    val description = "Test description"
    val isWorker = true
    val fieldOfWork = "Engineering"
    val hourlyRate = 50.0

    // Act - call the updateProfile function
    profileViewModel.updateProfile(
        Profile(
            uid = uid,
            firstName = firstName,
            lastName = lastName,
            email = email,
            birthDate = Timestamp(calendar.time),
            description = description,
            isWorker = isWorker,
            fieldOfWork = fieldOfWork,
            hourlyRate = hourlyRate))

    // Verify that updateProfile was called with the expected parameters
    verify(profileViewModel)
        .updateProfile(
            Profile(
                uid = uid,
                firstName = firstName,
                lastName = lastName,
                email = email,
                birthDate = Timestamp(calendar.time),
                description = description,
                isWorker = isWorker,
                fieldOfWork = fieldOfWork,
                hourlyRate = hourlyRate))

    // Verify that navigationActions.goBack() was called
    verify(navigationActions).goBack()
  }
}
