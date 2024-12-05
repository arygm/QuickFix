package com.arygm.quickfix.kaspresso

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.kaspresso.screen.HomeScreenObject
import com.arygm.quickfix.model.quickfix.QuickFixRepository
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.ui.home.HomeScreen
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class HomeScreenTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testHomeScreen() = run {
    step("Set up the HomeScreen") {
      composeTestRule.setContent {
        val navigationActions = mock(NavigationActions::class.java)
        val quickFixRepository = mock(QuickFixRepository::class.java)
        val quickFixViewModel = QuickFixViewModel(quickFixRepository)
        HomeScreen(navigationActions, true, quickFixViewModel)
      }
    }

    step("Check UI elements on HomeScreen") {
      // You can add assertions and interactions here
      ComposeScreen.onComposeScreen<HomeScreenObject>(composeTestRule) {
        composeTestRule.onRoot().printToLog("TAG")
        notification { assertIsDisplayed() }
        searchBar { assertIsDisplayed() }
      }
    }
    step("Click inside the search bar to gain focus") {
      composeTestRule.onNodeWithTag("searchBar").performClick()
    }

    // Step 3: Simulate clicking outside the search bar to lose focus
    step("Click outside the search bar") {
      composeTestRule.onNodeWithTag("homeContent").performClick()
    }

    // Step 4: Assert that the search bar has lost focus
    step("Assert the search bar has lost focus") {
      composeTestRule.onNodeWithTag("searchBar").assertIsNotFocused()
    }
    step("Verify Popular Services and Upcoming QuickFixes sections are displayed") {
      // Verify Popular Services title is displayed
      composeTestRule.onNodeWithTag("PopularServicesRow").assertIsDisplayed()

      // Verify Upcoming QuickFixes title is displayed
      composeTestRule.onNodeWithTag("UpcomingQuickFixes").assertIsDisplayed()
    }
  }
}
