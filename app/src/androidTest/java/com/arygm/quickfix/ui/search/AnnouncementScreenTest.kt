package com.arygm.quickfix.ui.search

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.search.AnnouncementRepository
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.AnnouncementScreen
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import com.arygm.quickfix.utils.UID_KEY
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class AnnouncementScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var announcementRepository: AnnouncementRepository
  private lateinit var profileRepository: ProfileRepository
  private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var loggedInAccountViewModel: LoggedInAccountViewModel
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var announcementViewModel: AnnouncementViewModel

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    announcementRepository = mock(AnnouncementRepository::class.java)
    profileRepository = mock(ProfileRepository::class.java)
    preferencesRepository = mock(PreferencesRepository::class.java)
    loggedInAccountViewModel = mock(LoggedInAccountViewModel::class.java)
    profileViewModel = mock(ProfileViewModel::class.java)
    accountViewModel = mock(AccountViewModel::class.java)
    preferencesViewModel = PreferencesViewModel(preferencesRepository)

    // Mock preferences to return a userId flow
    val userId = "testUserId"
    whenever(preferencesRepository.getPreferenceByKey(UID_KEY)).thenReturn(flowOf(userId))

    announcementViewModel =
        AnnouncementViewModel(announcementRepository, preferencesRepository, profileRepository)

    // Mock fetchUserProfile to return a UserProfile with wallet = 0.0
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (Any?) -> Unit
          onSuccess(
              UserProfile(
                  locations = listOf(Location(40.0, -74.0, "Hello")),
                  announcements = emptyList(),
                  wallet = 0.0,
                  uid = "testUserId",
                  quickFixes = emptyList()))
          null
        }
        .`when`(profileRepository)
        .getProfileById(anyString(), any(), any())

    // Mock announce success
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as () -> Unit
          onSuccess()
          null
        }
        .`when`(announcementRepository)
        .announce(any(), any(), any())

    // Mock profile update success
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as () -> Unit
          onSuccess()
          null
        }
        .`when`(profileViewModel)
        .updateProfile(any(), any(), any())

    // Mock account fetch
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (Account?) -> Unit
          onSuccess(
              Account(
                  uid = "testUserId",
                  firstName = "John",
                  lastName = "Doe",
                  email = "john@example.com",
                  birthDate = com.google.firebase.Timestamp.now(),
                  isWorker = false,
                  activeChats = emptyList()))
          null
        }
        .`when`(accountViewModel)
        .fetchUserAccount(anyString(), any())
  }

  @Test
  fun announcementScreenDisplaysCorrectly() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("titleInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("categoryInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("descriptionInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("locationInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("availabilityButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("announcementButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("picturesButton").assertIsDisplayed()
  }

  @Test
  fun mandatoryFieldsMessageDisplaysCorrectly() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("mandatoryText").assertTextEquals("* Mandatory fields")
  }

  @Test
  fun postButtonDisabledWhenFieldsAreEmpty() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("announcementButton").assertIsNotEnabled()
  }

  @Test
  fun postButtonEnabledWhenAllMandatoryFieldsAreFilled() {
    val selectedLocation =
        Location(name = "New York City", latitude = 40.7128, longitude = -74.0060)
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    // Fill fields
    composeTestRule.onNodeWithTag("titleInput").performTextInput("Test Title")
    composeTestRule.onNodeWithTag("categoryInput").performTextInput("Test Category")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Test Description")

    composeTestRule.onNodeWithTag("announcementButton").assertIsEnabled()
  }

  @Test
  fun clickingPicturesButtonOpensUploadSheet() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("picturesButton").performClick()
    composeTestRule.onNodeWithTag("uploadImageSheet").assertIsDisplayed()
  }

  @Test
  fun addingImagesDisplaysUploadedImagesBox() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    val bitmap1 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap2 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    composeTestRule.runOnUiThread {
      announcementViewModel.addUploadedImage(bitmap1)
      announcementViewModel.addUploadedImage(bitmap2)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("uploadedImagesBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadedImagesLazyRow").assertIsDisplayed()
  }

  @Test
  fun deletingAnUploadedImageRemovesItFromView() {
    val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    composeTestRule.runOnUiThread { announcementViewModel.addUploadedImage(bitmap) }

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("uploadedImageCard0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("deleteImageButton0").performClick()

    composeTestRule.waitForIdle()
    assertTrue(announcementViewModel.uploadedImages.value.isEmpty())
    composeTestRule.onNodeWithTag("picturesButton").assertIsDisplayed()
  }

  @Test
  fun postButtonResetsParametersAfterPosting() {
    val selectedLocation = Location(name = "London, UK", latitude = 51.5074, longitude = -0.1278)
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)

    `when`(announcementRepository.getNewUid()).thenReturn("newAnnouncementId")

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    // Fill fields
    composeTestRule.onNodeWithTag("titleInput").performTextInput("Title")
    composeTestRule.onNodeWithTag("categoryInput").performTextInput("Category")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Description")

    // Post
    composeTestRule.onNodeWithTag("announcementButton").assertIsEnabled().performClick()

    composeTestRule.waitForIdle()

    verify(navigationActions).saveToCurBackStack("selectedLocation", null)
    composeTestRule.onNodeWithTag("locationInput").assertTextContains("Location")
    composeTestRule.onNodeWithTag("titleInput").assertTextEquals("")
    composeTestRule.onNodeWithTag("categoryInput").assertTextEquals("")
    composeTestRule.onNodeWithTag("descriptionInput").assertTextEquals("")
  }

  @Test
  fun postingAnnouncementWithoutImagesCallsAnnounceDirectly() {
    val selectedLocation = Location(name = "Paris, France", latitude = 48.8566, longitude = 2.3522)
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)

    `when`(announcementRepository.getNewUid()).thenReturn("noImageId")

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("titleInput").performTextInput("Title")
    composeTestRule.onNodeWithTag("categoryInput").performTextInput("Category")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Description")

    composeTestRule.onNodeWithTag("announcementButton").assertIsEnabled().performClick()

    verify(announcementRepository, times(1)).announce(any(), any(), any())
  }

  @Test
  fun postingAnnouncementWithImagesUploadsAndThenAnnounces() {
    val selectedLocation =
        Location(name = "Berlin, Germany", latitude = 52.5200, longitude = 13.4050)
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)

    val bitmap1 = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888)
    val bitmap2 = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888)
    composeTestRule.runOnUiThread {
      announcementViewModel.addUploadedImage(bitmap1)
      announcementViewModel.addUploadedImage(bitmap2)
    }

    `when`(announcementRepository.getNewUid()).thenReturn("withImageId")

    doAnswer {
          val onSuccess = it.arguments[2] as (List<String>) -> Unit
          onSuccess(listOf("imageUrl1", "imageUrl2"))
          null
        }
        .`when`(announcementRepository)
        .uploadAnnouncementImages(anyString(), any(), any(), any())

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("titleInput").performTextInput("Title")
    composeTestRule.onNodeWithTag("categoryInput").performTextInput("Category")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Description")

    composeTestRule.onNodeWithTag("announcementButton").assertIsEnabled().performClick()

    verify(announcementRepository, times(1))
        .uploadAnnouncementImages(eq("withImageId"), eq(listOf(bitmap1, bitmap2)), any(), any())
    verify(announcementRepository, times(1)).announce(any(), any(), any())
  }

  @Test
  fun postingAnnouncementWithImageUploadFailureDoesNotAnnounce() {
    val selectedLocation = Location(name = "Tokyo, Japan", latitude = 35.6762, longitude = 139.6503)
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)

    val bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888)
    composeTestRule.runOnUiThread { announcementViewModel.addUploadedImage(bitmap) }

    `when`(announcementRepository.getNewUid()).thenReturn("failImageId")

    doAnswer {
          val onFailure = it.arguments[3] as (Exception) -> Unit
          onFailure(Exception("Upload failed"))
          null
        }
        .`when`(announcementRepository)
        .uploadAnnouncementImages(anyString(), any(), any(), any())

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("titleInput").performTextInput("Title")
    composeTestRule.onNodeWithTag("categoryInput").performTextInput("Category")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Description")

    composeTestRule.onNodeWithTag("announcementButton").assertIsEnabled().performClick()

    verify(announcementRepository, times(0)).announce(any(), any(), any())
  }

  @Test
  fun locationInputNavigatesToSearchLocationScreen() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("locationInput").performClick()
    verify(navigationActions, times(1)).navigateTo(UserScreen.SEARCH_LOCATION)
  }

  @Test
  fun locationIsDisplayedIfSelectedLocationInBackStack() {
    val selectedLocation =
        Location(name = "Los Angeles, USA", latitude = 34.0522, longitude = -118.2437)
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("locationInput").assertTextContains("Los Angeles, USA")
  }

  @Test
  fun clearingAfterPostClearsLocationInBackStack() {
    val selectedLocation = Location(name = "Madrid, Spain", latitude = 40.4168, longitude = -3.7038)
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)
    `when`(announcementRepository.getNewUid()).thenReturn("clearLocationId")

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("titleInput").performTextInput("Title")
    composeTestRule.onNodeWithTag("categoryInput").performTextInput("Category")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Description")

    composeTestRule.onNodeWithTag("announcementButton").assertIsEnabled().performClick()

    verify(navigationActions, times(1)).saveToCurBackStack("selectedLocation", null)
    composeTestRule.onNodeWithTag("locationInput").assertTextContains("Location")
  }

  @Test
  fun dismissingUploadImageSheetWorks() {
    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("picturesButton").performClick()
    composeTestRule.onNodeWithTag("uploadImageSheet").assertIsDisplayed()

    // Assume there's a close button with testTag "uploadImageSheetCloseButton"
    // If not, add such a button in the actual UI code
    composeTestRule
        .onNodeWithTag("uploadImageSheetCloseButton", useUnmergedTree = true)
        .performClick()

    composeTestRule.onNodeWithTag("uploadImageSheet").assertDoesNotExist()
  }

  @Test
  fun locationPlaceholderWhenNoSelectedLocation() {
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(null)

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("locationInput").assertTextEquals("Location")
  }

  @Test
  fun ensureNoCrashIfProfileNotUserProfile() {
    // If getProfileById returns something not a UserProfile
    doAnswer {
          val onSuccess = it.arguments[1] as (Any?) -> Unit
          onSuccess(null)
          null
        }
        .`when`(profileRepository)
        .getProfileById(anyString(), any(), any())

    val selectedLocation = Location(0.0, 0.0, "Somewhere")
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)
    `when`(announcementRepository.getNewUid()).thenReturn("noUserProfileId")

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel,
          profileViewModel = profileViewModel,
          accountViewModel = accountViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions)
    }

    composeTestRule.onNodeWithTag("titleInput").performTextInput("Title")
    composeTestRule.onNodeWithTag("categoryInput").performTextInput("Category")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Description")

    composeTestRule.onNodeWithTag("announcementButton").assertIsEnabled().performClick()

    // No crash should happen
    assertTrue(true)
  }
}
