package com.arygm.quickfix.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationHelper(
    private val context: Context,
    private val activity: Activity,
    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
) {

  companion object {
    const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
  }

  // Check if permissions are granted
  fun checkPermissions(): Boolean {
    return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
  }

  // Request location permissions
  fun requestPermissions() {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
        PERMISSION_REQUEST_ACCESS_LOCATION)
  }

  // Check if location services are enabled on the device
  private fun isLocationEnabled(): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
  }

  // Get the current location
  fun getCurrentLocation(onSuccess: (Location?) -> Unit) {
    if (checkPermissions()) {
      if (isLocationEnabled()) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
          requestPermissions()
          return
        }
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
          val location: Location? = task.result
          if (location == null) {
            Log.d("Location Helper", "No location available")
            // Toast.makeText(context, "No location available", Toast.LENGTH_SHORT).show()
          } else {
            onSuccess(location)
          }
        }
      } else {
        Log.d("Location Helper", "Please enable location services")
        // Toast.makeText(context, "Please enable location services", Toast.LENGTH_SHORT).show()
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
      }
    }
  }
}
