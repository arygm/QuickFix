package com.arygm.quickfix.ui.dashboard

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.search.Announcement
import com.arygm.quickfix.model.search.AnnouncementRepository
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.AvailabilitySlot
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import com.arygm.quickfix.utils.UID_KEY
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq

class AnnouncementsWidgetTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions
  private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var announcementRepository: AnnouncementRepository
  private lateinit var userProfileRepository: ProfileRepository
  private lateinit var announcementViewModel: AnnouncementViewModel

  private fun createMockBitmap(): Bitmap {
    return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
  }

  private fun createTestAnnouncement(id: String, title: String, description: String): Announcement {
    return Announcement(
        announcementId = id,
        userId = "user_$id",
        title = title,
        category = "TestCategory",
        description = description,
        location = Location(45.0, 9.0, "Test city"),
        availability = listOf(AvailabilitySlot(Timestamp.now(), Timestamp.now())),
        quickFixImages = listOf("image_$id"))
  }

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    preferencesRepository = mock(PreferencesRepository::class.java)
    announcementRepository = mock(AnnouncementRepository::class.java)
    userProfileRepository = mock(ProfileRepository::class.java)

    announcementViewModel =
        AnnouncementViewModel(announcementRepository, preferencesRepository, userProfileRepository)
  }

  @Test
  fun announcementsWidget_displaysDefaultNumberOfItems() = runTest {
    val testUserId = "test_user_id"
    val testProfile =
        UserProfile(
            uid = testUserId,
            announcements = listOf("1", "2", "3", "4"),
            locations = emptyList(),
            wallet = 0.0,
            quickFixes = emptyList())
    val testAnnouncements =
        listOf(
            createTestAnnouncement("1", "Title 1", "Description 1"),
            createTestAnnouncement("2", "Title 2", "Description 2"),
            createTestAnnouncement("3", "Title 3", "Description 3"),
            createTestAnnouncement("4", "Title 4", "Description 4"))

    // Mock repository behavior
    `when`(preferencesRepository.getPreferenceByKey(UID_KEY)).thenReturn(flow { emit(testUserId) })
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (Any?) -> Unit
          onSuccess(testProfile)
          null
        }
        .`when`(userProfileRepository)
        .getProfileById(anyString(), any(), any())
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (List<Announcement>) -> Unit
          onSuccess(testAnnouncements)
        }
        .`when`(announcementRepository)
        .getAnnouncementsForUser(eq(testProfile.announcements), any(), any())

    announcementViewModel.getAnnouncementsForCurrentUser()

    composeTestRule.setContent {
      AnnouncementsWidget(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          itemsToShowDefault = 3)
    }

    // Verify that only the default number of items (3) are displayed initially
    composeTestRule.onNodeWithTag("AnnouncementItem_1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnnouncementItem_2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnnouncementItem_3").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnnouncementItem_4").assertDoesNotExist()
  }

  @Test
  fun announcementsWidget_displaysAllItems_whenShowAllClicked() = runTest {
    val testUserId = "test_user_id"
    val testProfile =
        UserProfile(
            uid = testUserId,
            announcements = listOf("1", "2", "3", "4"),
            locations = emptyList(),
            wallet = 0.0,
            quickFixes = emptyList())
    val testAnnouncements =
        listOf(
            createTestAnnouncement("1", "Title 1", "Description 1"),
            createTestAnnouncement("2", "Title 2", "Description 2"),
            createTestAnnouncement("3", "Title 3", "Description 3"),
            createTestAnnouncement("4", "Title 4", "Description 4"))

    // Mock repository behavior
    `when`(preferencesRepository.getPreferenceByKey(UID_KEY)).thenReturn(flow { emit(testUserId) })
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (Any?) -> Unit
          onSuccess(testProfile)
          null
        }
        .`when`(userProfileRepository)
        .getProfileById(anyString(), any(), any())
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (List<Announcement>) -> Unit
          onSuccess(testAnnouncements)
        }
        .`when`(announcementRepository)
        .getAnnouncementsForUser(eq(testProfile.announcements), any(), any())

    announcementViewModel.getAnnouncementsForCurrentUser()

    composeTestRule.setContent {
      AnnouncementsWidget(
          announcementViewModel = announcementViewModel,
          navigationActions = navigationActions,
          itemsToShowDefault = 3)
    }

    // Click the "Show All" button
    composeTestRule.onNodeWithTag("ShowAllButton").performClick()

    // Verify that all items are displayed
    composeTestRule.onNodeWithTag("AnnouncementItem_1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnnouncementItem_2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnnouncementItem_3").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnnouncementItem_4").assertIsDisplayed()
  }

  @Test
  fun announcementsWidget_handlesItemClick() = runTest {
    val testUserId = "test_user_id"
    val testProfile =
        UserProfile(
            uid = testUserId,
            announcements = listOf("1"),
            locations = emptyList(),
            wallet = 0.0,
            quickFixes = emptyList())
    val testAnnouncements = listOf(createTestAnnouncement("1", "Title 1", "Description 1"))

    // Mock repository behavior
    `when`(preferencesRepository.getPreferenceByKey(UID_KEY)).thenReturn(flow { emit(testUserId) })
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (Any?) -> Unit
          onSuccess(testProfile)
          null
        }
        .`when`(userProfileRepository)
        .getProfileById(anyString(), any(), any())
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (List<Announcement>) -> Unit
          onSuccess(testAnnouncements)
        }
        .`when`(announcementRepository)
        .getAnnouncementsForUser(eq(testProfile.announcements), any(), any())

    announcementViewModel.getAnnouncementsForCurrentUser()

    composeTestRule.setContent {
      AnnouncementsWidget(
          announcementViewModel = announcementViewModel, navigationActions = navigationActions)
    }

    // Click on the first announcement
    composeTestRule.onNodeWithTag("AnnouncementItem_1").performClick()

    // Verify that the correct navigation method was triggered
    verify(navigationActions).navigateTo(UserScreen.ANNOUNCEMENT_DETAIL)
  }

  @Test
  fun announcementsWidget_displaysImageForAnnouncement() = runTest {
    val testUserId = "test_user_id"
    val testProfile =
        UserProfile(
            uid = testUserId,
            announcements = listOf("1"),
            locations = emptyList(),
            wallet = 0.0,
            quickFixes = emptyList())
    val testAnnouncement = createTestAnnouncement("1", "Title 1", "Description 1")
    val mockBitmap = createMockBitmap()

    // Mock repository behavior
    `when`(preferencesRepository.getPreferenceByKey(UID_KEY)).thenReturn(flow { emit(testUserId) })
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (Any?) -> Unit
          onSuccess(testProfile)
          null
        }
        .`when`(userProfileRepository)
        .getProfileById(anyString(), any(), any())
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (List<Announcement>) -> Unit
          onSuccess(listOf(testAnnouncement))
        }
        .`when`(announcementRepository)
        .getAnnouncementsForUser(eq(testProfile.announcements), any(), any())
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (List<Pair<String, Bitmap>>) -> Unit
          onSuccess(listOf("image_1" to mockBitmap))
        }
        .`when`(announcementRepository)
        .fetchAnnouncementsImagesAsBitmaps(eq("1"), any(), any())

    announcementViewModel.getAnnouncementsForCurrentUser()

    composeTestRule.setContent {
      AnnouncementsWidget(
          announcementViewModel = announcementViewModel, navigationActions = navigationActions)
    }

    // Verify that the announcement image is displayed
    composeTestRule.onNodeWithTag("AnnouncementImage_1", useUnmergedTree = true).assertIsDisplayed()
  }
}
