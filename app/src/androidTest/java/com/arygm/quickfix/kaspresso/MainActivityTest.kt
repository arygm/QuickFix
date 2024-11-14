package com.arygm.quickfix.kaspresso

import android.os.Build
import android.util.Log
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.kaspresso.screen.WelcomeScreen
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.Subcategory
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class MainActivityTest : TestCase() {

  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  private val item =
      Category(
          id = "painting",
          name = "Painting",
          description = "Find skilled painters for residential or commercial projects.",
          subcategories =
              listOf(
                  Subcategory(
                      id = "residential_painting",
                      name = "Residential Painting",
                      tags = listOf("Interior Painting", "Exterior Painting", "Cabinet Painting")),
                  Subcategory(
                      id = "commercial_painting",
                      name = "Commercial Painting",
                      tags = listOf("Office Buildings", "Retail Spaces")),
                  Subcategory(
                      id = "decorative_painting",
                      name = "Decorative Painting",
                      tags = listOf("Faux Finishes", "Murals")),
              ))

  @Before
  fun setup() {
    val firestore = FirebaseFirestore.getInstance()
    firestore.useEmulator("10.0.2.2", 8080)
    FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)

    firestore.firestoreSettings =
        FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()

    navigationActions = Mockito.mock(NavigationActions::class.java)
  }

  fun allowPermissionsIfNeeded() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
      // Wait up to 5 seconds for the dialog
      if (device.wait(Until.hasObject(By.text("Only this time")), 5000)) {
        device.findObject(By.text("Only this time")).click()
      }
    }
  }

  @After
  fun tearDown() {
    FirebaseApp.clearInstancesForTest()
    FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    val firestore = FirebaseFirestore.getInstance()
    firestore.firestoreSettings =
        FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(
                true) // Set to true or false as needed for your production environment
            .build()

    // Reinitialize FirebaseAuth without the emulator
    FirebaseAuth.getInstance().signOut()
  }

  @Test
  fun shouldNotBeAbleToReg() = run {
    step("Set up the WelcomeScreen and transit to the register") {
      composeTestRule.activity

      // Wait for the UI to settle
      composeTestRule.waitForIdle()

      // Attempt to grant permissions
      allowPermissionsIfNeeded()
      // Retry the action until it works with a timeout of 10 seconds
      ComposeScreen.onComposeScreen<WelcomeScreen>(composeTestRule) {
        registerButton {
          assertIsDisplayed()
          performClick()
          // Log the click action
          Log.d("TestLog", "Register button clicked")
        }
      }
      composeTestRule.mainClock.advanceTimeBy(2500L)
      composeTestRule.onNodeWithTag("firstNameInput").performTextInput("Ramy")
      composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Hatimy")
      composeTestRule.onNodeWithTag("emailInput").performTextInput("hatimyramy@gmail.com")
      composeTestRule.onNodeWithTag("birthDateInput").performTextInput("28/10/2004")
      composeTestRule.onNodeWithTag("passwordInput").performTextInput("246890357Asefthuk")
      composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("246890357Asefthuk")
      composeTestRule.onNodeWithTag("checkbox").performClick()

      composeTestRule.waitUntil(timeoutMillis = 20000) {
        val buttonNode = composeTestRule.onAllNodesWithTag("registerButton")
        // Check if any button node is not enabled
        buttonNode.fetchSemanticsNodes().any { semanticsNode ->
          semanticsNode.config.getOrNull(SemanticsProperties.Disabled) != null
        }
      }
      composeTestRule.onNodeWithTag("registerButton").assertIsNotEnabled()
    }
  }

  @Test
  fun shouldBeAbleToLogin() = run {
    step("Set up the WelcomeScreen and transit to the register") {
      composeTestRule.activity

      // Wait for the UI to settle
      composeTestRule.waitForIdle()

      // Attempt to grant permissions
      allowPermissionsIfNeeded()
      // Retry the action until it works with a timeout of 10 seconds
      ComposeScreen.onComposeScreen<WelcomeScreen>(composeTestRule) {
        loginButton {
          assertIsDisplayed()
          performClick()
          // Log the click action
        }
      }
      composeTestRule.mainClock.advanceTimeBy(2500L)
      composeTestRule.onNodeWithTag("inputEmail").performTextClearance()
      composeTestRule.onNodeWithTag("inputPassword").performTextClearance()

      composeTestRule.onNodeWithTag("inputEmail").performTextInput("main.activity@test.com")
      composeTestRule.onNodeWithTag("inputPassword").performTextInput("246890357Asefthuk")
      composeTestRule.onNodeWithTag("logInButton").assertIsEnabled()
      composeTestRule.onNodeWithTag("logInButton").performClick()
      composeTestRule.waitUntil("find the BottomNavMenu", timeoutMillis = 20000) {
        composeTestRule.onAllNodesWithTag("BottomNavMenu").fetchSemanticsNodes().isNotEmpty()
      }
      composeTestRule.onRoot().printToLog("TAG")
      // Get the bounds of the node
      // Get the bounds of the BottomNavMenu

      onView(withText("Search")) // Match the TextView that has the text "Hello World"
          .perform(click())
      composeTestRule.waitUntil("find the categories", timeoutMillis = 20000) {
        composeTestRule.onAllNodesWithText(item.name).fetchSemanticsNodes().isNotEmpty()
      }
      composeTestRule.onNodeWithText(item.name).assertIsDisplayed()
      composeTestRule.onNodeWithText(item.name).performClick()
      composeTestRule.waitUntil("find the categories", timeoutMillis = 20000) {
        composeTestRule
            .onAllNodesWithText(item.subcategories[0].name)
            .fetchSemanticsNodes()
            .isNotEmpty()
      }
      composeTestRule.onNodeWithText(item.subcategories[0].name).performClick()
      onView(withText("Dashboard")) // Match the TextView that has the text "Hello World"
          .perform(click())
      onView(withText("Profile")) // Match the TextView that has the text "Hello World"
          .perform(click())

      composeTestRule.waitUntil("find the AccountconfigurationOption", timeoutMillis = 20000) {
        composeTestRule
            .onAllNodesWithTag("AccountconfigurationOption")
            .fetchSemanticsNodes()
            .isNotEmpty()
      }
      updateAccountConfigurationAndVerify(
          composeTestRule, "Ramy", "Hatimy", "17/10/2004", "Ramy Hatimy", 1)

      updateAccountConfigurationAndVerify(
          composeTestRule, "Ramo", "Hatimo", "28/10/2004", "Ramo Hatimo", 2)

      composeTestRule.onNodeWithTag("AccountconfigurationOption").performClick()
      composeTestRule.waitUntil(timeoutMillis = 20000) {
        composeTestRule.onAllNodesWithTag("birthDateInput").fetchSemanticsNodes().isNotEmpty()
      }
      composeTestRule.onNodeWithTag("birthDateInput").assertTextEquals("28/10/2004")
      composeTestRule.onNodeWithTag("goBackButton").performClick()

      composeTestRule.waitUntil("find the SetupyourbusinessaccountOption", timeoutMillis = 20000) {
        composeTestRule
            .onAllNodesWithTag("SetupyourbusinessaccountOption")
            .fetchSemanticsNodes()
            .isNotEmpty()
      }
      composeTestRule.onNodeWithTag("SetupyourbusinessaccountOption").performClick()

      composeTestRule.waitUntil("find the goBackButton", timeoutMillis = 20000) {
        composeTestRule.onAllNodesWithTag("goBackButton").fetchSemanticsNodes().isNotEmpty()
      }
      composeTestRule.onNodeWithTag("goBackButton").performClick()
    }
  }
}

private fun updateAccountConfigurationAndVerify(
    composeTestRule: ComposeTestRule,
    firstName: String,
    lastName: String,
    birthDate: String,
    expectedProfileName: String,
    log: Int
) {
  // Click on account configuration option
  composeTestRule.onNodeWithTag("AccountconfigurationOption").performClick()

  // Wait until the first name input is visible
  composeTestRule.waitUntil("find the firstNameInput $log", timeoutMillis = 20000) {
    composeTestRule.onAllNodesWithTag("firstNameInput").fetchSemanticsNodes().isNotEmpty()
  }

  composeTestRule.onNodeWithTag("firstNameInput").performTextClearance()
  composeTestRule.onNodeWithTag("lastNameInput").performTextClearance()
  composeTestRule.onNodeWithTag("birthDateInput").performTextClearance()

  // Input first name
  composeTestRule.onNodeWithTag("firstNameInput").performTextInput(firstName)

  // Input last name
  composeTestRule.onNodeWithTag("lastNameInput").performTextInput(lastName)

  // Input birthdate
  composeTestRule.onNodeWithTag("birthDateInput").performTextInput(birthDate)

  // Click on save button
  composeTestRule.onNodeWithTag("SaveButton").performClick()

  composeTestRule.waitUntil("find the AccountconfigurationOption $log", timeoutMillis = 20000) {
    composeTestRule
        .onAllNodesWithTag("AccountconfigurationOption")
        .fetchSemanticsNodes()
        .isNotEmpty()
  }

  composeTestRule.waitUntil(20000) {
    val profileNode = composeTestRule.onAllNodesWithTag("ProfileName")
    // Check if there's at least one node with the expected text
    profileNode.fetchSemanticsNodes().any { semanticsNode ->
      val text = semanticsNode.config.getOrNull(SemanticsProperties.Text)?.joinToString()
      text == expectedProfileName
    }
  }
  // Verify that the profile name has been updated correctly
  composeTestRule.onNodeWithTag("ProfileName").assertTextEquals(expectedProfileName)
}
