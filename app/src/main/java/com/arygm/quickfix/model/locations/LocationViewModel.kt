package com.arygm.quickfix.model.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient

open class LocationViewModel(val repository: LocationRepository) : ViewModel() {
  // State to manage the search query
  private val query_ = MutableStateFlow("")
  val query: StateFlow<String> = query_.asStateFlow()

  // State for search results
  private val locations_ = MutableStateFlow<List<Location>>(emptyList())
  val locationSuggestions: StateFlow<List<Location>> = locations_.asStateFlow()

  // State for errors
  private val error_ = MutableStateFlow<Exception?>(null)
  val error: StateFlow<Exception?> = error_.asStateFlow()

  fun setQuery(query: String) {
    query_.value = query
    locations_.value = emptyList()
    error_.value = null

    repository.search(
        query,
        onSuccess = { results -> locations_.value = results },
        onFailure = { exception -> error_.value = exception })
  }

  fun setLocations(locations: List<Location>) {
    locations_.value = locations
  }

  // Create a factory for the ViewModel
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LocationViewModel(NominatimLocationRepository(OkHttpClient())) as T
          }
        }
  }
}
