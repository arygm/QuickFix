package com.arygm.quickfix.ui.profile

import androidx.datastore.preferences.core.Preferences
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A fake implementation of PreferencesRepository for testing purposes. It allows setting and
 * retrieving preferences using both String keys and Preferences.Key<T> keys.
 */
class FakePreferencesRepository : PreferencesRepository {
  // Internal storage mapping keys to their corresponding MutableStateFlow
  private val preferences = mutableMapOf<Any, MutableStateFlow<Any?>>()

  /**
   * Retrieves a Flow of the preference value associated with the given Preferences.Key<T> key. If
   * the key doesn't exist, it emits null by default.
   */
  override fun <T> getPreferenceByKey(key: Preferences.Key<T>): Flow<T?> {
    @Suppress("UNCHECKED_CAST")
    return preferences.getOrPut(key) { MutableStateFlow(null) } as Flow<T?>
  }

  /**
   * Sets the preference value for the given String key. If the key doesn't exist, it creates a new
   * MutableStateFlow.
   */
  fun setPreference(key: String, value: Any?) {
    preferences.getOrPut(key) { MutableStateFlow(null) }.value = value
  }

  /**
   * Sets the preference value for the given Preferences.Key<T> key. If the key doesn't exist, it
   * creates a new MutableStateFlow.
   */
  suspend fun <T> setPreference(key: Preferences.Key<T>, value: T) {
    preferences.getOrPut(key) { MutableStateFlow(null) }.value = value
  }

  /**
   * Sets the preference value for the given Preferences.Key<T> key. This method is part of the
   * PreferencesRepository interface.
   */
  override suspend fun <T> setPreferenceByKey(key: Preferences.Key<T>, value: T) {
    preferences.getOrPut(key) { MutableStateFlow(null) }.value = value
  }

  /**
   * Clears all preferences by setting each value to null. This method is part of the
   * PreferencesRepository interface.
   */
  override suspend fun clearPreferences() {
    preferences.values.forEach { it.value = null }
  }
}
