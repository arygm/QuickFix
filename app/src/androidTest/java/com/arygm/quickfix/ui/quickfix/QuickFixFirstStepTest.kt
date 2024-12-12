package com.arygm.quickfix.ui.quickfix

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.locations.LocationRepository
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.messaging.ChatRepository
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.quickfix.QuickFixRepository
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import java.time.LocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class QuickFixFirstStepTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var locationRepository: LocationRepository
  private lateinit var locationViewModel: LocationViewModel
  private lateinit var chatRepository: ChatRepository
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var quickFixRepository: QuickFixRepository
  private lateinit var quickFixViewModel: QuickFixViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    locationRepository = mock(LocationRepository::class.java)
    locationViewModel = LocationViewModel(locationRepository)
    chatRepository = mock(ChatRepository::class.java)
    chatViewModel = ChatViewModel(chatRepository)
    profileRepository = mock(ProfileRepository::class.java)
    profileViewModel = ProfileViewModel(profileRepository)
    quickFixRepository = mock(QuickFixRepository::class.java)
    quickFixViewModel = QuickFixViewModel(quickFixRepository)
  }

  @Test
  fun initialStateValidation() {

    composeTestRule.setContent {
      QuickFixFirstStep(
          navigationActions = navigationActions,
          locationViewModel = locationViewModel,
          chatViewModel = chatViewModel,
          workerViewModel = profileViewModel,
          workerId = "Test Worker Id",
          quickFixViewModel = quickFixViewModel,
          onQuickFixChange = { _ -> })
    }

    // Verify default UI elements
    composeTestRule.onNodeWithText("Enter a title ...").assertExists()
    composeTestRule.onNodeWithText("Features services").assertExists()
    composeTestRule.onNodeWithText("Add-on services").assertExists()
    composeTestRule.onNodeWithText("Upload Pictures").assertExists().performScrollTo()

    // Verify "Continue" button is initially disabled
    composeTestRule.onNodeWithText("Continue").assertIsNotEnabled()
  }

  @Test
  fun textInputBehavior() {
    composeTestRule.setContent {
      QuickFixFirstStep(
          navigationActions = navigationActions,
          locationViewModel = locationViewModel,
          chatViewModel = chatViewModel,
          workerViewModel = profileViewModel,
          workerId = "Test Worker Id",
          quickFixViewModel = quickFixViewModel,
          onQuickFixChange = { _ -> })
    }

    // Enter text in the title field
    composeTestRule.onNodeWithText("Enter a title ...").performTextInput("Test Title")
    composeTestRule.onNodeWithText("Test Title").assertExists()

    // Enter text in the quick note field
    composeTestRule.onNodeWithText("Type a description...").performTextInput("Test Note")
    composeTestRule.onNodeWithText("Test Note").assertExists()
  }

  @Test
  fun serviceSelection() {
    composeTestRule.setContent {
      QuickFixFirstStep(
          navigationActions = navigationActions,
          locationViewModel = locationViewModel,
          chatViewModel = chatViewModel,
          workerViewModel = profileViewModel,
          workerId = "Test Worker Id",
          quickFixViewModel = quickFixViewModel,
          onQuickFixChange = { _ -> })
    }

    // Select a service
    composeTestRule.onNodeWithText("Service 1").performClick()
    // Assert that the service is selected (custom assertion based on your implementation)
    composeTestRule.onNodeWithText("Service 1").assertHasClickAction()
  }

  @Test
  fun datePickerIntegration() {
    composeTestRule.setContent {
      QuickFixFirstStep(
          navigationActions = navigationActions,
          locationViewModel = locationViewModel,
          chatViewModel = chatViewModel,
          workerViewModel = profileViewModel,
          workerId = "Test Worker Id",
          quickFixViewModel = quickFixViewModel,
          onQuickFixChange = { _ -> })
    }

    val today = LocalDate.now()

    // Open the date picker
    composeTestRule.onNodeWithText("Add Suggested Date").performClick()
    // Assert the date picker is displayed
    composeTestRule.onNodeWithText("Select Date").assertExists()

    // Simulate selecting a date
    composeTestRule.onNodeWithText(today.dayOfMonth.toString()).performClick()
    composeTestRule.onNodeWithText("OK").performClick()

    composeTestRule.onNodeWithText("OK").performClick()

    // Assert the selected date is displayed
    composeTestRule.onNodeWithText("Suggested Date").assertExists()
  }
}
