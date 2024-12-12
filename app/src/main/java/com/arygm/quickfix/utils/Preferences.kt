package com.arygm.quickfix.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
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
// =====App Mode Preferences=====//
val APP_MODE_KEY = stringPreferencesKey("app_mode")

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

fun clearAccountPreferences(
    preferencesViewModel: PreferencesViewModel,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch { preferencesViewModel.clearAllPreferences() }
}

// Setter functions
fun setSignIn(
    preferencesViewModel: PreferencesViewModel,
    signIn: Boolean,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch { preferencesViewModel.savePreference(IS_SIGN_IN_KEY, signIn) }
}

fun setUserId(
    preferencesViewModel: PreferencesViewModel,
    userId: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch { preferencesViewModel.savePreference(UID_KEY, userId) }
}

fun setFirstName(
    preferencesViewModel: PreferencesViewModel,
    firstName: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.savePreference(FIRST_NAME_KEY, firstName)
  }
}

fun setLastName(
    preferencesViewModel: PreferencesViewModel,
    lastName: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch { preferencesViewModel.savePreference(LAST_NAME_KEY, lastName) }
}

fun setEmail(
    preferencesViewModel: PreferencesViewModel,
    email: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch { preferencesViewModel.savePreference(EMAIL_KEY, email) }
}

fun setBirthDate(
    preferencesViewModel: PreferencesViewModel,
    dateOfBirth: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.savePreference(BIRTH_DATE_KEY, dateOfBirth)
  }
}

fun setAppMode(
    preferencesViewModel: PreferencesViewModel,
    appMode: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch { preferencesViewModel.savePreference(APP_MODE_KEY, appMode) }
}

fun setIsWorker(
    preferencesViewModel: PreferencesViewModel,
    isWorker: Boolean,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch { preferencesViewModel.savePreference(IS_WORKER_KEY, isWorker) }
}

// Loader functions
suspend fun loadIsSignIn(preferencesViewModel: PreferencesViewModel): Boolean {
  return suspendCoroutine { cont ->
    var resumed = false
    preferencesViewModel.loadPreference(IS_SIGN_IN_KEY) { value ->
      if (!resumed) {
        resumed = true
        cont.resume(value ?: false)
      }
    }
  }
}

suspend fun loadUserId(preferencesViewModel: PreferencesViewModel): String {
  return suspendCoroutine { cont ->
    var resumed = false
    preferencesViewModel.loadPreference(UID_KEY) { value ->
      if (!resumed) {
        resumed = true

        cont.resume(value ?: "no_first_name")
      }
    }
  }
}

suspend fun loadFirstName(preferencesViewModel: PreferencesViewModel): String {
  return suspendCoroutine { cont ->
    var resumed = false
    preferencesViewModel.loadPreference(FIRST_NAME_KEY) { value ->
      if (!resumed) {
        resumed = true
        cont.resume(value ?: "no_first_name")
      }
    }
  }
}

suspend fun loadLastName(preferencesViewModel: PreferencesViewModel): String {
  return suspendCoroutine { cont ->
    var resumed = false
    preferencesViewModel.loadPreference(LAST_NAME_KEY) { value ->
      if (!resumed) {
        resumed = true
        cont.resume(value ?: "no_last_name")
      }
    }
  }
}

suspend fun loadEmail(preferencesViewModel: PreferencesViewModel): String {
  return suspendCoroutine { cont ->
    var resumed = false
    preferencesViewModel.loadPreference(EMAIL_KEY) { value ->
      if (!resumed) {
        resumed = true
        cont.resume(value ?: "no_email")
      }
    }
  }
}

suspend fun loadBirthDate(preferencesViewModel: PreferencesViewModel): String {
  return suspendCoroutine { cont ->
    var resumed = false
    preferencesViewModel.loadPreference(BIRTH_DATE_KEY) { value ->
      if (!resumed) {
        resumed = true
        cont.resume(value ?: "no_birth_date")
      }
    }
  }
}

suspend fun loadAppMode(preferencesViewModel: PreferencesViewModel): String {
  return suspendCoroutine { cont ->
    var resumed = false
    preferencesViewModel.loadPreference(APP_MODE_KEY) { value ->
      if (!resumed) {
        resumed = true
        cont.resume(value ?: "User")
      }
    }
  }
}

suspend fun loadIsWorker(preferencesViewModel: PreferencesViewModel): Boolean {
  return suspendCoroutine { cont ->
    var resumed = false
    preferencesViewModel.loadPreference(IS_WORKER_KEY) { value ->
      if (!resumed) {
        resumed = true
        cont.resume(value ?: false)
      }
    }
  }
}
