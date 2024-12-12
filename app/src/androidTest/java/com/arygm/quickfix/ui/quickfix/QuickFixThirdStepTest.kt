package com.arygm.quickfix.ui.quickfix

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.bill.BillField
import com.arygm.quickfix.model.bill.Units
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixRepository
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.google.firebase.Timestamp
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickFixThirdStepTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var quickFixRepository: QuickFixRepository
  private lateinit var quickFixViewModel: QuickFixViewModel

  // Sample data for QuickFix
  private val mockQuickFix =
      QuickFix(
          uid = "quickfix123",
          status = Status.PENDING,
          imageUrl = listOf("https://example.com/image1.png", "https://example.com/image2.png"),
          date = listOf(Timestamp.now(), Timestamp(1234567890, 0)),
          time = Timestamp.now(),
          includedServices = listOf(IncludedService("Service A"), IncludedService("Service B")),
          addOnServices = listOf(AddOnService("AddOn A")),
          workerId = "worker123",
          userId = "user123",
          chatUid = "chat123",
          title = "Fix the Sink",
          description = "Replace the leaking sink in the kitchen.",
          bill =
              listOf(
                  BillField(
                      description = "Sink Replacement",
                      unit = Units.H,
                      amount = 2.0,
                      unitPrice = 50.0,
                      total = 100.0)),
          location = Location(name = "123 Main St"))

  private val mockWorkerProfile =
      WorkerProfile(
          displayName = "John Doe",
          fieldOfWork = "Plumbing",
          // Initialize other fields as necessary
      )

  @Before
  fun setup() {
    quickFixViewModel = mockk<QuickFixViewModel>(relaxed = true)
    every { quickFixViewModel.updateQuickFix(any(), any(), any()) } just Runs
  }

  @Test
  fun addBillField_updatesList() {
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = mockQuickFix,
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { _ -> })
    }

    // Click on Add Bill Field Button
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Verify that a new bill field is added by checking the description TextField
    composeTestRule.onNodeWithTag("DescriptionTextField_0").assertExists()
  }

  @Test
  fun deleteBillField_removesFromList() {
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix =
              mockQuickFix.copy(
                  bill =
                      listOf(
                          BillField(
                              description = "Sink Replacement",
                              unit = Units.H,
                              amount = 2.0,
                              unitPrice = 50.0,
                              total = 100.0),
                          BillField(
                              description = "Pipe Fix",
                              unit = Units.H,
                              amount = 1.0,
                              unitPrice = 60.0,
                              total = 60.0))),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { _ -> })
    }

    // Add a bill field
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Verify initial bill fields
    composeTestRule.onNodeWithTag("DescriptionTextField_0").assertExists()
    composeTestRule.onNodeWithTag("DescriptionTextField_1").assertExists()

    // Delete the first bill field
    composeTestRule.onNodeWithTag("DeleteBillFieldButton_0").performClick()

    // Verify that the first bill field is removed and the second becomes first
    composeTestRule
        .onNodeWithTag("DescriptionTextField_0")
        .assertExists() // Now the second bill field should be at index 0
    composeTestRule.onNodeWithTag("DescriptionTextField_1").assertDoesNotExist()
  }

  @Test
  fun enterDescription_updatesBillField() {
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = mockQuickFix.copy(bill = emptyList()),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { _ -> })
    }

    // Add a bill field
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Enter text into the description field
    val description = "Replace pipes"
    composeTestRule.onNodeWithTag("DescriptionTextField_0").performTextInput(description)

    // Simulate focus loss
    composeTestRule.onRoot().performClick()

    // Verify that the text is correctly entered
    composeTestRule.onNodeWithTag("DescriptionTextField_0").assertTextEquals(description)
  }

  @Test
  fun enterAmount_updatesBillField() {
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = mockQuickFix.copy(bill = emptyList()),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { _ -> })
    }

    // Add a bill field
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Enter amount
    val amount = "3.0"
    composeTestRule.onNodeWithTag("AmountTextField_0").performTextInput(amount)

    // Simulate focus loss
    composeTestRule.onRoot().performClick()

    // Verify that the amount is correctly entered
    composeTestRule.onNodeWithTag("AmountTextField_0").assertTextEquals(amount)
  }

  @Test
  fun enterUnitPrice_updatesBillField() {
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = mockQuickFix.copy(bill = emptyList()),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { _ -> })
    }

    // Add a bill field
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Enter unit price
    val unitPrice = "50.0"
    composeTestRule.onNodeWithTag("UnitPriceTextField_0").performTextInput(unitPrice)

    // Simulate focus loss
    composeTestRule.onRoot().performClick()

    // Verify that the unit price is correctly entered
    composeTestRule.onNodeWithTag("UnitPriceTextField_0").assertTextEquals(unitPrice)
  }

  @Test
  fun selectUnit_updatesBillField() {
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = mockQuickFix.copy(bill = emptyList()),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { _ -> })
    }

    // Add a bill field
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Click on the Unit Dropdown
    composeTestRule.onNodeWithTag("UnitDropdown_0").performClick()

    // Select a unit from the dropdown (e.g., "H")
    composeTestRule
        .onNodeWithTag("UnitDropdownMenu_0")
        .onChildAt(1) // Assuming "H" is the second item
        .performClick()

    // Verify that the unit is updated
    composeTestRule.onNodeWithTag("UnitDropdown_0").assertTextContains("H")
  }

  @Test
  fun addAndSubmitQuickFix() {
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = mockQuickFix.copy(bill = emptyList()),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { _ -> })
    }

    // Click on Select Suggested Dates Button
    composeTestRule.onNodeWithTag("SelectSuggestedDatesButton").performClick()

    // Wait for the dialog to appear
    composeTestRule.onNodeWithTag("SuggestedDatesDialogTitle").assertIsDisplayed()

    // Select the first date
    composeTestRule.onNodeWithTag("RadioButton_0").performClick()

    // Click on OK button
    composeTestRule.onNodeWithTag("ConfirmSuggestedDatesButton").performClick()

    // Add a bill field
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Enter description
    composeTestRule.onNodeWithTag("DescriptionTextField_0").performTextInput("Replace sink")

    // Select unit
    composeTestRule.onNodeWithTag("UnitDropdown_0").performClick()
    composeTestRule
        .onNodeWithTag("UnitDropdownMenu_0")
        .onChildAt(0) // Assuming "M2" is the first item
        .performClick()

    // Enter amount
    composeTestRule.onNodeWithTag("AmountTextField_0").performTextInput("2")

    // Enter unit price
    composeTestRule.onNodeWithTag("UnitPriceTextField_0").performTextInput("100")

    // Simulate focus loss
    composeTestRule.onRoot().performClick()

    // Verify that the submit button is enabled
    composeTestRule.onNodeWithTag("SubmitQuickFixButton").assertIsEnabled()

    // Click submit
    composeTestRule.onNodeWithTag("SubmitQuickFixButton").performClick()

    // Verify that the ViewModel's update function was called with correct parameters
    verify {
      quickFixViewModel.updateQuickFix(
          match {
            it.status == Status.UNPAID &&
                it.date == mockQuickFix.date &&
                it.bill.size == 1 &&
                it.bill[0].description == "Replace sink" &&
                it.bill[0].unit == Units.M2 &&
                it.bill[0].amount == 2.0 &&
                it.bill[0].unitPrice == 100.0
            it.bill[0].total == 200.0
          },
          any(),
          any())
    }
  }

  @Test
  fun submitButton_disabledWhenFieldsIncomplete() {
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = mockQuickFix.copy(bill = emptyList()),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { _ -> })
    }

    // Add a bill field
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    // Enter incomplete data: only description
    composeTestRule.onNodeWithTag("DescriptionTextField_0").performTextInput("Fix leak")

    // Submit button should still be disabled
    composeTestRule.onNodeWithTag("SubmitQuickFixButton").assertIsNotEnabled()
  }

  @Test
  fun openAndSelectSuggestedDates() {
    // Create a QuickFix with specific dates
    val date1 = Timestamp.now()
    val date2 = Timestamp(1234567890, 0)
    val testQuickFix = mockQuickFix.copy(date = listOf(date1, date2))

    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix = testQuickFix,
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { _ -> })
    }

    // Click on Select Suggested Dates Button
    composeTestRule.onNodeWithTag("SelectSuggestedDatesButton").performClick()

    // Wait for the dialog to appear
    composeTestRule.onNodeWithTag("SuggestedDatesDialogTitle").assertIsDisplayed()

    // Select the first date
    composeTestRule.onNodeWithTag("RadioButton_0").performClick()

    // Click on OK button
    composeTestRule.onNodeWithTag("ConfirmSuggestedDatesButton").performClick()

    // Verify that the selected date is displayed in the main screen
    composeTestRule.onNodeWithTag("DateText_0").assertExists()
    composeTestRule.onNodeWithTag("TimeText_0").assertExists()
  }

  @Test
  fun overallTotal_calculatesCorrectly() {
    composeTestRule.setContent {
      QuickFixThirdStep(
          quickFix =
              mockQuickFix.copy(
                  bill =
                      listOf(
                          BillField(
                              description = "Sink Replacement",
                              unit = Units.H,
                              amount = 2.0,
                              unitPrice = 50.0,
                              total = 100.0),
                          BillField(
                              description = "Pipe Fix",
                              unit = Units.H,
                              amount = 1.0,
                              unitPrice = 60.0,
                              total = 60.0))),
          quickFixViewModel = quickFixViewModel,
          workerProfile = mockWorkerProfile,
          onQuickFixChange = { _ -> })
    }

    // Add a bill field
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()
    composeTestRule.onNodeWithTag("AddBillFieldButton").performClick()

    composeTestRule.onNodeWithTag("UnitPriceTextField_0").performTextInput("50.0")
    composeTestRule.onNodeWithTag("UnitPriceTextField_1").performTextInput("60.0")
    composeTestRule.onNodeWithTag("AmountTextField_0").performTextInput("2.0")
    composeTestRule.onNodeWithTag("AmountTextField_1").performTextInput("1.0")

    // Verify that the overall total is calculated correctly: 100 + 60 = 160
    composeTestRule.onNodeWithTag("OverallTotalValue").assertTextEquals("160.00 CHF")
  }
}
