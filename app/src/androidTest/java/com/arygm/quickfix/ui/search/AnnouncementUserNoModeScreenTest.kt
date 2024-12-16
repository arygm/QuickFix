package com.arygm.quickfix.ui.search

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.datastore.preferences.core.Preferences
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationRepository
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.search.AnnouncementRepository
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.AnnouncementScreen
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AnnouncementUserNoModeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var announcementRepository: AnnouncementRepository
  private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var userProfileRepository: ProfileRepository
  private lateinit var announcementViewModel: AnnouncementViewModel
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var locationRepository: LocationRepository
  private lateinit var locationViewModel: LocationViewModel

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    preferencesRepository = mock(PreferencesRepository::class.java)
    announcementRepository = mock(AnnouncementRepository::class.java)
    userProfileRepository = mock(ProfileRepository::class.java)
    val mockedPreferenceFlow = MutableStateFlow<Any?>(null)

    whenever(
            preferencesRepository.getPreferenceByKey(
                org.mockito.kotlin.any<Preferences.Key<Any>>()))
        .thenReturn(mockedPreferenceFlow)
    preferencesViewModel = PreferencesViewModel(preferencesRepository)
    announcementViewModel =
        AnnouncementViewModel(announcementRepository, preferencesRepository, userProfileRepository)

    locationRepository = mock(LocationRepository::class.java)
    locationViewModel = LocationViewModel(locationRepository)
  }

  @Test
  fun announcementScreenDisplaysCorrectly() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel)
    }

    // Check that each labeled component in the screen is displayed
    composeTestRule.onNodeWithTag("titleInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("categoryInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("descriptionInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("locationInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("picturesButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("announcementButton").assertIsDisplayed()
  }

  @Test
  fun textFieldDisplaysCorrectPlaceholdersAndLabels() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel)
    }

    // Adaptation : subcategory au lieu de category
    composeTestRule.onNodeWithTag("titleText").assertTextEquals("Title *")
    composeTestRule.onNodeWithTag("categoryText").assertTextEquals("Subcategory *")
    composeTestRule.onNodeWithTag("descriptionText").assertTextEquals("Description *")
    composeTestRule.onNodeWithTag("locationText").assertTextEquals("Location *")
  }

  @Test
  fun titleInputAcceptsText() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel)
    }

    val titleText = "My QuickFix Title"
    composeTestRule.onNodeWithTag("titleInput").performTextInput(titleText)
    composeTestRule.onNodeWithTag("titleInput").assertTextEquals(titleText)
  }

  @Test
  fun categoryInputAcceptsText() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel)
    }

    val categoryText = "ResidentialPainting"
    composeTestRule.onNodeWithTag("categoryInput").performTextInput(categoryText)
    composeTestRule.onNodeWithTag("categoryInput").assertTextEquals(categoryText)
  }

  @Test
  fun descriptionInputAcceptsText() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel)
    }

    val descriptionText = "Detailed description"
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput(descriptionText)
    composeTestRule.onNodeWithTag("descriptionInput").assertTextEquals(descriptionText)
  }

  @Test
  fun picturesButtonClickable() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel)
    }

    composeTestRule.onNodeWithTag("picturesButton").performClick().assertIsDisplayed()
  }

  @Test
  fun mandatoryFieldsMessageDisplaysCorrectly() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel)
    }

    composeTestRule.onNodeWithTag("mandatoryText").assertTextEquals("* Mandatory fields")
  }

  @Test
  fun uploadImageButtonOpensImageSheet() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel)
    }

    composeTestRule.onNodeWithTag("picturesButton").performClick()

    // Assumant que QuickFixUploadImageSheet a un tag "uploadImageSheet"
    // Si besoin, ajouter .testTag("uploadImageSheet") dans QuickFixUploadImageSheet
    composeTestRule.onNodeWithTag("uploadImageSheet", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun imagesAreDisplayedWhenUploadedImagesIsNotEmpty() {
    val bitmap1 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap2 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap3 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap4 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel)
    }

    composeTestRule.runOnUiThread {
      announcementViewModel.addUploadedImage(bitmap1)
      announcementViewModel.addUploadedImage(bitmap2)
      announcementViewModel.addUploadedImage(bitmap3)
      announcementViewModel.addUploadedImage(bitmap4)
    }

    composeTestRule.waitForIdle()

    assertEquals(4, announcementViewModel.uploadedImages.value.size)

    composeTestRule.onNodeWithTag("uploadedImagesBox").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("uploadedImagesLazyRow", useUnmergedTree = true)
        .assertIsDisplayed()

    composeTestRule.onNodeWithTag("uploadedImageCard0", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadedImageCard1", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadedImageCard2", useUnmergedTree = true).assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("remainingImagesOverlay", useUnmergedTree = true)
        .assertIsDisplayed()
  }

  @Test
  fun deleteImageButtonRemovesImage() {
    val bitmap1 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap2 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap3 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel)
    }

    composeTestRule.runOnUiThread {
      announcementViewModel.addUploadedImage(bitmap1)
      announcementViewModel.addUploadedImage(bitmap2)
      announcementViewModel.addUploadedImage(bitmap3)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("uploadedImageCard0", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadedImageCard1", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadedImageCard2", useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithTag("deleteImageButton0", useUnmergedTree = true).performClick()

    composeTestRule.waitForIdle()

    assertEquals(2, announcementViewModel.uploadedImages.value.size)
  }

  @Test
  fun clickingRemainingImagesOverlayNavigatesToDisplayUploadedImages() {
    val bitmap1 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap2 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap3 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap4 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap5 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel)
    }

    composeTestRule.runOnUiThread {
      announcementViewModel.addUploadedImage(bitmap1)
      announcementViewModel.addUploadedImage(bitmap2)
      announcementViewModel.addUploadedImage(bitmap3)
      announcementViewModel.addUploadedImage(bitmap4)
      announcementViewModel.addUploadedImage(bitmap5)
    }

    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag("remainingImagesOverlay", useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("remainingImagesOverlay", useUnmergedTree = true).performClick()

    verify(navigationActions, times(1)).navigateTo(UserScreen.DISPLAY_UPLOADED_IMAGES)
  }

  @Test
  fun prefilledAvailabilityIsDisplayedCorrectly() {
    val initialAvailability =
        listOf(
            Pair(
                LocalDateTime.of(2023, 6, 15, 10, 0)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli(),
                LocalDateTime.of(2023, 6, 15, 12, 0)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()))

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel,
          initialAvailability = initialAvailability)
    }

    // Vérifier que la disponibilité pré-remplie est affichée
    composeTestRule.onNodeWithTag("availabilitySlot").assertExists()
  }

  @Test
  fun prefilledLocationIsDisplayedCorrectly() {
    val initialLocation = Location(name = "Paris, France", latitude = 48.8566, longitude = 2.3522)

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel,
          initialLocation = initialLocation)
    }

    // Vérifier que l'emplacement pré-rempli est affiché
    composeTestRule.onNodeWithTag("locationInput").assertTextEquals("Paris, France")
  }

  @Test
  fun prefilledUploadedImagesAreDisplayedCorrectly() {
    val bitmap1 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap2 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel,
          initialUploadedImages = listOf(bitmap1, bitmap2))
    }

    // Vérifier que les images téléchargées pré-remplies sont affichées
    composeTestRule.onNodeWithTag("uploadedImageCard0", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadedImageCard1", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun availabilityPopupFlowAddsAvailabilitySlot() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          preferencesViewModel = preferencesViewModel,
          initialAvailability = emptyList())
    }

    // Simulate clicking the "Add Availability" button to open the start availability picker
    composeTestRule.onNodeWithText("Add Availability").performClick()
    composeTestRule.waitForIdle()
    val date = LocalDate.now()
    composeTestRule
        .onNode(hasText(date.dayOfMonth.toString()) and hasClickAction())
        .assertIsDisplayed()
  }
}
