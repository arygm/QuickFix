package com.arygm.quickfix.model.offline.small

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val repository: PreferencesRepository
) : ViewModel() {

    private val _preferenceValue = MutableStateFlow<Any?>(null)
    val preferenceValue: StateFlow<Any?> get() = _preferenceValue

    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = PreferencesRepositoryDataStore(context)
                    return PreferencesViewModel(repository) as T
                }
            }
        }
    }

    fun <T> loadPreference(key: Preferences.Key<T>) {
        viewModelScope.launch {
            repository.getPreferenceByKey(key)
                .catch {
                    // Handle exceptions (e.g., log or show error message)
                    _preferenceValue.value = null
                }
                .collect { value ->
                    _preferenceValue.value = value
                }
        }
    }

    suspend fun <T> savePreference(key: Preferences.Key<T>, value: T) {
        repository.setPreferenceByKey(
            key,
            value,
        )
    }

    suspend fun clearAllPreferences() {
        repository.clearPreferences()
    }
}
