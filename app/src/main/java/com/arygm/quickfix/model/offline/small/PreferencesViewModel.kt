package com.arygm.quickfix.model.offline.small

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arygm.quickfix.utils.BIRTH_DATE_KEY
import com.arygm.quickfix.utils.EMAIL_KEY
import com.arygm.quickfix.utils.FIRST_NAME_KEY
import com.arygm.quickfix.utils.IS_SIGN_IN_KEY
import com.arygm.quickfix.utils.IS_WORKER_KEY
import com.arygm.quickfix.utils.LAST_NAME_KEY
import com.arygm.quickfix.utils.PROFILE_PICTURE_KEY
import com.arygm.quickfix.utils.UID_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class PreferencesViewModel(
    private val repositoryAccount: PreferencesRepository,
) : ViewModel() {

  private val _preferenceValue = MutableStateFlow<Any?>(null)
  val preferenceValue: StateFlow<Any?> = _preferenceValue.asStateFlow()

  val isWorkerFlow: Flow<Boolean> =
      repositoryAccount.getPreferenceByKey(IS_WORKER_KEY).map { it ?: false }

  val email: Flow<String> = repositoryAccount.getPreferenceByKey(EMAIL_KEY).map { it ?: "" }

  val firstName: Flow<String> =
      repositoryAccount.getPreferenceByKey(FIRST_NAME_KEY).map { it ?: "" }

  val lastName: Flow<String> = repositoryAccount.getPreferenceByKey(LAST_NAME_KEY).map { it ?: "" }

  val birthDate: Flow<String> =
      repositoryAccount.getPreferenceByKey(BIRTH_DATE_KEY).map { it ?: "" }
  val profilePicture: Flow<String> =
      repositoryAccount.getPreferenceByKey(PROFILE_PICTURE_KEY).map { it ?: "" }

  val isSignInKey: Flow<Boolean> =
      repositoryAccount.getPreferenceByKey(IS_SIGN_IN_KEY).map { it ?: false }

  val uidKey: Flow<String> = repositoryAccount.getPreferenceByKey(UID_KEY).map { it ?: "" }

  companion object {
    /** If you want two distinct DataStores, pass both in. */
    fun Factory(
        dataStore: DataStore<Preferences>,
    ): ViewModelProvider.Factory {
      return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          val repositoryAccount = PreferencesRepositoryDataStore(dataStore)
          return PreferencesViewModel(repositoryAccount) as T
        }
      }
    }
  }

  fun <T> loadPreference(key: Preferences.Key<T>, onLoaded: (T?) -> Unit) {
    viewModelScope.launch {
      repositoryAccount.getPreferenceByKey(key).collect { value -> onLoaded(value) }
    }
  }

  suspend fun <T> savePreference(key: Preferences.Key<T>, value: T) {
    repositoryAccount.setPreferenceByKey(key, value)
  }

  suspend fun clearAllPreferences() {
    repositoryAccount.clearPreferences()
  }
}
