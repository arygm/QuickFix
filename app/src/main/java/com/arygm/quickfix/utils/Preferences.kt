package com.arygm.quickfix.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// =====Account Preferences=====//
val IS_SIGN_IN_KEY = booleanPreferencesKey("is_sign_in")
val UID_KEY = stringPreferencesKey("user_id")
val FIRST_NAME_KEY = stringPreferencesKey("first_name")
val LAST_NAME_KEY = stringPreferencesKey("last_name")
val EMAIL_KEY = stringPreferencesKey("email")
val BIRTH_DATE_KEY = stringPreferencesKey("date_of_birth")
val IS_WORKER_KEY = booleanPreferencesKey("is_worker")

// =====Helper functions======//
fun setAccountPreferences(
    preferencesViewModel: PreferencesViewModel,
    account: Account,
    signIn: Boolean = true,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.savePreference(IS_SIGN_IN_KEY, signIn)
    preferencesViewModel.savePreference(UID_KEY, account.uid)
    preferencesViewModel.savePreference(FIRST_NAME_KEY, account.firstName)
    preferencesViewModel.savePreference(LAST_NAME_KEY, account.lastName)
    preferencesViewModel.savePreference(EMAIL_KEY, account.email)
    preferencesViewModel.savePreference(BIRTH_DATE_KEY, timestampToString(account.birthDate))
    preferencesViewModel.savePreference(IS_WORKER_KEY, account.isWorker)
  }
}

fun clearAccountPreferences(preferencesViewModel: PreferencesViewModel, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.clearAllPreferences()
  }
}

// Setter functions
fun setSignIn(preferencesViewModel: PreferencesViewModel, signIn: Boolean, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.savePreference(IS_SIGN_IN_KEY, signIn)
  }
}

fun setUserId(preferencesViewModel: PreferencesViewModel, userId: String, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.savePreference(UID_KEY, userId)
  }
}

fun setFirstName(preferencesViewModel: PreferencesViewModel, firstName: String, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.savePreference(FIRST_NAME_KEY, firstName)
  }
}

fun setLastName(preferencesViewModel: PreferencesViewModel, lastName: String, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.savePreference(LAST_NAME_KEY, lastName)
  }
}

fun setEmail(preferencesViewModel: PreferencesViewModel, email: String, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.savePreference(EMAIL_KEY, email)
  }
}

fun setDateOfBirth(preferencesViewModel: PreferencesViewModel, dateOfBirth: String, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.savePreference(BIRTH_DATE_KEY, dateOfBirth)
  }
}

fun setIsWorker(preferencesViewModel: PreferencesViewModel, isWorker: Boolean, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.savePreference(IS_WORKER_KEY, isWorker)
  }
}

// Loader functions
fun loadIsSignIn(preferencesViewModel: PreferencesViewModel, onLoaded: (Boolean?) -> Unit, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.loadPreference(IS_SIGN_IN_KEY, onLoaded)
  }
}

fun loadUserId(preferencesViewModel: PreferencesViewModel, onLoaded: (String?) -> Unit, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.loadPreference(UID_KEY, onLoaded)
  }
}

fun loadFirstName(preferencesViewModel: PreferencesViewModel, onLoaded: (String?) -> Unit, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.loadPreference(FIRST_NAME_KEY, onLoaded)
  }
}

fun loadLastName(preferencesViewModel: PreferencesViewModel, onLoaded: (String?) -> Unit, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.loadPreference(LAST_NAME_KEY, onLoaded)
  }
}

fun loadEmail(preferencesViewModel: PreferencesViewModel, onLoaded: (String?) -> Unit, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.loadPreference(EMAIL_KEY, onLoaded)
  }
}

fun loadDateOfBirth(preferencesViewModel: PreferencesViewModel, onLoaded: (String?) -> Unit, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.loadPreference(BIRTH_DATE_KEY, onLoaded)
  }
}

fun loadIsWorker(preferencesViewModel: PreferencesViewModel, onLoaded: (Boolean?) -> Unit, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.loadPreference(IS_WORKER_KEY, onLoaded)
  }
}
