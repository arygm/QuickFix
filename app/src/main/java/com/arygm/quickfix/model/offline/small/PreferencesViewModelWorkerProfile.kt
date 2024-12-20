package com.arygm.quickfix.model.offline.small

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arygm.quickfix.utils.WALLET_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class PreferencesViewModelWorkerProfile(
    private val repositoryUserProfile: PreferencesRepository,
) : ViewModel() {

  val wallet: Flow<Double> = repositoryUserProfile.getPreferenceByKey(WALLET_KEY).map { it ?: 0.0 }

  companion object {
    /** If you want two distinct DataStores, pass both in. */
    fun Factory(
        dataStore: DataStore<Preferences>,
    ): ViewModelProvider.Factory {
      return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          val repositoryUserProfile = PreferencesRepositoryDataStore(dataStore)
          return PreferencesViewModelWorkerProfile(repositoryUserProfile) as T
        }
      }
    }
  }

  fun <T> loadPreference(key: Preferences.Key<T>, onLoaded: (T?) -> Unit) {
    viewModelScope.launch {
      repositoryUserProfile.getPreferenceByKey(key).collect { value -> onLoaded(value) }
    }
  }

  suspend fun <T> savePreference(key: Preferences.Key<T>, value: T) {
    repositoryUserProfile.setPreferenceByKey(key, value)
  }

  suspend fun clearAllPreferences() {
    repositoryUserProfile.clearPreferences()
  }
}
