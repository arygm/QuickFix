package com.arygm.quickfix.model.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.Subcategory
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class SearchViewModel(
    private val workerProfileRepo: WorkerProfileRepositoryFirestore,
) : ViewModel() {

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery

  val _searchSubcategory = MutableStateFlow<Subcategory?>(null)
  val searchSubcategory: StateFlow<Subcategory?> = _searchSubcategory

  val _searchCategory = MutableStateFlow<Category?>(null)
  val searchCategory: StateFlow<Category?> = _searchCategory

  val _popularServiceCategory = MutableStateFlow<Category?>(null)
  val popularServiceCategory: StateFlow<Category?> = _popularServiceCategory

  val _workerProfiles = MutableStateFlow<List<WorkerProfile>>(emptyList())
  val workerProfiles: StateFlow<List<WorkerProfile>> = _workerProfiles

  val _subCategoryWorkerProfiles = MutableStateFlow<List<WorkerProfile>>(emptyList())
  val subCategoryWorkerProfiles: StateFlow<List<WorkerProfile>> = _subCategoryWorkerProfiles

  val _workerProfilesSuggestions = MutableStateFlow<List<WorkerProfile>>(emptyList())
  val workerProfilesSuggestions: StateFlow<List<WorkerProfile>> = _workerProfilesSuggestions

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage

  companion object {
    private val firestoreInstance by lazy { Firebase.firestore } // Singleton Firestore instance
    private val storageInstance by lazy { Firebase.storage } // Singleton Storage instance
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(
                WorkerProfileRepositoryFirestore(firestoreInstance, storageInstance))
                as T
          }
        }
  }

  fun setSearchQuery(query: String) { // Used for test purposes
    _searchQuery.value = query
  }

  fun setSearchSubcategory(subcategory: Subcategory) { // Used for test purposes
    _searchSubcategory.value = subcategory
  }

  fun setSearchCategory(category: Category) { // Used for test purposes
    _searchCategory.value = category
  }

  fun setPopularServiceCategory(category: Category?) { // Used for test purposes
    _popularServiceCategory.value = category
  }

  fun setWorkerProfiles(workerProfiles: List<WorkerProfile>) { // Used for test purposes
    _workerProfiles.value = workerProfiles
  }

  fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // Radius of the Earth in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a =
        sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
  }

  fun filterWorkersByAvailability(
      workers: List<WorkerProfile>,
      selectedDays: List<LocalDate>,
      selectedHour: Int,
      selectedMinute: Int
  ): List<WorkerProfile> {
    return workers
        .filter { worker ->
          val workingStart = worker.workingHours.first
          val workingEnd = worker.workingHours.second

          // Check if the selected time is within the worker's working hours
          (selectedHour > workingStart.hour ||
              (selectedHour == workingStart.hour && selectedMinute >= workingStart.minute)) &&
              (selectedHour < workingEnd.hour ||
                  (selectedHour == workingEnd.hour && selectedMinute <= workingEnd.minute))
        }
        .filter { worker -> selectedDays.none { day -> worker.unavailability_list.contains(day) } }
  }

  fun filterWorkersByServices(
      workers: List<WorkerProfile>,
      selectedServices: List<String>
  ): List<WorkerProfile> {
    return workers.filter { worker -> selectedServices.all { service -> service in worker.tags } }
  }

  fun sortWorkersByRating(workers: List<WorkerProfile>): List<WorkerProfile> {
    return workers.sortedWith(
        compareByDescending<WorkerProfile> { it.rating.takeIf { !it.isNaN() } }
            .thenBy { it.rating.isNaN() })
  }

  fun filterWorkersByPriceRange(
      workers: List<WorkerProfile>,
      start: Int,
      end: Int
  ): List<WorkerProfile> {
    return workers.filter { worker -> worker.price in start.toDouble()..end.toDouble() }
  }

  fun filterWorkersByDistance(
      workers: List<WorkerProfile>,
      userLocation: Location,
      maxDistance: Int
  ): List<WorkerProfile> {
    return workers.filter { worker ->
      val distance =
          calculateDistance(
              userLocation.latitude,
              userLocation.longitude,
              worker.location!!.latitude,
              worker.location.longitude)
      distance <= maxDistance
    }
  }

  fun filterWorkersBySubcategory(fieldOfWork: String, onComplete: (() -> Unit)? = null) {
    _subCategoryWorkerProfiles.value = emptyList()

    workerProfileRepo.getProfiles(
        onSuccess = { profiles ->
          val workerProfiles = profiles.filterIsInstance<WorkerProfile>()
          val filteredProfiles = workerProfiles.filter { it.fieldOfWork == fieldOfWork }
          _subCategoryWorkerProfiles.value = filteredProfiles
          onComplete?.invoke()
        },
        onFailure = { Log.e("SearchViewModel", "Failed to fetch worker profiles.") })
  }

  fun emergencyFilter(
      workers: List<WorkerProfile>,
      location: Location,
      hour: Int = LocalTime.now().hour,
      minute: Int = LocalTime.now().minute
  ): List<WorkerProfile> {
    val availableWorkers =
        filterWorkersByAvailability(workers, listOf(LocalDate.now()), hour, minute)
    val sortedWorkers =
        availableWorkers.sortedBy { worker ->
          val workerLocation = worker.location
          if (workerLocation != null) {
            calculateDistance(
                location.latitude,
                location.longitude,
                workerLocation.latitude,
                workerLocation.longitude)
          } else {
            Double.MAX_VALUE // Place workers without a location at the end
          }
        }
    return sortedWorkers.take(3)
  }

  fun searchEngine(query: String, workerProfiles: List<WorkerProfile>): List<WorkerProfile> {
    val queryWords = query.split(" ").map { it.lowercase().trim() }

    val filteredProfiles =
        workerProfiles.filter { profile ->
          queryWords.all { word ->
            profile.fieldOfWork.lowercase().contains(word) ||
                profile.description.lowercase().contains(word) ||
                profile.displayName.lowercase().contains(word) ||
                profile.tags.any { it.lowercase().contains(word) } ||
                profile.includedServices.any { it.name.lowercase().contains(word) } ||
                profile.addOnServices.any { it.name.lowercase().contains(word) }
          }
        }

    return filteredProfiles.sortedByDescending { it.rating }
  }
}
