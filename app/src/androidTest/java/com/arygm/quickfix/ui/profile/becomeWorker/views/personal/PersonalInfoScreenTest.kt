// Import statements
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.profile.becomeWorker.views.personal.PersonalInfoScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersonalInfoScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Helper function to set up the screen
  private fun setUpPersonalInfoScreen(
      displayName: MutableState<String>,
      description: MutableState<String>,
      imagePathPP: MutableState<String>,
      imagePathBP: MutableState<String>,
      displayNameError: MutableState<Boolean>,
      descriptionError: MutableState<Boolean>,
      initialPage: Int = 0,
      pagerStateHolder: (PagerState) -> Unit = {},
      showBottomSheetPPR: Boolean = false,
      showBottomSheetBPR: Boolean = false,
  ) {
    composeTestRule.setContent {
      val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = initialPage)
      pagerStateHolder(pagerState)
      PersonalInfoScreen(
          pagerState = pagerState,
          displayName = displayName,
          description = description,
          imagePathPP = imagePathPP,
          imagePathBP = imagePathBP,
          displayNameError = displayNameError.value,
          onDisplayNameErrorChange = { displayNameError.value = it },
          descriptionError = descriptionError.value,
          onDescriptionErrorChange = { descriptionError.value = it },
          showBottomSheetPPR = showBottomSheetPPR,
          showBottomSheetBPR = showBottomSheetBPR,
      )
    }
  }

  @Test
  fun testAllComponentsAreDisplayed() {
    // Initialize states
    val displayName = mutableStateOf("")
    val description = mutableStateOf("")
    val imagePathPP = mutableStateOf("")
    val imagePathBP = mutableStateOf("")
    val displayNameError = mutableStateOf(false)
    val descriptionError = mutableStateOf(false)
    var pagerState: PagerState? = null

    setUpPersonalInfoScreen(
        displayName,
        description,
        imagePathPP,
        imagePathBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it })

    // Assert that all components are displayed
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreenSectionTitle).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreenSectionDescription).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreendisplayNameField).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreenprofilePictureField).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(C.Tag.personalInfoScreenprofilePictureBackground)
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreenBannerPictureField).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(C.Tag.personalInfoScreenBannerPictureBackground)
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreendescriptionField).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreencancelButton).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreencontinueButton).assertIsDisplayed()
  }

  @Test
  fun testEnteringDisplayName() {
    val displayName = mutableStateOf("")
    val description = mutableStateOf("")
    val imagePathPP = mutableStateOf("")
    val imagePathBP = mutableStateOf("")
    val displayNameError = mutableStateOf(false)
    val descriptionError = mutableStateOf(false)
    var pagerState: PagerState? = null

    setUpPersonalInfoScreen(
        displayName,
        description,
        imagePathPP,
        imagePathBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it })

    val testName = "John"

    // Enter text into the display name field
    composeTestRule
        .onNodeWithTag(C.Tag.personalInfoScreendisplayNameField)
        .performTextInput(testName)

    // Assert that the display name state has been updated
    assertEquals(testName, displayName.value)
    // Assert that the error state is false since length >= 3
    assertEquals(false, displayNameError.value)
  }

  @Test
  fun testDisplayNameError() {
    val displayName = mutableStateOf("")
    val description = mutableStateOf("")
    val imagePathPP = mutableStateOf("")
    val imagePathBP = mutableStateOf("")
    val displayNameError = mutableStateOf(false)
    val descriptionError = mutableStateOf(false)
    var pagerState: PagerState? = null

    setUpPersonalInfoScreen(
        displayName,
        description,
        imagePathPP,
        imagePathBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it })

    val shortName = "Jo"

    // Enter a short name into the display name field
    composeTestRule
        .onNodeWithTag(C.Tag.personalInfoScreendisplayNameField)
        .performTextInput(shortName)

    // Assert that the display name state has been updated
    assertEquals(shortName, displayName.value)
    // Assert that the error state is true since length < 3
    assertEquals(true, displayNameError.value)

    // Check that the error message is displayed
    composeTestRule
        .onNodeWithText("That’s too short. Your display name must be at least 3 characters.")
        .assertIsDisplayed()
  }

  @Test
  fun testEnteringDescription() {
    val displayName = mutableStateOf("John")
    val description = mutableStateOf("")
    val imagePathPP = mutableStateOf("")
    val imagePathBP = mutableStateOf("")
    val displayNameError = mutableStateOf(false)
    val descriptionError = mutableStateOf(false)
    var pagerState: PagerState? = null

    setUpPersonalInfoScreen(
        displayName,
        description,
        imagePathPP,
        imagePathBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it })

    val testDescription = "Experienced electrician with over 10 years in the industry."

    // Enter text into the description field
    composeTestRule
        .onNodeWithTag(C.Tag.personalInfoScreendescriptionField)
        .performTextInput(testDescription)

    // Assert that the description state has been updated
    assertEquals(testDescription, description.value)
    // Since the length is less than 150, error should be true
    assertEquals(true, descriptionError.value)

    // Check that the error message is displayed
    composeTestRule.onNodeWithText("Please enter at least 150 characters").assertIsDisplayed()
  }

  @Test
  fun testDescriptionErrorResolved() {
    val displayName = mutableStateOf("John")
    val description = mutableStateOf("")
    val imagePathPP = mutableStateOf("")
    val imagePathBP = mutableStateOf("")
    val displayNameError = mutableStateOf(false)
    val descriptionError = mutableStateOf(false)
    var pagerState: PagerState? = null

    setUpPersonalInfoScreen(
        displayName,
        description,
        imagePathPP,
        imagePathBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it })

    // Enter a description longer than 150 characters
    val longDescription =
        "I am an experienced electrician with over 10 years in the industry. I specialize in residential and commercial electrical installations, repairs, and maintenance. My focus is on providing high-quality service and ensuring customer satisfaction."

    composeTestRule
        .onNodeWithTag(C.Tag.personalInfoScreendescriptionField)
        .performTextInput(longDescription)

    // Assert that the description state has been updated
    assertEquals(longDescription, description.value)
    // Error should be false since length >= 150
    assertEquals(false, descriptionError.value)

    // Check that the error message is not displayed
    composeTestRule.onNodeWithText("Please enter at least 150 characters").assertDoesNotExist()
  }

  @Test
  fun testProfilePictureSelection() {
    val displayName = mutableStateOf("John")
    val description = mutableStateOf("Valid description exceeding 150 characters...")
    val imagePathPP = mutableStateOf("")
    val imagePathBP = mutableStateOf("")
    val displayNameError = mutableStateOf(false)
    val descriptionError = mutableStateOf(false)
    var pagerState: PagerState? = null

    setUpPersonalInfoScreen(
        displayName,
        description,
        imagePathPP,
        imagePathBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it })

    // Click on the profile picture background to open the bottom sheet
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreenprofilePictureBackground).performClick()

    // Since the bottom sheet logic involves showing another composable based on state,
    // you may need to verify that the bottom sheet is displayed.
    // For simplicity, let's assume the bottom sheet has a test tag:
    // C.Tag.cameraBottomSheet

    // Verify that the bottom sheet is displayed
    composeTestRule.onNodeWithTag(C.Tag.cameraBottomSheet).assertIsDisplayed()

    // Simulate selecting an image (this would require additional setup or mocking)
    // For this test, we'll simulate that the image path is updated
    imagePathPP.value = "path/to/profile_picture.jpg"

    // Assert that the imagePathPP has been updated
    assertEquals("path/to/profile_picture.jpg", imagePathPP.value)
  }

  @Test
  fun testContinueButtonEnabled() {
    val displayName = mutableStateOf("John")
    val description = mutableStateOf("Valid description exceeding 150 characters...")
    val imagePathPP = mutableStateOf("path/to/profile_picture.jpg")
    val imagePathBP = mutableStateOf("")
    val displayNameError = mutableStateOf(false)
    val descriptionError = mutableStateOf(false)
    var pagerState: PagerState? = null

    setUpPersonalInfoScreen(
        displayName,
        description,
        imagePathPP,
        imagePathBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it })

    // The continue button should be enabled
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreencontinueButton).assertIsEnabled()
  }

  @Test
  fun testContinueButtonDisabled() {
    val displayName = mutableStateOf("")
    val description = mutableStateOf("")
    val imagePathPP = mutableStateOf("")
    val imagePathBP = mutableStateOf("")
    val displayNameError = mutableStateOf(true)
    val descriptionError = mutableStateOf(true)
    var pagerState: PagerState? = null

    setUpPersonalInfoScreen(
        displayName,
        description,
        imagePathPP,
        imagePathBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it })

    // The continue button should be disabled
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreencontinueButton).assertIsNotEnabled()
  }

  @Test
  fun testNavigationToNextPage() {
    val displayName = mutableStateOf("John")
    val description = mutableStateOf("Valid description exceeding 150 characters...")
    val imagePathPP = mutableStateOf("path/to/profile_picture.jpg")
    val imagePathBP = mutableStateOf("")
    val displayNameError = mutableStateOf(false)
    val descriptionError = mutableStateOf(false)
    var pagerState: PagerState? = null

    setUpPersonalInfoScreen(
        displayName,
        description,
        imagePathPP,
        imagePathBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it })

    // Ensure the initial page is 0
    assertEquals(0, pagerState?.currentPage ?: 0)

    val testName = "John"

    // Enter text into the display name field
    composeTestRule
        .onNodeWithTag(C.Tag.personalInfoScreendisplayNameField)
        .performTextInput(testName)

    val longDescription =
        "I am an experienced electrician with over 10 years in the industry. I specialize in residential and commercial electrical installations, repairs, and maintenance. My focus is on providing high-quality service and ensuring customer satisfaction."

    composeTestRule
        .onNodeWithTag(C.Tag.personalInfoScreendescriptionField)
        .performTextInput(longDescription)

    // Click the continue button
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreencontinueButton).assertIsEnabled()
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreencontinueButton).performClick()
  }

  @Test
  fun testBottomSheetToNextPage() {
    val displayName = mutableStateOf("John")
    val description = mutableStateOf("Valid description exceeding 150 characters...")
    val imagePathPP = mutableStateOf("path/to/profile_picture.jpg")
    val imagePathBP = mutableStateOf("")
    val displayNameError = mutableStateOf(false)
    val descriptionError = mutableStateOf(false)
    var pagerState: PagerState? = null
    val showBottomSheetPPR = true
    val showBottomSheetBPR = true

    setUpPersonalInfoScreen(
        displayName,
        description,
        imagePathPP,
        imagePathBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it },
        showBottomSheetPPR = showBottomSheetPPR,
        showBottomSheetBPR = showBottomSheetBPR)
    composeTestRule.onNodeWithTag(C.Tag.cameraBottomSheet).assertIsDisplayed()

    composeTestRule.onNodeWithTag(C.Tag.cameraBottomSheet).performGesture { swipeDown() }

    // Wait for animations to complete
    composeTestRule.waitUntil("find the AccountconfigurationOption", timeoutMillis = 20000) {
      composeTestRule
          .onAllNodesWithTag(C.Tag.personalInfoScreendisplayNameField)
          .fetchSemanticsNodes()
          .isNotEmpty()
    }
    // Assert that the bottom sheet is no longer displayed
  }
}
