package com.arygm.quickfix.model.offline.small

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
  // Get a preference by key
  fun <T> getPreferenceByKey(key: Preferences.Key<T>): Flow<T?>

  // Set a preference by key
  suspend fun <T> setPreferenceByKey(key: Preferences.Key<T>, value: T)

  // Clear all preferences
  suspend fun clearPreferences()
}
