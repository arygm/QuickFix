package com.arygm.quickfix.model.offline.small

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

open class PreferencesRepositoryDataStore(private val dataStore: DataStore<Preferences>) :
    PreferencesRepository {
  override fun <T> getPreferenceByKey(key: Preferences.Key<T>): Flow<T?> {
    return dataStore.data.map { preferences -> preferences[key] }
  }

  override suspend fun <T> setPreferenceByKey(key: Preferences.Key<T>, value: T) {
    dataStore.edit { preferences -> preferences[key] = value }
  }

  override suspend fun clearPreferences() {
    dataStore.edit { preferences -> preferences.clear() }
  }
}
