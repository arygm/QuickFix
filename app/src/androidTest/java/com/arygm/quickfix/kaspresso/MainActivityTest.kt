package com.arygm.quickfix.kaspresso

import android.graphics.Bitmap
import android.location.Location
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
import com.arygm.quickfix.model.category.Scale
import com.arygm.quickfix.model.category.Subcategory
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ressources.C.Tag.professionalInfoScreenCategoryField
import com.arygm.quickfix.ressources.C.Tag.professionalInfoScreenSubcategoryField
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import okhttp3.internal.wait
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.Mockito

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class MainActivityTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  private val item =
      Category(
          id = "carpentry",
          name = "Carpentry",
          description = "Hire experienced carpenters for woodwork and construction tasks.",
          subcategories =
              listOf(
                  Subcategory(
                      id = "construction_carpentry",
                      name = "Construction Carpentry",
                      tags =
                          listOf(
                              "Framing",
                              "Deck Building",
                              "Structural Repairs",
                              "Custom Woodwork",
                              "Building Codes",
                              "Project Management"),
                      scale =
                          Scale(
                              longScale =
                                  "Prices are displayed relative to the cost of framing a standard room.",
                              shortScale = "Standard room framing equivalent"),
                      setServices =
                          listOf(
                              "Framing",
                              "Deck Building",
                              "Door and Window Installation",
                              "Siding Installation",
                              "Roofing Support",
                              "Floor Installation",
                              "Staircase Construction",
                              "Trim and Molding Installation",
                              "Gazebo and Pergola Construction",
                              "Drywall Installation",
                              "Basement Finishing",
                              "Renovations and Additions",
                              "Demolition and Removal",
                              "Insulation Installation",
                              "Clean-Up"))))

  fun allowPermissionsIfNeeded() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
      // Wait up to 5 seconds for the dialog
      if (device.wait(Until.hasObject(By.text("Only this time")), 5000)) {
        device.findObject(By.text("Only this time")).click()
      }
    }
  }

  companion object {
    private lateinit var navigationActions: NavigationActions

    @JvmStatic
    @BeforeClass
    fun setup() {
      val firestore = FirebaseFirestore.getInstance()
      firestore.useEmulator("10.0.2.2", 8080)
      FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
      FirebaseStorage.getInstance().useEmulator("10.0.2.2", 9199)

      firestore.firestoreSettings =
          FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()

      navigationActions = Mockito.mock(NavigationActions::class.java)
    }

    @JvmStatic
    @AfterClass
    fun tearDownClass() {
      // Runs once after all tests in this class have finished
      FirebaseApp.clearInstancesForTest()
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
      val firestore = FirebaseFirestore.getInstance()
      firestore.firestoreSettings =
          FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()

      // Reinitialize FirebaseAuth without the emulator
      FirebaseAuth.getInstance().signOut()
    }
  }

  @Test
  fun AshouldNotBeAbleToReg() = run {
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
  fun becomeAWorker() = run {
    step("Set up the WelcomeScreen and transit to the register") {
      val indicesIncludedServices =
          (0 until item.subcategories[0].setServices.size / 2 step 2).toList()
      val indicesAddOnService = (0 until 6 step 2).toList()
      val indicesTags = (0 until item.subcategories[0].tags.size step 2).toList()
      val listServices = item.subcategories[0].setServices
      val testTagPrecisionIncServ = "Included Services"
      val testTagPrecisionAddOnServ = "Add-On Services"
      val testTagPrecisionTags = "Tags"
      val testLocation =
          com.arygm.quickfix.model.locations.Location(
              latitude = 0.0, longitude = 0.0, name = "Test Location")
      val testBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
      composeTestRule.activityRule.scenario.onActivity { activity ->
        (activity as MainActivity).setTestBitmap(testBitmap)
        (activity as MainActivity).setTestLocation(testLocation)
      }
      // Wait for the UI to settle
      composeTestRule.waitForIdle()

      // Attempt to grant permissions
      allowPermissionsIfNeeded()
      loginToTestAccount()
      // Retry the action until it works with a timeout of 10 seconds
      composeTestRule.waitUntil("find the BottomNavMenu", timeoutMillis = 20000) {
        composeTestRule.onAllNodesWithTag("BNM").fetchSemanticsNodes().isNotEmpty()
      }
      composeTestRule.onRoot().printToLog("TAG")
      onView(withText("Search")) // Match the TextView that has the text "Hello World"
          .perform(click())
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
      onView(withText("Profile")) // Match the TextView that has the text "Hello World"
          .perform(click())
      composeTestRule.onNodeWithTag("SetupyourbusinessaccountOption").performClick()
      composeTestRule
          .onNodeWithTag(C.Tag.personalInfoScreendisplayNameField)
          .performTextInput("ramy")
      composeTestRule
          .onNodeWithTag(C.Tag.personalInfoScreendescriptionField)
          .performTextInput(
              "Dedicated and skilled painter with over 5 years of experience in residential and commercial projects. Specializes in surface preparation, interior and exterior painting, and detailed decorative finishes. Known for delivering high-quality results, adhering to client specifications, and ensuring timely project completion. Passionate about transforming spaces and creating visually appealing environments with precision and creativity.")

      composeTestRule.onNodeWithTag(C.Tag.personalInfoScreencontinueButton).performClick()

      composeTestRule.onNodeWithTag(professionalInfoScreenCategoryField).performClick()

      // Select the first category
      composeTestRule
          .onNodeWithTag(C.Tag.professionalInfoScreenCategoryDropdownMenuItem + 0)
          .performClick()

      composeTestRule.onNodeWithTag(professionalInfoScreenSubcategoryField).performClick()

      // Select the first subcategory
      composeTestRule
          .onNodeWithTag(C.Tag.professionalInfoScreenSubcategoryDropdownMenuItem + 0)
          .performClick()

      composeTestRule.onNodeWithTag(C.Tag.professionalInfoScreenPriceField).performTextInput("100")

      indicesTags.forEach {
        composeTestRule
            .onNodeWithTag(C.Tag.quickFixCheckedListElementLeft + it + testTagPrecisionTags)
            .performClick()
        if (it + 1 < listServices.size)
            composeTestRule
                .onNodeWithTag(C.Tag.quickFixCheckedListElementRight + it + testTagPrecisionTags)
                .performClick()
      }

      composeTestRule
          .onNodeWithTag(C.Tag.quickFixCheckedListOk + testTagPrecisionTags)
          .performClick()

      indicesIncludedServices.forEach {
        composeTestRule
            .onNodeWithTag(C.Tag.quickFixCheckedListElementLeft + it + testTagPrecisionIncServ)
            .performClick()
        if (it + 1 < listServices.size)
            composeTestRule
                .onNodeWithTag(C.Tag.quickFixCheckedListElementRight + it + testTagPrecisionIncServ)
                .performClick()
      }
      composeTestRule
          .onNodeWithTag(C.Tag.quickFixCheckedListOk + testTagPrecisionIncServ)
          .performClick()
      /*
      composeTestRule.waitUntil("find the add on services", timeoutMillis = 20000) {
          composeTestRule.onAllNodesWithTag(C.Tag.quickFixCheckedListOk + testTagPrecisionAddOnServ).fetchSemanticsNodes().isNotEmpty()
      }

        */
      var delayMillis = 2000L
      var targetTime = System.currentTimeMillis() + delayMillis

      composeTestRule.waitUntil(timeoutMillis = delayMillis + 1000) { // Add buffer to timeout
        System.currentTimeMillis() >= targetTime
      }

      indicesAddOnService.forEach {
        composeTestRule
            .onNodeWithTag(C.Tag.quickFixCheckedListElementLeft + it + testTagPrecisionAddOnServ)
            .performClick()
        if (it + 1 < listServices.size)
            composeTestRule
                .onNodeWithTag(
                    C.Tag.quickFixCheckedListElementRight + it + testTagPrecisionAddOnServ)
                .performClick()
      }
      composeTestRule
          .onNodeWithTag(C.Tag.quickFixCheckedListOk + testTagPrecisionAddOnServ)
          .performClick()
      delayMillis = 2000L
      targetTime = System.currentTimeMillis() + delayMillis
      composeTestRule.waitUntil(timeoutMillis = delayMillis + 1000) { // Add buffer to timeout
        System.currentTimeMillis() >= targetTime
      }
      composeTestRule.onNodeWithTag(C.Tag.professionalInfoScreencontinueButton).performClick()
      composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenStayUserButton).performClick()
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
      composeTestRule.waitUntil("find the BottomNavMenu", timeoutMillis = 20000) {
        composeTestRule.onAllNodesWithTag("BottomNavMenu").fetchSemanticsNodes().isNotEmpty()
      }
      composeTestRule.onRoot().printToLog("TAG")
      // Get the bounds of the node
      // Get the bounds of the BottomNavMenu

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

  private fun loginToTestAccount() {
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
}
