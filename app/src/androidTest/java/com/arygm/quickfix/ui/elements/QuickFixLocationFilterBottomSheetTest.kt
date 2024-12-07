package com.arygm.quickfix.ui.elements

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.app.ActivityCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.arygm.quickfix.utils.LocationHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class QuickFixLocationFilterBottomSheetTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val userProfile =
      UserProfile(
          locations =
              listOf(
                  Location(46.0, 6.0, "Home"),
                  Location(46.1, 6.1, "Work"),
                  Location(46.2, 6.2, "EPFL")),
          announcements = emptyList(),
          uid = "test_user")
  private lateinit var context: Context
  private lateinit var locationHelper: LocationHelper
  private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
  private lateinit var activity: Activity

  @Before
  fun setup() {
    context = mock(Context::class.java)
    activity = mock(Activity::class.java)
    fusedLocationProviderClient = mock(FusedLocationProviderClient::class.java)
    // Create a spy of LocationHelper
    locationHelper = spy(LocationHelper(context, activity, fusedLocationProviderClient))
    `when`(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)
    `when`(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)
  }

  @Test
  fun bottomSheetIsVisibleWhenShowModalIsTrue() {
    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixLocationFilterBottomSheet(
            showModalBottomSheet = true,
            userProfile = userProfile,
            locationHelper = locationHelper,
            onApplyClick = { _, _ -> },
            onDismissRequest = {},
            onClearClick = {},
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
            userProfile = userProfile,
            locationHelper = locationHelper,
            onApplyClick = { _, _ -> },
            onDismissRequest = {},
            onClearClick = {},
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
            userProfile = userProfile,
            locationHelper = locationHelper,
            onApplyClick = { _, _ -> },
            onDismissRequest = {},
            onClearClick = {},
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
  fun selectingCurrentLocationEnablesApplyButtonAndCallsOnApply() {

    // Mock location enabled
    val locationManager = mock(LocationManager::class.java)
    `when`(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManager)
    `when`(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true)
    `when`(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true)

    // Mock permissions check
    `when`(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)
    `when`(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    // Mock fusedLocationProviderClient.lastLocation
    val mockLocation = mock(android.location.Location::class.java)
    `when`(mockLocation.latitude).thenReturn(37.422)
    `when`(mockLocation.longitude).thenReturn(-122.084)

    val mockTask = mock(Task::class.java) as Task<android.location.Location>
    `when`(mockTask.isSuccessful).thenReturn(true)
    `when`(mockTask.result).thenReturn(mockLocation)
    `when`(fusedLocationProviderClient.lastLocation).thenReturn(mockTask)

    // Mock addOnCompleteListener
    `when`(mockTask.addOnCompleteListener(Mockito.any())).thenAnswer { invocation ->
      val listener = invocation.arguments[0] as OnCompleteListener<android.location.Location>
      listener.onComplete(mockTask)
      mockTask
    }

    var appliedLocation: Location? = null
    var appliedRange = -1
    var dismissed = false

    composeTestRule.setContent {
      QuickFixTheme {
        QuickFixLocationFilterBottomSheet(
            showModalBottomSheet = true,
            userProfile = userProfile,
            locationHelper = locationHelper,
            onApplyClick = { loc, r ->
              appliedLocation = loc
              appliedRange = r
            },
            onDismissRequest = { dismissed = true },
            onClearClick = {},
            clearEnabled = false)
      }
    }

    // Select the "Use my Current Location" radio
    composeTestRule.onNodeWithTag("locationOptionRow0").performClick()
    // Now apply button should be enabled
    composeTestRule.onNodeWithTag("applyButton").assertIsEnabled()

    // Click apply
    composeTestRule.onNodeWithTag("applyButton").performClick()

    composeTestRule.runOnIdle {
      // Check that onApplyClick was called
      assertEquals("Phone Location", appliedLocation?.name)
      // Default slider value = 200 (as defined in code)
      assertEquals(200, appliedRange)
      // Check that onDismissRequest was called
      assert(dismissed)
    }
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
            userProfile = userProfile,
            locationHelper = locationHelper, // We won't call getCurrentLocation this time
            onApplyClick = { loc, r ->
              appliedLocation = loc
              appliedRange = r
            },
            onDismissRequest = { dismissed = true },
            onClearClick = {},
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
            userProfile = userProfile,
            locationHelper = locationHelper,
            onApplyClick = { _, _ -> },
            onDismissRequest = {},
            onClearClick = {},
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
            userProfile = userProfile,
            locationHelper = locationHelper,
            onApplyClick = { _, _ -> },
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
            userProfile = userProfile,
            locationHelper = locationHelper,
            onApplyClick = { _, _ -> },
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
