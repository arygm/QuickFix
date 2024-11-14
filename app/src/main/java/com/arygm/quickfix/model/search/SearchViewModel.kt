package com.arygm.quickfix.model.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchViewModel(
    private val workerProfileRepo: WorkerProfileRepositoryFirestore,
    private val categoryRepo: CategoryRepositoryFirestore
) : ViewModel() {

  private val _workerProfiles = MutableStateFlow<List<Profile>>(emptyList())
  val workerProfiles: StateFlow<List<Profile>> = _workerProfiles

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage

  private val _categories = MutableStateFlow<List<Category>>(emptyList())
  val categories: StateFlow<List<Category>> = _categories

  init {
    categoryRepo.init { fetchCategories() }
  }

  companion object {
    private val firestoreInstance by lazy { Firebase.firestore } // Singleton Firestore instance

    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(
                WorkerProfileRepositoryFirestore(firestoreInstance),
                CategoryRepositoryFirestore(firestoreInstance))
                as T
          }
        }
  }

  fun fetchCategories() {
    categoryRepo.fetchCategories(
        onSuccess = { categories -> _categories.value = categories as List<Category> },
        onFailure = { e -> Log.e("SearchViewModel", "Failed to fetch categories: ${e.message}") })
  }

  fun filterWorkerProfiles(
      hourlyRateThreshold: Double? = null,
      fieldOfWork: String? = null,
      location: Location? = null,
      maxDistanceInKm: Double? = null
  ) {
    val userLat = location?.latitude
    val userLon = location?.longitude

    workerProfileRepo.filterWorkers(
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
