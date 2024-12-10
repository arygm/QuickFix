package com.arygm.quickfix.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.util.*

open class GeocoderWrapper(private val context: Context) {
  private val geocoder: Geocoder = Geocoder(context, Locale.getDefault())

  fun getFromLocation(latitude: Double, longitude: Double, maxResults: Int): List<Address>? {
    return try {
      geocoder.getFromLocation(latitude, longitude, maxResults)
    } catch (e: Exception) {
      null
    }
  }
}
