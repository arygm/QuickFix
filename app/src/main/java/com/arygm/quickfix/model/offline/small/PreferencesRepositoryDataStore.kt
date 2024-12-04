package com.arygm.quickfix.model.offline.small

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepositoryDataStore(private val context: Context) : PreferencesRepository {
    private val Context.dataStore by preferencesDataStore(name = "quickfix_preferences")


    override fun <T> getPreferenceByKey(key: Preferences.Key<T>): Flow<T?> {
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    override suspend fun <T> setPreferenceByKey(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override suspend fun clearPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
