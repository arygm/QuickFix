package com.arygm.quickfix.model.profile

import androidx.lifecycle.ViewModel
import com.arygm.quickfix.model.Location.Location
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class searchViewModel(private val repository: WorkerProfileRepositoryFirestore) : ViewModel() {

  private val _workerProfiles = MutableStateFlow<List<Profile>>(emptyList())
  val workerProfiles: StateFlow<List<Profile>> = _workerProfiles

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage

  fun filterWorkerProfiles(
      hourlyRateThreshold: Double? = null,
      fieldOfWork: String? = null,
      location: Location? = null,
      maxDistanceInKm: Double? = null
  ) {
    val userLat = location?.latitude
    val userLon = location?.longitude

    repository.filterWorkers(
        hourlyRateThreshold,
        fieldOfWork,
        location,
        maxDistanceInKm,
        { profiles ->
          if (userLat != null && userLon != null && maxDistanceInKm != null) {
            val filteredProfiles =
                profiles.filter { profile ->
                  val location =
                      profile.location ?: return@filter false // Ensure location is non-null
                  val workerLat = location.latitude
                  val workerLon = location.longitude
                  val distance = calculateDistance(userLat, userLon, workerLat, workerLon)
                  distance <= maxDistanceInKm
                }
            _workerProfiles.value = filteredProfiles
          } else {
            _workerProfiles.value = profiles
          }
        },
        { error -> _errorMessage.value = error.message })
  }

  private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // Radius of the Earth in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a =
        sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
  }
}
