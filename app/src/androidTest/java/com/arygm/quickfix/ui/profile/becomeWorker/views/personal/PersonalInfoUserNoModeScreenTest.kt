// Import statements
import android.graphics.Bitmap
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationRepository
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.becomeWorker.views.personal.PersonalInfoScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class PersonalInfoUserNoModeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var locationViewModel: LocationViewModel
  private lateinit var locationRepo: LocationRepository
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    locationViewModel = mock(LocationViewModel::class.java)
    locationRepo = mock(LocationRepository::class.java)

    val testLocationSuggestions =
        MutableStateFlow(listOf(Location(name = "New York"), Location(name = "Los Angeles")))

    // Mock the LocationViewModel
    locationViewModel =
        object : LocationViewModel(locationRepo) {
          override val locationSuggestions: StateFlow<List<Location>> = testLocationSuggestions

          override fun setQuery(query: String) {
            // Simulate filtering by query
            testLocationSuggestions.value =
                if (query.isEmpty()) {
                  emptyList()
                } else {
                  listOf(Location(name = "New York"), Location(name = "Los Angeles")).filter {
                    it.name.contains(query, ignoreCase = true)
                  }
                }
          }
        }
    navigationActions = mock(NavigationActions::class.java)
  }
  // Helper function to set up the screen
  private fun setUpPersonalInfoScreen(
      displayName: MutableState<String>,
      description: MutableState<String>,
      imageBitmapPP: MutableState<Bitmap?>,
      imageBitmapBP: MutableState<Bitmap?>,
      displayNameError: MutableState<Boolean>,
      descriptionError: MutableState<Boolean>,
      initialPage: Int = 0,
      pagerStateHolder: (PagerState) -> Unit = {},
      showBottomSheetPPR: Boolean = false,
      showBottomSheetBPR: Boolean = false,
      locationWorker: MutableState<Location> = mutableStateOf(Location())
  ) {
    composeTestRule.setContent {
      val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = initialPage)
      pagerStateHolder(pagerState)
      PersonalInfoScreen(
          pagerState = pagerState,
          displayName = displayName,
          description = description,
          imageBitmapPP = imageBitmapPP,
          imageBitmapBP = imageBitmapBP,
          displayNameError = displayNameError.value,
          onDisplayNameErrorChange = { displayNameError.value = it },
          descriptionError = descriptionError.value,
          onDescriptionErrorChange = { descriptionError.value = it },
          showBottomSheetPPR = showBottomSheetPPR,
          showBottomSheetBPR = showBottomSheetBPR,
          locationViewModel = locationViewModel,
          locationWorker = locationWorker,
          navigationActions)
    }
  }

  @Test
  fun testAllComponentsAreDisplayed() {
    val displayName = mutableStateOf("")
    val description = mutableStateOf("")
    val imageBitmapPP = mutableStateOf<Bitmap?>(null)
    val imageBitmapBP = mutableStateOf<Bitmap?>(null)
    val displayNameError = mutableStateOf(false)
    val descriptionError = mutableStateOf(false)
    var pagerState: PagerState? = null
    var locationWorker = mutableStateOf(Location())

    setUpPersonalInfoScreen(
        displayName,
        description,
        imageBitmapPP,
        imageBitmapBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it },
        locationWorker = locationWorker)

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
    val imageBitmapPP = mutableStateOf<Bitmap?>(null)
    val imageBitmapBP = mutableStateOf<Bitmap?>(null)
    val displayNameError = mutableStateOf(false)
    val descriptionError = mutableStateOf(false)
    var pagerState: PagerState? = null

    setUpPersonalInfoScreen(
        displayName,
        description,
        imageBitmapPP,
        imageBitmapBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it })

    val testName = "John"

    composeTestRule
        .onNodeWithTag(C.Tag.personalInfoScreendisplayNameField)
        .performTextInput(testName)

    assertEquals(testName, displayName.value)
    assertEquals(false, displayNameError.value)
  }

  @Test
  fun testDisplayNameError() {
    val displayName = mutableStateOf("")
    val description = mutableStateOf("")
    val imageBitmapPP = mutableStateOf<Bitmap?>(null)
    val imageBitmapBP = mutableStateOf<Bitmap?>(null)
    val displayNameError = mutableStateOf(false)
    val descriptionError = mutableStateOf(false)
    var pagerState: PagerState? = null

    setUpPersonalInfoScreen(
        displayName,
        description,
        imageBitmapPP,
        imageBitmapBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it })

    val shortName = "Jo"

    composeTestRule
        .onNodeWithTag(C.Tag.personalInfoScreendisplayNameField)
        .performTextInput(shortName)

    assertEquals(shortName, displayName.value)
    assertEquals(true, displayNameError.value)

    composeTestRule
        .onNodeWithText("That’s too short. Your display name must be at least 3 characters.")
        .assertIsDisplayed()
  }

  @Test
  fun testProfilePictureSelection() {
    val displayName = mutableStateOf("John")
    val description = mutableStateOf("Valid description exceeding 150 characters...")
    val imageBitmapPP = mutableStateOf<Bitmap?>(null)
    val imageBitmapBP = mutableStateOf<Bitmap?>(null)
    val displayNameError = mutableStateOf(false)
    val descriptionError = mutableStateOf(false)
    var pagerState: PagerState? = null

    setUpPersonalInfoScreen(
        displayName,
        description,
        imageBitmapPP,
        imageBitmapBP,
        displayNameError,
        descriptionError,
        initialPage = 0,
        pagerStateHolder = { pagerState = it })

    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreenprofilePictureBackground).performClick()

    composeTestRule.onNodeWithTag("uploadImageSheet").assertIsDisplayed()

    // Simulate a bitmap update for testing purposes
    val testBitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    imageBitmapPP.value = testBitmap

    assertEquals(testBitmap, imageBitmapPP.value)
  }

  @Test
  fun testLocationFieldBehavior() {
    // Mocked location suggestions
    setUpPersonalInfoScreen(
        displayName = mutableStateOf("John"),
        description = mutableStateOf("Valid description exceeding 150 characters..."),
        imageBitmapPP = mutableStateOf(null),
        imageBitmapBP = mutableStateOf(null),
        displayNameError = mutableStateOf(false),
        descriptionError = mutableStateOf(false),
        locationWorker = mutableStateOf(Location()))

    // Verify the initial state (DropdownMenu not displayed)
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreenLoactionDropdownMenu).assertDoesNotExist()

    // Enter text into the QuickFixTextFieldCustom
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreenLocationField).performTextInput("New")

    // Verify that the DropdownMenu is displayed
    composeTestRule.onNodeWithTag(C.Tag.personalInfoScreenLoactionDropdownMenu).assertIsDisplayed()

    // Verify DropdownMenuItems are displayed
    composeTestRule
        .onAllNodesWithTag(C.Tag.professionalInfoScreenLocationDropdownMenuItem + 0)
        .assertCountEquals(1) // Expect one item (filtered list)

    // Select the first item
    composeTestRule
        .onNodeWithTag(C.Tag.professionalInfoScreenLocationDropdownMenuItem + 0)
        .performClick()

    // Verify the text in the field updates
    composeTestRule
        .onNodeWithTag(C.Tag.personalInfoScreenLocationField)
        .assertTextContains("New York")
  }
}
