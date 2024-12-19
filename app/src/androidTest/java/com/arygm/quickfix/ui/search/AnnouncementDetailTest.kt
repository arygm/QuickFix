package com.arygm.quickfix.ui.search

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.search.Announcement
import com.arygm.quickfix.model.search.AnnouncementRepository
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.AvailabilitySlot
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.AnnouncementDetailScreen
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class AnnouncementDetailTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mocked dependencies
  private lateinit var navigationActions: NavigationActions
  private lateinit var mockAnnouncementRepository: AnnouncementRepository
  private lateinit var mockPreferencesRepository: PreferencesRepository
  private lateinit var mockProfileRepository: ProfileRepository

  // Real AnnouncementViewModel with mocked repositories
  private lateinit var announcementViewModel: AnnouncementViewModel
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var categoryViewModel: CategoryViewModel

  // DataStore preference flows
  private val userIdFlow = MutableStateFlow("testUserId")
  private val appModeFlow = MutableStateFlow("USER") // "USER" or "WORKER"

  // Sample announcement for testing
  private val sampleAnnouncement =
      Announcement(
          announcementId = "testAnnouncementId",
          userId = "testUserId",
          title = "Test Announcement",
          category = "subCatId",
          description = "This is a test description",
          location = null,
          availability = emptyList(),
          quickFixImages = emptyList())

  // Maps announcementId -> List of (URL, Bitmap)
  private val imagesMap = mutableMapOf<String, List<Pair<String, Bitmap>>>()

  @Before
  fun setup() {
    navigationActions = Mockito.mock(NavigationActions::class.java)

    // Mock repositories
    mockAnnouncementRepository = Mockito.mock(AnnouncementRepository::class.java)
    mockPreferencesRepository = Mockito.mock(PreferencesRepository::class.java)
    mockProfileRepository = Mockito.mock(ProfileRepository::class.java)

    categoryViewModel = mockk(relaxed = true)

    // PreferencesViewModel
    preferencesViewModel = PreferencesViewModel(mockPreferencesRepository)

    // Mock DataStore calls
    val userIdKey = stringPreferencesKey("user_id")
    val appModeKey = stringPreferencesKey("app_mode")
    whenever(mockPreferencesRepository.getPreferenceByKey(userIdKey)).thenReturn(userIdFlow)
    whenever(mockPreferencesRepository.getPreferenceByKey(appModeKey)).thenReturn(appModeFlow)

    // Mock Profile fetch
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (Any?) -> Unit
          onSuccess(UserProfile(emptyList(), emptyList(), 0.0, "testUserId", emptyList()))
          null
        }
        .whenever(mockProfileRepository)
        .getProfileById(any(), any(), any())

    // Initialize AnnouncementViewModel with real logic but mocked repos
    announcementViewModel =
        AnnouncementViewModel(
            announcementRepository = mockAnnouncementRepository,
            preferencesRepository = mockPreferencesRepository,
            profileRepository = mockProfileRepository)

    // Select the sample announcement
    announcementViewModel.selectAnnouncement(sampleAnnouncement)
    // Pre-populate images map with an empty list (no images initially)
    imagesMap[sampleAnnouncement.announcementId] = emptyList()
    announcementViewModel.setAnnouncementImagesMap(imagesMap.toMutableMap())

    // Mock CategoryViewModel success call, matching your Category data class
    every { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } answers
        {
          secondArg<(Category?) -> Unit>()
              .invoke(
                  Category(
                      id = "catId",
                      name = "Fake Category",
                      description = "A test category description",
                      subcategories = emptyList()))
        }
  }

  @Test
  fun announcementDetailScreen_displaysProperly_whenAnnouncementSelected() {
    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel,
          categoryViewModel = categoryViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    // Verify root container is displayed
    composeTestRule.onNodeWithTag("AnnouncementDetailScreenRoot").assertExists()
    // Verify LazyColumn is displayed
    composeTestRule.onNodeWithTag("AnnouncementDetailLazyColumn").assertExists()
    // Verify content column
    composeTestRule.onNodeWithTag("AnnouncementContentColumn").assertExists()

    // Check the announcement title
    composeTestRule.onNodeWithTag("AnnouncementTitle").assertTextEquals("Test Announcement")
  }

  @Test
  fun goBackButtonNavigatesCorrectly() {
    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel,
          categoryViewModel = categoryViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }
    composeTestRule.onNodeWithTag("GoBackIconBtn").performClick()
    Mockito.verify(navigationActions).goBack()
  }

  @Test
  fun editDeleteIconBtn_togglesIsEditing_andDeletesAnnouncement() {
    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel,
          categoryViewModel = categoryViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }
    // First click on "EditDeleteIconBtn" turns editing ON
    composeTestRule.onNodeWithTag("EditDeleteIconBtn").performClick()

    // Now in editing mode. Click again => should delete the announcement
    composeTestRule.onNodeWithTag("EditDeleteIconBtn").performClick()

    // Verify that the repository's deleteAnnouncementById was called
    Mockito.verify(mockAnnouncementRepository)
        .deleteAnnouncementById(eq("testAnnouncementId"), any(), any())
    // Also verify the viewModel unselects it
    assert(announcementViewModel.selectedAnnouncement.value == null)
  }

  @Test
  fun bannerTextIsDisplayed_whenImagesArePresent() {
    // Add images to the imagesMap
    val dummyBitmap = createMockBitmap()
    val updatedImages = listOf("imgUrl1" to dummyBitmap, "imgUrl2" to dummyBitmap)
    imagesMap[sampleAnnouncement.announcementId] = updatedImages
    announcementViewModel.setAnnouncementImagesMap(imagesMap.toMutableMap())

    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel,
          categoryViewModel = categoryViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    // The banner should read "1 of 2"
    composeTestRule.onNodeWithTag("BannerText", useUnmergedTree = true).assertTextEquals("1 of 2")
  }

  @Test
  fun locationFallbackDisplayed_whenLocationIsNull() {
    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel,
          categoryViewModel = categoryViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }
    composeTestRule.onNodeWithTag("LocationValue").assertTextEquals("No location available")
  }

  @Test
  fun availabilitySlotsDisplayed_whenNotEmpty() {
    // Provide some availability
    val updatedAnnouncement =
        sampleAnnouncement.copy(
            availability =
                listOf(
                    AvailabilitySlot(
                        start = com.google.firebase.Timestamp(Date(1680000000000)),
                        end = com.google.firebase.Timestamp(Date(1680003600000)))))
    announcementViewModel.selectAnnouncement(updatedAnnouncement)

    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel,
          categoryViewModel = categoryViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    // The header row should appear
    composeTestRule.onNodeWithTag("AvailabilityHeader").assertExists()
    // The slot row should appear
    composeTestRule.onNodeWithTag("AvailabilitySlot_0").assertExists()
  }

  @Test
  fun emptyAvailabilityFallback_whenNoSlots() {
    // sampleAnnouncement has an empty availability list by default
    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel,
          categoryViewModel = categoryViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }
    // Fallback
    composeTestRule.onNodeWithTag("EmptyAvailabilityBox").assertIsDisplayed()
  }

  @Test
  fun enableEditing_showsUpdateAnnouncementBtn_clickingItCallsUpdate_andNavigatesBack() {
    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel,
          categoryViewModel = categoryViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    // Turn editing on
    composeTestRule.onNodeWithTag("EditDeleteIconBtn").performClick()

    // Now we need to simulate the user *changing the description* so that descriptionChanged = true
    composeTestRule
        .onNodeWithTag("DescriptionTextField", useUnmergedTree = true)
        .performTextClearance() // Clear existing text
    composeTestRule
        .onNodeWithTag("DescriptionTextField", useUnmergedTree = true)
        .performTextInput("New description") // Type something new

    // "UpdateAnnouncementBtn" is now visible
    composeTestRule.onNodeWithTag("UpdateAnnouncementBtn").assertExists()
    // Click it
    composeTestRule.onNodeWithTag("UpdateAnnouncementBtn").performClick()

    // Now, updateAnnouncement(...) should have been called
    Mockito.verify(mockAnnouncementRepository).updateAnnouncement(any(), any(), any())
    Mockito.verify(navigationActions).goBack()
  }

  @Test
  fun proposeQuickFixBtn_isShown_whenAppModeIsWorker() {
    // Switch app mode to "WORKER"
    appModeFlow.value = "WORKER"

    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel,
          categoryViewModel = categoryViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    // "EditDeleteIconBtn" should not exist
    composeTestRule.onNodeWithTag("EditDeleteIconBtn").assertDoesNotExist()
    // "ProposeQuickFixBtn" should be visible
    composeTestRule.onNodeWithTag("ProposeQuickFixBtn").assertIsDisplayed()
  }

  @Test
  fun addAvailability_displaysDatePicker() {

    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    // Act - Turn on editing mode
    composeTestRule.onNodeWithTag("EditDeleteIconBtn").performClick()
    composeTestRule.onNodeWithTag("AddNewAvailabilityBtn").assertExists().performClick()

    // Assert - Start Availability Picker is displayed
    val date = LocalDate.now()
    composeTestRule
        .onNode(hasText(date.dayOfMonth.toString()) and hasClickAction())
        .assertIsDisplayed()
  }

  private fun createMockBitmap(): Bitmap {
    return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
  }
}
