package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class QuickFixCheckedListElementTest {

  private fun hasState(state: ToggleableState) =
      SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, state)

  @get:Rule val composeTestRule = createComposeRule()

  private val sampleServices = listOf("Service A", "Service B", "Service C")
  private val checkedStates = mutableStateListOf(false, false, false)

  @Test
  fun quickFixCheckedListElement_displaysServiceName() {
    composeTestRule.setContent {
      Column {
        sampleServices.forEachIndexed { index, _ ->
          QuickFixCheckedListElement(
              listServices = sampleServices, checkedStatesServices = checkedStates, index = index)
        }
      }
    }

    // Verify each service name is displayed
    sampleServices.forEach { service -> composeTestRule.onNodeWithText(service).assertExists() }
  }

  @Test
  fun quickFixCheckedListElement_toggleServiceSelection() {
    composeTestRule.setContent {
      Column {
        sampleServices.forEachIndexed { index, _ ->
          QuickFixCheckedListElement(
              listServices = sampleServices, checkedStatesServices = checkedStates, index = index)
        }
      }
    }

    // Click on the first service
    composeTestRule.onNodeWithText("Service A").performClick()

    // Verify the first service is selected
    composeTestRule.onNodeWithText("Service A").assert(hasState(ToggleableState.On))

    // Verify other services are not selected
    composeTestRule.onNodeWithText("Service B").assert(hasState(ToggleableState.Off))
    composeTestRule.onNodeWithText("Service C").assert(hasState(ToggleableState.Off))
  }

  @Test
  fun quickFixCheckedListElement_toggleMultipleServices() {
    composeTestRule.setContent {
      Column {
        sampleServices.forEachIndexed { index, _ ->
          QuickFixCheckedListElement(
              listServices = sampleServices, checkedStatesServices = checkedStates, index = index)
        }
      }
    }

    // Click on multiple services
    composeTestRule.onNodeWithText("Service A").performClick()
    composeTestRule.onNodeWithText("Service B").performClick()

    // Verify selected states
    composeTestRule.onNodeWithText("Service A").assert(hasState(ToggleableState.On))
    composeTestRule.onNodeWithText("Service B").assert(hasState(ToggleableState.On))
    composeTestRule.onNodeWithText("Service C").assert(hasState(ToggleableState.Off))
  }

  @Test
  fun quickFixCheckedListElement_toggleOffServiceSelection() {
    composeTestRule.setContent {
      Column {
        sampleServices.forEachIndexed { index, _ ->
          QuickFixCheckedListElement(
              listServices = sampleServices, checkedStatesServices = checkedStates, index = index)
        }
      }
    }

    // Click on a service to select it
    composeTestRule.onNodeWithText("Service A").performClick()

    // Verify it is selected
    composeTestRule.onNodeWithText("Service A").assert(hasState(ToggleableState.On))

    // Click again to deselect
    composeTestRule.onNodeWithText("Service A").performClick()

    // Verify it is not selected
    composeTestRule.onNodeWithText("Service A").assert(hasState(ToggleableState.Off))
  }
}
