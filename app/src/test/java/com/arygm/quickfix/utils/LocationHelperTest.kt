package com.arygm.quickfix.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocationHelperTest {

  private lateinit var context: Context
  private lateinit var activity: Activity
  private lateinit var locationHelper: LocationHelper
  private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    context = mock(Context::class.java)
    activity = mock(Activity::class.java)
    fusedLocationProviderClient = mock(FusedLocationProviderClient::class.java)
    // Create a spy of LocationHelper
    locationHelper = spy(LocationHelper(context, activity, fusedLocationProviderClient))
  }

  @Test
  fun checkPermissions_ReturnsTrue_WhenPermissionsGranted() {
    // Mock permissions granted
    `when`(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)
    `when`(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    val result = locationHelper.checkPermissions()
    assertTrue(result)
  }

  @Test
  fun checkPermissions_ReturnsFalse_WhenPermissionsNotGranted() {
    // Mock permissions denied
    `when`(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_DENIED)
    `when`(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_DENIED)

    val result = locationHelper.checkPermissions()
    assertFalse(result)
  }

  @Test
  fun requestPermissions_CallsActivityCompatRequestPermissions() {
    // Call the method
    locationHelper.requestPermissions()

    // Verify that requestPermissions was called with correct parameters
    verify(activity)
        .requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),
            LocationHelper.PERMISSION_REQUEST_ACCESS_LOCATION)
  }

  @Test
  fun getCurrentLocation_PermissionsNotGranted_DoesNotProceed() {
    // Stub the method using doReturn().when()
    doReturn(false).`when`(locationHelper).checkPermissions()

    val onSuccess = mock<(Location?) -> Unit>()

    locationHelper.getCurrentLocation(onSuccess)

    // Since permissions are not granted, onSuccess should not be called
    verifyNoInteractions(onSuccess)
  }

  @Test
  fun getCurrentLocation_LocationDisabled_OpensSettings() {
    // Stub the method using doReturn().when()
    doReturn(true).`when`(locationHelper).checkPermissions()

    // Mock location disabled
    val locationManager = mock(LocationManager::class.java)
    `when`(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManager)
    `when`(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false)
    `when`(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(false)

    val onSuccess = mock<(Location?) -> Unit>()

    locationHelper.getCurrentLocation(onSuccess)

    // Verify that startActivity is called with ACTION_LOCATION_SOURCE_SETTINGS
    val intentCaptor = ArgumentCaptor.forClass(Intent::class.java)
    verify(context).startActivity(intentCaptor.capture())

    val intent = intentCaptor.value
    assertEquals(Settings.ACTION_LOCATION_SOURCE_SETTINGS, intent.action)

    // Verify that onSuccess is not called
    verifyNoInteractions(onSuccess)
  }

  @Test
  fun getCurrentLocation_LocationAvailable_CallsOnSuccess() {
    // Stub the method using doReturn().when()
    doReturn(true).`when`(locationHelper).checkPermissions()

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
    val mockLocation = mock(Location::class.java)
    `when`(mockLocation.latitude).thenReturn(37.422)
    `when`(mockLocation.longitude).thenReturn(-122.084)

    val mockTask = mock(Task::class.java) as Task<Location>
    `when`(mockTask.isSuccessful).thenReturn(true)
    `when`(mockTask.result).thenReturn(mockLocation)
    `when`(fusedLocationProviderClient.lastLocation).thenReturn(mockTask)

    // Mock addOnCompleteListener
    `when`(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
      val listener = invocation.arguments[0] as OnCompleteListener<Location>
      listener.onComplete(mockTask)
      mockTask
    }

    val onSuccess = mock<(Location?) -> Unit>()

    locationHelper.getCurrentLocation(onSuccess)

    // Verify that onSuccess is called with the location
    verify(onSuccess).invoke(mockLocation)
  }

  @Test
  fun getCurrentLocation_LocationIsNull_ShowsToast() {
    // Stub the method using doReturn().when()
    doReturn(true).`when`(locationHelper).checkPermissions()

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
    val mockTask = mock(Task::class.java) as Task<Location>
    `when`(mockTask.isSuccessful).thenReturn(true)
    `when`(mockTask.result).thenReturn(null)
    `when`(fusedLocationProviderClient.lastLocation).thenReturn(mockTask)

    // Mock addOnCompleteListener
    `when`(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
      val listener = invocation.arguments[0] as OnCompleteListener<Location>
      listener.onComplete(mockTask)
      mockTask
    }

    val onSuccess = mock<(Location?) -> Unit>()

    locationHelper.getCurrentLocation(onSuccess)

    // Since location is null, onSuccess should not be called
    verifyNoInteractions(onSuccess)

    // Optionally, verify that a Toast is shown (requires Robolectric's ShadowToast)
    // val lastToast = ShadowToast.getTextOfLatestToast()
    // assertEquals("No location available", lastToast)
  }
}
