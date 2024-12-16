package com.arygm.quickfix.ui.elements

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.ui.theme.QuickFixTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickFixStepperTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Sample data for testing
  private val steps = listOf("Info", "Settings", "Review")
  private val icons = listOf(Icons.Default.Info, Icons.Default.Settings, Icons.Default.Check)

  @Test
  fun stepperRendersCorrectNumberOfSteps() {
    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixStepper(
            steps = steps,
            icons = icons,
            currentStep = 1,
            heightRatio = 1.dp,
            widthRatio = 1.dp,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined))
      }
    }

    steps.forEachIndexed { index, _ ->
      composeTestRule.onNodeWithTag("Step_$index").assertExists()
      composeTestRule.onNodeWithTag("StepIcon_$index").assertExists()
      composeTestRule.onNodeWithTag("StepLabel_$index").assertExists()
      composeTestRule.onNodeWithTag("StepDescription_$index").assertExists()
      if (index < steps.size - 1) {
        composeTestRule.onNodeWithTag("Connector_$index").assertExists()
      }
    }
  }

  @Test
  fun stepperHighlightsCurrentStep_step1() {
    val currentStep = 1 // 1-based index
    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixStepper(
            steps = steps,
            icons = icons,
            currentStep = currentStep,
            heightRatio = 1.dp,
            widthRatio = 1.dp,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined))
      }
    }

    steps.forEachIndexed { index, _ ->
      when (index + 1) {
        currentStep -> {
          // Current step assertions
          composeTestRule
              .onNodeWithTag("StepIcon_$index")
              .assertExists()
              .assert(hasAnyDescendant(hasContentDescription("Step Icon")))
          // You can add more assertions related to the current step's appearance
        }
        in 1 until currentStep -> {
          // Steps before current should be marked as done
          composeTestRule.onNodeWithTag("StepIcon_$index").assertExists()
          // Add assertions to verify 'done' state if possible
        }
        else -> {
          // Steps after current should be to-do
          composeTestRule.onNodeWithTag("StepIcon_$index").assertExists()
          // Add assertions to verify 'to-do' state if possible
        }
      }
    }
  }

  @Test
  fun stepperHighlightsCurrentStep_step2() {
    val currentStep = 2 // 1-based index
    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixStepper(
            steps = steps,
            icons = icons,
            currentStep = currentStep,
            heightRatio = 1.dp,
            widthRatio = 1.dp,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined))
      }
    }

    steps.forEachIndexed { index, _ ->
      when (index + 1) {
        currentStep -> {
          // Current step assertions
          composeTestRule
              .onNodeWithTag("StepIcon_$index")
              .assertExists()
              .assert(hasAnyDescendant(hasContentDescription("Step Icon")))
          // You can add more assertions related to the current step's appearance
        }
        in 1 until currentStep -> {
          // Steps before current should be marked as done
          composeTestRule.onNodeWithTag("StepIcon_$index").assertExists()
          // Add assertions to verify 'done' state if possible
        }
        else -> {
          // Steps after current should be to-do
          composeTestRule.onNodeWithTag("StepIcon_$index").assertExists()
          // Add assertions to verify 'to-do' state if possible
        }
      }
    }
  }

  @Test
  fun stepperHighlightsCurrentStep_step3() {
    val currentStep = 3 // 1-based index
    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixStepper(
            steps = steps,
            icons = icons,
            currentStep = currentStep,
            heightRatio = 1.dp,
            widthRatio = 1.dp,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined))
      }
    }

    steps.forEachIndexed { index, _ ->
      when (index + 1) {
        currentStep -> {
          // Current step assertions
          composeTestRule
              .onNodeWithTag("StepIcon_$index")
              .assertExists()
              .assert(hasAnyDescendant(hasContentDescription("Step Icon")))
          // You can add more assertions related to the current step's appearance
        }
        in 1 until currentStep -> {
          // Steps before current should be marked as done
          composeTestRule.onNodeWithTag("StepIcon_$index").assertExists()
          // Add assertions to verify 'done' state if possible
        }
        else -> {
          // Steps after current should be to-do
          composeTestRule.onNodeWithTag("StepIcon_$index").assertExists()
          // Add assertions to verify 'to-do' state if possible
        }
      }
    }
  }

  @Test
  fun stepperDisplaysCorrectStepTitles() {
    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixStepper(
            steps = steps,
            icons = icons,
            currentStep = 1,
            heightRatio = 1.dp,
            widthRatio = 1.dp,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined))
      }
    }

    steps.forEachIndexed { index, step ->
      composeTestRule.onNodeWithTag("StepLabel_$index").assertTextEquals("STEP ${index + 1}")
      composeTestRule.onNodeWithTag("StepDescription_$index").assertTextEquals(step)
    }
  }

  @Test
  fun stepperShowsIconsCorrectly() {
    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixStepper(
            steps = steps,
            icons = icons,
            currentStep = 3,
            heightRatio = 1.dp,
            widthRatio = 1.dp,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined))
      }
    }

    steps.forEachIndexed { index, icon ->
      composeTestRule.onNodeWithTag("StepIcon_$index").assertExists().assertIsDisplayed()
      // Further icon-specific assertions can be added if necessary
    }
  }

  @Test
  fun stepperConnectorLinesExistBetweenSteps() {
    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixStepper(
            steps = steps,
            icons = icons,
            currentStep = 2,
            heightRatio = 1.dp,
            widthRatio = 1.dp,
            coroutineScope = CoroutineScope(Dispatchers.Unconfined))
      }
    }

    // Connectors are one less than steps
    for (index in 0 until steps.size - 1) {
      composeTestRule.onNodeWithTag("Connector_$index").assertExists()
    }
  }
}
