package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.search.Announcement
import com.arygm.quickfix.model.search.AnnouncementRepository
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.announcements.AnnouncementsScreen
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerScreen
import com.arygm.quickfix.utils.UID_KEY
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class AnnouncementsScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var announcementViewModel: AnnouncementViewModel
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var workerProfileViewModel: ProfileViewModel
  private lateinit var categoryViewModel: CategoryViewModel
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var mockNavigationActions: NavigationActions

  private lateinit var mockAnnouncementRepository: AnnouncementRepository
  private lateinit var mockPreferencesRepository: PreferencesRepository
  private lateinit var mockProfileRepository: ProfileRepository

  private val userIdFlow = MutableStateFlow("workerUserId")

  private val sampleAnnouncements =
      listOf(
          Announcement(
              announcementId = "ann1",
              userId = "user1",
              title = "Announcement 1",
              category = "cat1",
              description = "Desc1",
              location = Location(10.0, 10.0, "Loc1"),
              availability = emptyList(),
              quickFixImages = emptyList()),
          Announcement(
              announcementId = "ann2",
              userId = "user2",
              title = "Announcement 2",
              category = "cat2",
              description = "Desc2",
              location = Location(20.0, 20.0, "Loc2"),
              availability = emptyList(),
              quickFixImages = emptyList()))

  @Before
  fun setup() {
    mockAnnouncementRepository = mock(AnnouncementRepository::class.java)
    mockPreferencesRepository = mock(PreferencesRepository::class.java)
    mockProfileRepository = mock(ProfileRepository::class.java)
    categoryViewModel = mockk(relaxed = true)
    accountViewModel = mockk(relaxed = true)
    workerProfileViewModel = ProfileViewModel(mockProfileRepository)

    mockNavigationActions = mock(NavigationActions::class.java)

    preferencesViewModel = PreferencesViewModel(mockPreferencesRepository)

    val userIdKey = UID_KEY
    whenever(mockPreferencesRepository.getPreferenceByKey(userIdKey)).thenReturn(userIdFlow)

    // Mock announcements flow
    // By default, no announcements are set until we decide to call getAnnouncements or set them
    // We'll directly manipulate the announcements in AnnouncementViewModel by mocking the
    // repository calls.
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[0] as (List<Announcement>) -> Unit
          onSuccess(sampleAnnouncements)
          null
        }
        .whenever(mockAnnouncementRepository)
        .getAnnouncements(any(), any())

    // Mock init call
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[0] as () -> Unit
          onSuccess()
          null
        }
        .whenever(mockAnnouncementRepository)
        .init(any())

    // Initialize view model
    announcementViewModel =
        AnnouncementViewModel(
            announcementRepository = mockAnnouncementRepository,
            preferencesRepository = mockPreferencesRepository,
            profileRepository = mockProfileRepository)

    // Set announcements
    announcementViewModel.getAnnouncements()

    // Mock fetching worker profile after userId is loaded
    doAnswer { invocation ->
          val uid = invocation.arguments[0] as String
          val onSuccess = invocation.arguments[1] as (Any?) -> Unit
          // Return a WorkerProfile
          onSuccess(WorkerProfile(location = Location(30.0, 30.0, "WorkerLoc"), uid = uid))
          null
        }
        .whenever(mockProfileRepository)
        .getProfileById(anyString(), any(), any())
  }

  @Test
  fun announcementsScreen_displaysProperly() {
    composeTestRule.setContent {
      AnnouncementsScreen(
          announcementViewModel = announcementViewModel,
          preferencesViewModel = preferencesViewModel,
          workerProfileViewModel = workerProfileViewModel,
          categoryViewModel = categoryViewModel,
          accountViewModel = accountViewModel,
          navigationActions = mockNavigationActions)
    }

    // Verify base UI elements
    composeTestRule.onNodeWithTag("announcements_screen").assertExists()
    composeTestRule.onNodeWithTag("announcements_scaffold").assertExists()
    composeTestRule.onNodeWithTag("main_column").assertExists()
    composeTestRule.onNodeWithTag("title_column").assertExists()
    composeTestRule.onNodeWithTag("announcements_title").assertTextEquals("Announcements for you")
    composeTestRule
        .onNodeWithTag("announcements_subtitle")
        .assertTextEquals("Here are announcements that matches your profile")
  }

  @Test
  fun tuneButton_togglesFilterVisibility() {
    composeTestRule.setContent {
      AnnouncementsScreen(
          announcementViewModel = announcementViewModel,
          preferencesViewModel = preferencesViewModel,
          workerProfileViewModel = workerProfileViewModel,
          categoryViewModel = categoryViewModel,
          accountViewModel = accountViewModel,
          navigationActions = mockNavigationActions)
    }

    // Initially, the filter buttons row is there, but the LazyRow with filters is hidden
    composeTestRule.onNodeWithTag("tuneButton").assertExists()
    // Since the visibility is false initially, "filter_button_Clear" should not be found
    composeTestRule.onNodeWithTag("filter_button_Clear").assertDoesNotExist()

    // Click tune button -> show filters
    composeTestRule.onNodeWithTag("tuneButton").performClick()
    // Now the filters should be visible
    composeTestRule.onNodeWithTag("filter_button_Clear").assertExists()

    // Click tune button again -> hide filters
    composeTestRule.onNodeWithTag("tuneButton").performClick()
    // "filter_button_Clear" should no longer exist
    composeTestRule.onNodeWithTag("filter_button_Clear").assertDoesNotExist()
  }

  @Test
  fun announcements_areDisplayed_andClickable() {
    composeTestRule.setContent {
      AnnouncementsScreen(
          announcementViewModel = announcementViewModel,
          preferencesViewModel = preferencesViewModel,
          workerProfileViewModel = workerProfileViewModel,
          categoryViewModel = categoryViewModel,
          accountViewModel = accountViewModel,
          navigationActions = mockNavigationActions)
    }

    // Check that announcements are displayed
    composeTestRule.onNodeWithTag("worker_profiles_list").assertExists()
    composeTestRule.onNodeWithTag("announcement_0").assertExists()
    composeTestRule.onNodeWithTag("announcement_1").assertExists()

    // Click on an announcement
    composeTestRule.onNodeWithTag("announcement_0").performClick()
    verify(mockNavigationActions).navigateTo(eq(WorkerScreen.ANNOUNCEMENT_DETAIL))
  }

  @Test
  fun clearFilterButton_resetsFilters() {
    composeTestRule.setContent {
      AnnouncementsScreen(
          announcementViewModel = announcementViewModel,
          preferencesViewModel = preferencesViewModel,
          workerProfileViewModel = workerProfileViewModel,
          categoryViewModel = categoryViewModel,
          accountViewModel = accountViewModel,
          navigationActions = mockNavigationActions)
    }

    // Show filters
    composeTestRule.onNodeWithTag("tuneButton").performClick()

    // Click clear button
    composeTestRule.onNodeWithTag("filter_button_Clear").performClick()

    // Filters should be reset. Since we had no actual modifications, just ensure no crash
    // and announcements remain displayed
    composeTestRule.onNodeWithTag("announcement_0").assertExists()
    composeTestRule.onNodeWithTag("announcement_1").assertExists()
  }

  @Test
  fun applyLocationFilter_withDefaultLocation_showsToast_andFilters() {
    // Initially locationFilterApplied = false
    // Show filters
    composeTestRule.setContent {
      AnnouncementsScreen(
          announcementViewModel = announcementViewModel,
          preferencesViewModel = preferencesViewModel,
          workerProfileViewModel = workerProfileViewModel,
          categoryViewModel = categoryViewModel,
          accountViewModel = accountViewModel,
          navigationActions = mockNavigationActions)
    }

    // Show filter buttons
    composeTestRule.onNodeWithTag("tuneButton").performClick()

    // Click location button
    composeTestRule.onNodeWithTag("filter_button_Location").performClick()

    // The bottom sheet should appear. It's controlled by showLocationBottomSheet
    // Since we have a WorkerProfile (mocked), the bottom sheet will show location options.
    // We must select the "Use my Current Location" (option 0)
    // Perform action: We can't directly interact with QuickFixLocationFilterBottomSheet's internal
    // elements
    // since not all test tags might be available. We assume "applyButton" testTag from
    // QuickFixLocationFilterBottomSheet.
    // We'll just simulate the scenario by calling onApplyClick indirectly:

    // To ensure line coverage, we need to reflect a scenario:
    // We'll mock a scenario that location chosen is default (0.0,0.0,"Default") and see if Toast
    // shows up
    // Although we can't directly test Toast easily, we can still ensure no crash occurs.

    // Let's simulate tapping apply from the bottom sheet:
    // For that, we must ensure we have a testTag for "applyButton" inside the bottom sheet (already
    // added in previous code)
    composeTestRule.onNodeWithTag("applyButton").assertIsDisplayed().performClick()

    // Now locationFilterApplied = true
    // We triggered a toast scenario but we can't verify toast in Jetpack Compose tests easily,
    // just trusting the code runs. Announcements should now be filtered (though we have no real
    // distance checks)
    composeTestRule.onNodeWithTag("announcement_0").assertExists()
  }

  @Test
  fun applyLocationFilter_withActualLocation_andThenClear() {
    // Let's simulate a scenario where we first show the bottom sheet and apply a real location
    // filter
    // locationFilterApplied should become true after applying
    composeTestRule.setContent {
      AnnouncementsScreen(
          announcementViewModel = announcementViewModel,
          preferencesViewModel = preferencesViewModel,
          workerProfileViewModel = workerProfileViewModel,
          categoryViewModel = categoryViewModel,
          accountViewModel = accountViewModel,
          navigationActions = mockNavigationActions)
    }

    // Show filters
    composeTestRule.onNodeWithTag("tuneButton").performClick()
    // Click location to show bottom sheet
    composeTestRule.onNodeWithTag("filter_button_Location").performClick()

    // Select a location from the worker profile, index 1 (but we must have test tags from bottom
    // sheet)
    // For simplicity, we assume we can select the second option (worker's location)
    // In your bottomSheet test code, you had a testTag "locationOptionRow1" and "applyButton"
    composeTestRule.onNodeWithTag("locationOptionRow1").performClick()
    composeTestRule.onNodeWithTag("applyButton").performClick()

    // locationFilterApplied = true now
    // Check that announcements still exist
    composeTestRule.onNodeWithTag("worker_profiles_list").assertExists()

    // Clear filters now by pressing "Clear"
    composeTestRule.onNodeWithTag("filter_button_Clear").performClick()
    // locationFilterApplied = false, all announcements should remain visible
    composeTestRule.onNodeWithTag("announcement_0", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("announcement_1", useUnmergedTree = true).assertExists()
  }

  @Test
  fun fetchAnnouncementImagesIsCalledForEachAnnouncement() {
    verify(mockAnnouncementRepository, times(1)).getAnnouncements(any(), any())

    composeTestRule.setContent {
      AnnouncementsScreen(
          announcementViewModel = announcementViewModel,
          preferencesViewModel = preferencesViewModel,
          workerProfileViewModel = workerProfileViewModel,
          categoryViewModel = categoryViewModel,
          accountViewModel = accountViewModel,
          navigationActions = mockNavigationActions)
    }

    // Now just verify that fetchAnnouncementsImagesAsBitmaps is called once per announcement
    verify(mockAnnouncementRepository, times(sampleAnnouncements.size))
        .fetchAnnouncementsImagesAsBitmaps(anyString(), any(), any())
  }

  @Test
  fun alreadyAppliedLocationFilter_reapplyFiltersAfterNewLocation() {
    composeTestRule.setContent {
      AnnouncementsScreen(
          announcementViewModel = announcementViewModel,
          preferencesViewModel = preferencesViewModel,
          workerProfileViewModel = workerProfileViewModel,
          categoryViewModel = categoryViewModel,
          accountViewModel = accountViewModel,
          navigationActions = mockNavigationActions)
    }

    // Show filters
    composeTestRule.onNodeWithTag("tuneButton").performClick()
    // Click location
    composeTestRule.onNodeWithTag("filter_button_Location").performClick()
    // Select current location and apply
    composeTestRule.onNodeWithTag("locationOptionRow0").performClick()
    composeTestRule.onNodeWithTag("applyButton").performClick()

    composeTestRule.onNodeWithTag("filter_button_Location").performClick()
    composeTestRule.onNodeWithTag("locationOptionRow1").performClick()
    composeTestRule.onNodeWithTag("applyButton").performClick()

    composeTestRule.onNodeWithTag("worker_profiles_list").assertExists()
  }
}
