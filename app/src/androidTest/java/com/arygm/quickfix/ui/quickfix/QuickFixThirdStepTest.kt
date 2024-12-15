package com.arygm.quickfix.ui.quickfix

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.AnnotatedString
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arygm.quickfix.model.bill.Units
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationRepository
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.messaging.ChatRepository
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixRepository
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.quickfix.QuickFixThirdStep
import com.google.firebase.Timestamp
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

class QuickFixThirdStepTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mocked dependencies
  private lateinit var navigationActions: NavigationActions
  private lateinit var locationRepository: LocationRepository
  private lateinit var locationViewModel: LocationViewModel
  private lateinit var chatRepository: ChatRepository
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var quickFixRepository: QuickFixRepository
  private lateinit var quickFixViewModel: QuickFixViewModel
  private lateinit var preferencesRepositoryDataStore: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel

  // Fake data setup
  private val fakeQuickFixUid = "qf_123"
  private val fakeQuickFix =
      QuickFix(
          uid = fakeQuickFixUid,
          status = Status.PENDING,
          imageUrl = listOf("https://example.com/image1.png", "https://example.com/image2.png"),
          date = listOf(Timestamp.now(), Timestamp(1234567890, 0)),
          time = Timestamp.now(),
          includedServices = listOf(IncludedService("Service A"), IncludedService("Service B")),
          addOnServices = listOf(AddOnService("AddOn A"), AddOnService("AddOn B")),
          workerId = "worker123",
          userId = "user123",
          chatUid = "chat123",
          title = "Fix the Sink",
          description = "Replace the leaking sink in the kitchen.",
          bill = emptyList(),
          location = Location(name = "123 Main St"))

  private val mockWorkerProfile =
      WorkerProfile(
          uid = "worker123",
          displayName = "John Doe",
          fieldOfWork = "Plumbing",
          includedServices = listOf(IncludedService("Service A"), IncludedService("Service B")),
          addOnServices = listOf(AddOnService("AddOn A"), AddOnService("AddOn B")))

  @Before
  fun setUp() {
    // Initialize mocks using Mockito
    navigationActions = mock(NavigationActions::class.java)
    locationRepository = mock(LocationRepository::class.java)
    locationViewModel = LocationViewModel(locationRepository)
    chatRepository = mock(ChatRepository::class.java)
    chatViewModel = ChatViewModel(chatRepository)
    profileRepository = mock(ProfileRepository::class.java)
    profileViewModel = ProfileViewModel(profileRepository)
    quickFixRepository = mock(QuickFixRepository::class.java)
    quickFixViewModel = QuickFixViewModel(quickFixRepository)
    preferencesRepositoryDataStore = mock(PreferencesRepository::class.java)
    preferencesViewModel = PreferencesViewModel(preferencesRepositoryDataStore)

    // Mock getPreferenceByKey for user_id
    val userIdKey = stringPreferencesKey("user_id")
    whenever(preferencesRepositoryDataStore.getPreferenceByKey(userIdKey))
        .thenReturn(MutableStateFlow("user123"))

    // Mock getPreferenceByKey for app_mode
    val appModeKey = stringPreferencesKey("app_mode")
    whenever(preferencesRepositoryDataStore.getPreferenceByKey(appModeKey))
        .thenReturn(MutableStateFlow("USER"))

    runBlocking {
      // Mock QuickFixRepository's addQuickFix method to simulate success
      doAnswer { invocation ->
            val quickFix = invocation.getArgument<QuickFix>(0)
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
            onSuccess()
            null
          }
          .whenever(quickFixRepository)
          .addQuickFix(any(), any(), any())

      // Mock QuickFixViewModel's getRandomUid
      whenever(quickFixViewModel.getRandomUid()).thenReturn("randomUid123")

      // Mock QuickFixRepository's updateQuickFix method to simulate success
      doAnswer { invocation ->
            val updatedQuickFix = invocation.getArgument<QuickFix>(0)
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
            onSuccess()
            null
          }
          .whenever(quickFixRepository)
          .updateQuickFix(any(), any(), any())

      // Mock ProfileRepository's fetch and update methods if necessary
      // For example:
      // whenever(profileRepository.fetchUserProfile(any())).thenReturn(fakeUserProfile)
    }
  }

  @Test
  fun addBillField_updatesList() = runTest {
    // Arrange: Set the composable content
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = fakeQuickFix.copy(bill = emptyList()),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { /* No-op for testing */},
          onQuickFixPay = { /* No-op for testing */},
          mode = AppMode.WORKER)
    }

    // Act: Click on Add Bill Field Button
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Assert: Verify that a new bill field is added by checking the description TextField
    composeTestRule.onNodeWithTag("DescriptionTextField_0").assertExists()
  }

  @Test
  fun deleteBillField_removesFromList() = runTest {
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = fakeQuickFix,
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { /* No-op for testing */},
          onQuickFixPay = { /* No-op for testing */},
          mode = AppMode.WORKER)
    }

    // Add a bill field
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Act: Delete the first bill field
    composeTestRule.onNodeWithTag("DeleteBillFieldButton_0").performClick()

    // Assert:
    composeTestRule
        .onNodeWithTag("DescriptionTextField_0")
        .assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("")))
    // - The second bill field should no longer exist
    composeTestRule.onNodeWithTag("DescriptionTextField_1").assertDoesNotExist()
  }

  @Test
  fun enterDescription_updatesBillField() = runTest {
    // Arrange: Add a bill field
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = fakeQuickFix.copy(bill = emptyList()),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { /* No-op for testing */},
          onQuickFixPay = { /* No-op for testing */},
          mode = AppMode.WORKER)
    }

    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Act: Enter text into the description field
    val description = "Replace pipes"
    composeTestRule.onNodeWithTag("DescriptionTextField_0").performTextInput(description)

    // Simulate focus loss to trigger any listeners
    composeTestRule.onRoot().performClick()

    // Assert: Verify that the text is correctly entered
    composeTestRule.onNodeWithTag("DescriptionTextField_0").assertTextEquals(description)
  }

  @Test
  fun enterAmount_updatesBillField() = runTest {
    // Arrange: Add a bill field
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = fakeQuickFix.copy(bill = emptyList()),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { /* No-op for testing */},
          onQuickFixPay = { /* No-op for testing */},
          mode = AppMode.WORKER)
    }

    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Act: Enter amount
    val amount = "3.0"
    composeTestRule.onNodeWithTag("AmountTextField_0").performTextInput(amount)

    // Simulate focus loss
    composeTestRule.onRoot().performClick()

    // Assert: Verify that the amount is correctly entered
    composeTestRule.onNodeWithTag("AmountTextField_0").assertTextEquals(amount)
  }

  @Test
  fun enterUnitPrice_updatesBillField() = runTest {
    // Arrange: Add a bill field
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = fakeQuickFix.copy(bill = emptyList()),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { /* No-op for testing */},
          onQuickFixPay = { /* No-op for testing */},
          mode = AppMode.WORKER)
    }

    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Act: Enter unit price
    val unitPrice = "50.0"
    composeTestRule.onNodeWithTag("UnitPriceTextField_0").performTextInput(unitPrice)

    // Simulate focus loss
    composeTestRule.onRoot().performClick()

    // Assert: Verify that the unit price is correctly entered
    composeTestRule.onNodeWithTag("UnitPriceTextField_0").assertTextEquals(unitPrice)
  }

  @Test
  fun selectUnit_updatesBillField() = runTest {
    // Arrange: Add a bill field
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = fakeQuickFix.copy(bill = emptyList()),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { /* No-op for testing */},
          onQuickFixPay = { /* No-op for testing */},
          mode = AppMode.WORKER)
    }

    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Act: Click on the Unit Dropdown
    composeTestRule.onNodeWithTag("UnitDropdown_0").performClick()

    // Select a unit from the dropdown (e.g., "M2")
    composeTestRule.onNodeWithTag("UnitDropdownMenu_0").onChildAt(0).performClick()

    // Assert: Verify that the unit is updated
    composeTestRule.onNodeWithTag("UnitDropdown_0").assertTextContains("M2")
  }

  @Test
  fun addAndSubmitQuickFix_success() = runTest {
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = fakeQuickFix,
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { /* Verify changes if needed */},
          onQuickFixPay = { /* No-op for testing */},
          mode = AppMode.WORKER)
    }

    // Act:
    // 1. Select Suggested Dates
    composeTestRule.onNodeWithTag("SelectSuggestedDatesButton").performClick()

    // 2. Interact with the date dialog
    composeTestRule.onNodeWithTag("SuggestedDatesDialogTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RadioButton_0").performClick()
    composeTestRule.onNodeWithTag("ConfirmSuggestedDatesButton").performClick()

    // 3. Add a bill field
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // 4. Enter description
    composeTestRule.onNodeWithTag("DescriptionTextField_0").performTextInput("Replace sink")

    // 5. Select unit
    composeTestRule.onNodeWithTag("UnitDropdown_0").performClick()
    composeTestRule
        .onNodeWithTag("UnitDropdownMenu_0")
        .onChildAt(0)
        .performClick() // Assuming "M2" is first

    // 6. Enter amount
    composeTestRule.onNodeWithTag("AmountTextField_0").performTextInput("2")

    // 7. Enter unit price
    composeTestRule.onNodeWithTag("UnitPriceTextField_0").performTextInput("100")

    // Simulate focus loss to trigger any listeners
    composeTestRule.onRoot().performClick()

    // 8. Verify that the submit button is enabled
    composeTestRule.onNodeWithTag("SubmitQuickFixButton").assertIsEnabled()

    // 9. Click submit
    composeTestRule.onNodeWithTag("SubmitQuickFixButton").performClick()

    // Assert: Verify that the ViewModel's updateQuickFix was called with correct parameters
    val quickFixCaptor: ArgumentCaptor<QuickFix> = ArgumentCaptor.forClass(QuickFix::class.java)
    verify(quickFixRepository, times(1)).updateQuickFix(capture(quickFixCaptor), any(), any())

    val capturedQuickFix = quickFixCaptor.value
    assertNotNull(capturedQuickFix)
    assertEquals(Status.UNPAID, capturedQuickFix.status)
    assertEquals(fakeQuickFix.date.first(), capturedQuickFix.date.first())
    assertEquals(1, capturedQuickFix.bill.size)
    assertEquals("Replace sink", capturedQuickFix.bill[0].description)
    assertEquals(Units.M2, capturedQuickFix.bill[0].unit)
    assertEquals(2.0, capturedQuickFix.bill[0].amount, 0.001)
    assertEquals(100.0, capturedQuickFix.bill[0].unitPrice, 0.001)
    assertEquals(200.0, capturedQuickFix.bill[0].total, 0.001)
  }

  @Test
  fun submitButton_disabledWhenFieldsIncomplete() = runTest {
    // Arrange: Initialize QuickFix with no bill fields
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = fakeQuickFix.copy(bill = emptyList()),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { /* No-op for testing */},
          onQuickFixPay = { /* No-op for testing */},
          mode = AppMode.WORKER)
    }

    // Act: Add a bill field and enter incomplete data (only description)
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()
    composeTestRule.onNodeWithTag("DescriptionTextField_0").performTextInput("Fix leak")

    // Simulate focus loss
    composeTestRule.onRoot().performClick()

    // Assert: Submit button should still be disabled
    composeTestRule.onNodeWithTag("SubmitQuickFixButton").assertIsNotEnabled()
  }

  @Test
  fun openAndSelectSuggestedDates() = runTest {
    // Arrange: Initialize QuickFix with specific dates
    val date1 = Timestamp.now()
    val date2 = Timestamp(1234567890, 0)
    val testQuickFix = fakeQuickFix.copy(date = listOf(date1, date2))

    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = testQuickFix,
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { /* No-op for testing */},
          onQuickFixPay = { /* No-op for testing */},
          mode = AppMode.WORKER)
    }

    // Act: Open Suggested Dates Dialog and select the first date
    composeTestRule.onNodeWithTag("SelectSuggestedDatesButton").performClick()
    composeTestRule.onNodeWithTag("SuggestedDatesDialogTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RadioButton_0").performClick()
    composeTestRule.onNodeWithTag("ConfirmSuggestedDatesButton").performClick()

    // Assert: Verify that the selected date is displayed in the main screen
    composeTestRule.onNodeWithTag("DateText_0").assertExists()
    composeTestRule.onNodeWithTag("TimeText_0").assertExists()
  }

  @Test
  fun overallTotal_calculatesCorrectly() = runTest {
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = fakeQuickFix,
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { /* No-op for testing */},
          onQuickFixPay = { /* No-op for testing */},
          mode = AppMode.WORKER)
    }

    // Act: Add two more bill fields
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Enter data for first new bill field
    composeTestRule.onNodeWithTag("DescriptionTextField_0").performTextInput("Electrical Fix")
    composeTestRule.onNodeWithTag("UnitDropdown_0").performClick()
    composeTestRule
        .onNodeWithTag("UnitDropdownMenu_0")
        .onChildAt(0)
        .performClick() // Assuming "M2" is first
    composeTestRule.onNodeWithTag("AmountTextField_0").performTextInput("3.0")
    composeTestRule.onNodeWithTag("UnitPriceTextField_0").performTextInput("100")

    // Enter data for second new bill field
    composeTestRule.onNodeWithTag("DescriptionTextField_1").performTextInput("Painting")
    composeTestRule.onNodeWithTag("UnitDropdown_1").performClick()
    composeTestRule
        .onNodeWithTag("UnitDropdownMenu_1")
        .onChildAt(0)
        .performClick() // Assuming "M2" is first
    composeTestRule.onNodeWithTag("AmountTextField_1").performTextInput("1.0")
    composeTestRule.onNodeWithTag("UnitPriceTextField_1").performTextInput("150")

    // Simulate focus loss
    composeTestRule.onRoot().performClick()

    // Assert: Verify that the overall total is calculated correctly: 300 + 150 = 450
    composeTestRule.onNodeWithTag("OverallTotalValue").assertTextEquals("450.00 CHF")
  }
}
