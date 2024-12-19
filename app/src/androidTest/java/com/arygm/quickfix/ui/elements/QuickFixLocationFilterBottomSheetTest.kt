package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.ui.theme.QuickFixTheme
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class QuickFixLocationFilterBottomSheetTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var onApplyClick: (Location, Int) -> Unit
  private lateinit var onDismissRequest: () -> Unit
  private lateinit var onClearClick: () -> Unit

  private val userProfile =
      UserProfile(
          locations =
              listOf(
                  Location(46.0, 6.0, "Home"),
                  Location(46.1, 6.1, "Work"),
                  Location(46.2, 6.2, "EPFL")),
          announcements = emptyList(),
          uid = "test_user")
  private val phoneLocation = Location(37.422, -122.084, "Phone Location")

  @Before
  fun setup() {
    onApplyClick = mock()
    onDismissRequest = mock()
    onClearClick = mock()
  }

  @Test
  fun bottomSheetIsVisibleWhenShowModalIsTrue() {
    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixLocationFilterBottomSheet(
            showModalBottomSheet = true,
            profile = userProfile,
            phoneLocation = phoneLocation,
            onApplyClick = onApplyClick,
            onDismissRequest = onDismissRequest,
            onClearClick = onClearClick,
            clearEnabled = false)
      }
    }

    composeTestRule.onNodeWithTag("locationFilterModalSheet").assertIsDisplayed()
  }

  @Test
  fun bottomSheetIsNotVisibleWhenShowModalIsFalse() {
    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixLocationFilterBottomSheet(
            showModalBottomSheet = false,
            profile = userProfile,
            phoneLocation = phoneLocation,
            onApplyClick = onApplyClick,
            onDismissRequest = onDismissRequest,
            onClearClick = onClearClick,
            clearEnabled = false)
      }
    }

    composeTestRule.onNodeWithTag("locationFilterModalSheet").assertDoesNotExist()
  }

  @Test
  fun checkAllUIElementsAreDisplayed() {
    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixLocationFilterBottomSheet(
            showModalBottomSheet = true,
            profile = userProfile,
            phoneLocation = phoneLocation,
            onApplyClick = onApplyClick,
            onDismissRequest = onDismissRequest,
            onClearClick = onClearClick,
            clearEnabled = false)
      }
    }

    composeTestRule
        .onNodeWithTag("locationFilterTitle")
        .assertIsDisplayed()
        .assertTextEquals("Search Radius")
    composeTestRule
        .onNodeWithTag("locationOptionsTitle")
        .assertIsDisplayed()
        .assertTextEquals("Location Options:")
    composeTestRule.onNodeWithTag("locationOptionsList").assertIsDisplayed()
    composeTestRule.onNodeWithTag("quickFixPriceRange").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("applyButton")
        .assertIsDisplayed()
        .assertIsNotEnabled() // No option selected yet
  }

  @Test
  fun selectingCurrentLocationEnablesApply_andCallsOnApplyClick() {
    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixLocationFilterBottomSheet(
            showModalBottomSheet = true,
            profile = userProfile,
            phoneLocation = phoneLocation,
            onApplyClick = onApplyClick,
            onDismissRequest = onDismissRequest,
            onClearClick = onClearClick,
            clearEnabled = false)
      }
    }

    // Select "Use my Current Location" which is option 0
    composeTestRule.onNodeWithTag("locationOptionRow0").performClick()

    // Now apply button should be enabled
    composeTestRule.onNodeWithTag("applyButton").assertIsEnabled()

    // Click apply
    composeTestRule.onNodeWithTag("applyButton").performClick()

    // Verify onApplyClick is called with phoneLocation and default range (0 initially, or as set in
    // the component)
    verify(onApplyClick).invoke(phoneLocation, 200) // 200 is the initial slider value based on code
    verify(onDismissRequest).invoke()
  }

  @Test
  fun selectingAUserSavedLocationEnablesApplyButtonAndCallsOnApply() {
    var appliedLocation: Location? = null
    var appliedRange = -1
    var dismissed = false

    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixLocationFilterBottomSheet(
            showModalBottomSheet = true,
            profile = userProfile,
            phoneLocation = phoneLocation,
            onApplyClick = { loc, r ->
              appliedLocation = loc
              appliedRange = r
            },
            onDismissRequest = { dismissed = true },
            onClearClick = onClearClick,
            clearEnabled = false)
      }
    }

    // "Use my Current Location" is index 0, "Home" is index 1, "Work" is index 2, "EPFL" is index 3
    composeTestRule.onNodeWithTag("locationOptionRow1").performClick()
    // Apply button should now be enabled
    composeTestRule.onNodeWithTag("applyButton").assertIsEnabled()

    // Click apply
    composeTestRule.onNodeWithTag("applyButton").performClick()

    composeTestRule.runOnIdle {
      // Check that onApplyClick was called with "Home"
      assertEquals("Home", appliedLocation?.name)
      // Default slider value = 200
      assertEquals(200, appliedRange)
      // Check that onDismissRequest was called
      assert(dismissed)
    }
  }

  @Test
  fun noLocationSelected_ApplyButtonDisabled() {
    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixLocationFilterBottomSheet(
            showModalBottomSheet = true,
            profile = userProfile,
            phoneLocation = phoneLocation,
            onApplyClick = onApplyClick,
            onDismissRequest = onDismissRequest,
            onClearClick = onClearClick,
            clearEnabled = false)
      }
    }

    // Ensure no radio button clicked yet
    composeTestRule.onNodeWithTag("applyButton").assertIsDisplayed().assertIsNotEnabled()
  }

  /** Test that the Clear button calls [onClearClick] when enabled. */
  @Test
  fun clearButtonCallsOnClearClickWhenEnabled() {
    var clearCalled = false
    var dismissCalled = false

    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixLocationFilterBottomSheet(
            showModalBottomSheet = true,
            profile = userProfile,
            phoneLocation = phoneLocation,
            onApplyClick = onApplyClick,
            onDismissRequest = { dismissCalled = true },
            onClearClick = { clearCalled = true },
            clearEnabled = true)
      }
    }

    // Simulate clicking the Clear button
    composeTestRule.onNodeWithTag("resetButton").performClick()

    // Assert that onClearClick and onDismissRequest were called
    composeTestRule.runOnIdle {
      assert(clearCalled) { "Expected onClearClick to be called" }
      assert(dismissCalled) { "Expected onDismissRequest to be called" }
    }
  }

  /**
   * Test that the Clear button is disabled and does not call [onClearClick] when [clearEnabled] is
   * false.
   */
  @Test
  fun clearButtonDoesNotCallOnClearClickWhenDisabled() {
    var clearCalled = false
    var dismissCalled = false

    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixLocationFilterBottomSheet(
            showModalBottomSheet = true,
            profile = userProfile,
            phoneLocation = phoneLocation,
            onApplyClick = onApplyClick,
            onDismissRequest = { dismissCalled = true },
            onClearClick = { clearCalled = true },
            clearEnabled = false)
      }
    }

    // Simulate clicking the Clear button
    composeTestRule.onNodeWithTag("resetButton").performClick()

    // Assert that onClearClick was not called
    composeTestRule.runOnIdle {
      assert(!clearCalled) { "Expected onClearClick not to be called" }
      assert(!dismissCalled) { "Expected onDismissRequest not to be called" }
    }
  }
}
