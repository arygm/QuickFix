import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.authentication.WelcomeScreen
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class WelcomeScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  private lateinit var navigationActions: NavigationActions
  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel
  private var intentsInitialized = false // Keep track of Intents initialization

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    profileRepository = mock(ProfileRepository::class.java)
    profileViewModel = ProfileViewModel(profileRepository)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.WELCOME)
  }

  @After
  fun tearDown() {
    // Only release Intents if they were initialized
    if (intentsInitialized) {
      Intents.release()
      intentsInitialized = false
    }
  }

  @Test
  fun testInitialState() {
    composeTestRule.setContent { WelcomeScreen(navigationActions, profileViewModel) }

    // Check if the background image is displayed
    composeTestRule.onNodeWithTag("welcomeBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("boxDecoration1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("boxDecoration2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("workerBackground").assertIsDisplayed()

    // Check that the QuickFix logo is displayed
    composeTestRule.onNodeWithTag("quickFixLogo").assertIsDisplayed()

    // Check that the QuickFix text is displayed
    composeTestRule.onNodeWithTag("quickFixText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("quickFixText").assertTextEquals("QuickFix")

    // Check that the buttons are displayed
    composeTestRule.onNodeWithTag("logInButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("logInButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("logInButton").assertTextEquals("LOG IN TO QUICKFIX")
    composeTestRule.onNodeWithTag("RegistrationButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RegistrationButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("RegistrationButton").assertTextEquals("REGISTER TO QUICKFIX")

    // Check if Google button and logo are displayed
    composeTestRule.onNodeWithTag("googleButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("googleButton").assertTextEquals("CONTINUE WITH GOOGLE")
  }

  @Test
  fun testLogInButtonClickNavigatesToLogin() {
    composeTestRule.setContent { WelcomeScreen(navigationActions, profileViewModel) }

    // Click the "LOG IN TO QUICKFIX" button
    composeTestRule.onNodeWithTag("logInButton").performClick()

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    // Verify that the navigation action is triggered for the login screen
    Mockito.verify(navigationActions).navigateTo(Screen.LOGIN)
  }

  @Test
  fun testRegistrationButtonClickNavigatesToInfo() {
    composeTestRule.setContent { WelcomeScreen(navigationActions, profileViewModel) }

    // Click the "REGISTER TO QUICKFIX" button
    composeTestRule.onNodeWithTag("RegistrationButton").performClick()

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    // Verify that the navigation action is triggered for the registration/info screen
    Mockito.verify(navigationActions).navigateTo(Screen.INFO)
  }

  @Test
  fun testGoogleButtonClickSendsIntent() {
    // Initialize Intents for this test
    Intents.init()
    intentsInitialized = true // Mark Intents as initialized

    composeTestRule.setContent { WelcomeScreen(navigationActions, profileViewModel) }

    // Perform click on the Google Sign-In button
    composeTestRule.onNodeWithTag("googleButton").performClick()

    // Assert that an Intent resolving to Google Mobile Services has been sent
    Intents.intended(IntentMatchers.toPackage("com.google.android.gms"))
  }
}
