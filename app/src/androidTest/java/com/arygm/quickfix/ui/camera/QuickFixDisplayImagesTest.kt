package com.arygm.quickfix.ui.camera

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.search.Announcement
import com.arygm.quickfix.model.search.AnnouncementRepository
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.utils.UID_KEY
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

class QuickFixDisplayImagesTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var images: List<Bitmap>
  private lateinit var announcementViewModel: AnnouncementViewModel

  private lateinit var mockAnnouncementRepository: AnnouncementRepository
  private lateinit var mockPreferencesRepository: PreferencesRepository
  private lateinit var mockProfileRepository: ProfileRepository

  @Before
  fun setup() {
    navigationActions = Mockito.mock(NavigationActions::class.java)
    images = List(4) { createTestBitmap() }

    // Mock repositories
    mockAnnouncementRepository = Mockito.mock(AnnouncementRepository::class.java)
    mockPreferencesRepository = Mockito.mock(PreferencesRepository::class.java)
    mockProfileRepository = Mockito.mock(ProfileRepository::class.java)

    // Return a valid userId from preferences
    whenever(mockPreferencesRepository.getPreferenceByKey(UID_KEY)).thenReturn(flowOf("testUserId"))

    // Mock profile fetch to return a UserProfile (not strictly needed here but let's keep it
    // consistent)
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (Any?) -> Unit
          onSuccess(UserProfile(emptyList(), emptyList(), 0.0, "testUserId", emptyList()))
          null
        }
        .whenever(mockProfileRepository)
        .getProfileById(any(), any(), any())

    // Initialize a real AnnouncementViewModel instance
    announcementViewModel =
        AnnouncementViewModel(
            mockAnnouncementRepository, mockPreferencesRepository, mockProfileRepository)
  }

  @Test
  fun displaysTitleWithCorrectNumberOfImages_whenImagesProvidedDirectly() {
    // Pass images directly
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true,
          navigationActions = navigationActions,
          announcementViewModel = announcementViewModel,
          images = images)
    }

    // Verify the title displays the correct number of images
    composeTestRule.onNodeWithTag("DisplayedImagesTitle").assertTextEquals("4 elements")
  }

  @Test
  fun goBackButtonNavigatesCorrectly_whenSelectingIsOff() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true,
          navigationActions = navigationActions,
          announcementViewModel = announcementViewModel,
          images = images)
    }

    // Click the "Go Back" button
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    // Verify navigation action is called
    Mockito.verify(navigationActions).goBack()
  }

  @Test
  fun selectImagesButtonTogglesSelectionMode() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true,
          navigationActions = navigationActions,
          announcementViewModel = announcementViewModel,
          images = images)
    }

    // Click the "Select Images" button
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()

    // Verify "Select all" and "Done" buttons are displayed
    composeTestRule.onNodeWithTag("selectionButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("endSelectionButton").assertIsDisplayed()
  }

  @Test
  fun selectAllButtonSelectsAllImages() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true,
          navigationActions = navigationActions,
          announcementViewModel = announcementViewModel,
          images = images)
    }

    // Enter selection mode
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()

    // Click the "Select All" button
    composeTestRule.onNodeWithTag("selectionButton").performClick()

    // Verify all images are selected
    composeTestRule.onNodeWithTag("nbOfSelectedPhotos").assertTextContains("4 photos selected")
  }

  @Test
  fun endSelectionButtonExitsSelectionMode() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true,
          navigationActions = navigationActions,
          announcementViewModel = announcementViewModel,
          images = images)
    }

    // Enter selection mode
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()

    // Exit selection mode
    composeTestRule.onNodeWithTag("endSelectionButton").performClick()

    // Verify selection mode is exited
    composeTestRule.onNodeWithTag("SelectImagesButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("selectionButton").assertDoesNotExist()
  }

  @Test
  fun deleteButtonDeletesSelectedImages_noAnnouncementSelected() {
    // Start with no images passed in but set uploaded images in the viewModel
    images.forEach { announcementViewModel.addUploadedImage(it) }

    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true,
          navigationActions = navigationActions,
          announcementViewModel = announcementViewModel,
          images = emptyList())
    }

    // Initially 4 uploaded images means "4 elements"
    composeTestRule.onNodeWithTag("DisplayedImagesTitle").assertTextEquals("4 elements")

    // Enter selection mode and select all uploaded images
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()
    composeTestRule.onNodeWithTag("selectionButton").performClick()

    // Click the delete button
    composeTestRule.onNodeWithTag("nbOfSelectedPhotos").performClick()

    // After deletion, uploadedImages should be empty, thus "0 elements"
    composeTestRule.onNodeWithTag("DisplayedImagesTitle").assertTextEquals("0 elements")
    composeTestRule.onNodeWithTag("SelectImagesButton").assertIsDisplayed()
  }

  @Test
  fun clickingOnImageSelectsAndDeselectsIt() {
    // Pass images directly
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true,
          navigationActions = navigationActions,
          announcementViewModel = announcementViewModel,
          images = images)
    }

    // Enter selection mode
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()

    // Click on the first image to select it
    composeTestRule.onNodeWithTag("imageCard_0").performClick()
    composeTestRule.onNodeWithTag("nbOfSelectedPhotos").assertTextContains("1 photo selected")

    // Click on the same image to deselect it
    composeTestRule.onNodeWithTag("imageCard_0").performClick()
    composeTestRule.onNodeWithTag("nbOfSelectedPhotos").assertDoesNotExist()
  }

  @Test
  fun displaysCorrectIconForSelectedImages() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true,
          navigationActions = navigationActions,
          announcementViewModel = announcementViewModel,
          images = images)
    }

    // Enter selection mode
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()

    // Select the first image
    composeTestRule.onNodeWithTag("imageCard_0").performClick()

    // Verify the icon changes to "CheckCircle"
    composeTestRule.onNodeWithTag("selectionIcon_0").assertExists()
  }

  @Test
  fun displaysRadioButtonForUnselectedImages() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true,
          navigationActions = navigationActions,
          announcementViewModel = announcementViewModel,
          images = images)
    }

    // Enter selection mode
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()

    // Verify the icon for an unselected image exists
    composeTestRule.onNodeWithTag("selectionIcon_1").assertExists()
  }

  @Test
  fun whenAnnouncementSelected_updatesImagesFromAnnouncementImagesMap() {
    // Setup a selected announcement and images in announcementImagesMap
    val announcement =
        Announcement(
            announcementId = "ann1",
            userId = "user1",
            title = "Test",
            category = "Cat",
            description = "Desc",
            location = null,
            availability = emptyList(),
            quickFixImages = listOf("gs://bucket/image1", "gs://bucket/image2"))

    val bmp1 = createTestBitmap()
    val bmp2 = createTestBitmap()
    // Update the map via viewModel method
    announcementViewModel.setAnnouncementImagesMap(
        mutableMapOf("ann1" to listOf("gs://bucket/image1" to bmp1, "gs://bucket/image2" to bmp2)))
    announcementViewModel.selectAnnouncement(announcement)

    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true,
          navigationActions = navigationActions,
          announcementViewModel = announcementViewModel,
          images = emptyList())
    }

    // Verify the title is "2 elements"
    composeTestRule.onNodeWithTag("DisplayedImagesTitle").assertTextEquals("2 elements")
  }

  @Test
  fun canDeleteFalseHidesSelectionFeatures() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = false,
          navigationActions = navigationActions,
          announcementViewModel = announcementViewModel,
          images = images)
    }

    // Since canDelete is false, "SelectImagesButton" should not exist
    composeTestRule.onNodeWithTag("SelectImagesButton").assertDoesNotExist()
  }

  @Test
  fun noImagesShowsZeroElements() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true,
          navigationActions = navigationActions,
          announcementViewModel = announcementViewModel,
          images = emptyList())
    }

    // With no images and no uploadedImages or announcement selected, "0 elements"
    composeTestRule.onNodeWithTag("DisplayedImagesTitle").assertTextEquals("0 elements")
  }

  private fun createTestBitmap(): Bitmap {
    return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
  }
}
