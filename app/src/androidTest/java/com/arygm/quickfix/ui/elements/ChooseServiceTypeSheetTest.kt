package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.ui.theme.QuickFixTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ChooseServiceTypeSheetTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var onDismissRequest: () -> Unit
  private lateinit var onServiceSelect: (String) -> Unit
  private lateinit var onApplyClick: () -> Unit
  private lateinit var onResetClick: () -> Unit

  @Before
  fun setup() {
    onDismissRequest = mock()
    onServiceSelect = mock()
    onApplyClick = mock()
    onResetClick = mock()
  }

  @Test
  fun chooseServiceTypeSheet_displaysCorrectly() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")
    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            selectedService = "Exterior Painter",
            onServiceSelect = onServiceSelect,
            onApplyClick = onApplyClick,
            onResetClick = onResetClick,
            onDismissRequest = onDismissRequest)
      }
    }

    // Assert the dialog is displayed
    composeTestRule.onNodeWithTag("serviceTypeText").assertIsDisplayed()
    composeTestRule.onNodeWithText("Service Type").assertIsDisplayed()

    // Assert divider is displayed
    composeTestRule.onNodeWithTag("serviceTypeText").assertIsDisplayed()

    // Assert each service type is displayed
    serviceTypes.forEach { service ->
      composeTestRule.onNodeWithTag("serviceText_$service").assertIsDisplayed()
      composeTestRule.onNodeWithText(service).assertIsDisplayed()
    }

    // Assert Apply and Reset buttons are displayed
    composeTestRule.onNodeWithTag("applyButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("resetButton").assertIsDisplayed()
    composeTestRule.onNodeWithText("Apply").assertIsDisplayed()
    composeTestRule.onNodeWithText("Reset").assertIsDisplayed()
  }

  @Test
  fun chooseServiceTypeSheet_applyButtonClick_invokesCallback() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")
    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            selectedService = "Exterior Painter",
            onServiceSelect = onServiceSelect,
            onApplyClick = onApplyClick,
            onResetClick = onResetClick,
            onDismissRequest = onDismissRequest)
      }
    }

    // Click on Apply button
    composeTestRule.onNodeWithTag("applyButton").performClick()

    // Verify the apply callback is triggered
    verify(onApplyClick).invoke()
  }

  @Test
  fun chooseServiceTypeSheet_resetButtonClick_invokesCallback() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")
    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            selectedService = "Exterior Painter",
            onServiceSelect = onServiceSelect,
            onApplyClick = onApplyClick,
            onResetClick = onResetClick,
            onDismissRequest = onDismissRequest)
      }
    }

    // Click on Reset button
    composeTestRule.onNodeWithTag("resetButton").performClick()

    // Verify the reset callback is triggered
    verify(onResetClick).invoke()
  }

  @Test
  fun chooseServiceTypeSheet_serviceSelect_invokesCallback() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")
    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            selectedService = "",
            onServiceSelect = onServiceSelect,
            onApplyClick = onApplyClick,
            onResetClick = onResetClick,
            onDismissRequest = onDismissRequest)
      }
    }

    // Click on a service type
    composeTestRule.onNodeWithTag("serviceText_Exterior Painter").performClick()

    // Verify the service select callback is triggered with the correct parameter
    verify(onServiceSelect).invoke("Exterior Painter")
  }

  @Test
  fun chooseServiceTypeSheet_notDisplayedWhenShowModalBottomSheetIsFalse() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")
    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = false,
            serviceTypes = serviceTypes,
            selectedService = "",
            onServiceSelect = onServiceSelect,
            onApplyClick = onApplyClick,
            onResetClick = onResetClick,
            onDismissRequest = onDismissRequest)
      }
    }

    // Assert the dialog is not displayed
    composeTestRule.onNodeWithTag("serviceTypeText").assertDoesNotExist()
  }
}
