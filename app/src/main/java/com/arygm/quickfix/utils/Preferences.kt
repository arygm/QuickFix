package com.arygm.quickfix.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModelUserProfile
import com.arygm.quickfix.model.offline.small.PreferencesViewModelWorkerProfile
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.WorkerProfile
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
val PROFILE_PICTURE_KEY = stringPreferencesKey("profile_picture")
val EMAIL_KEY = stringPreferencesKey("email")
val BIRTH_DATE_KEY = stringPreferencesKey("date_of_birth")
val IS_WORKER_KEY = booleanPreferencesKey("is_worker")
// =====App Mode Preferences=====//
val APP_MODE_KEY = stringPreferencesKey("app_mode")
// =====Profile Preferences=====//
val WALLET_KEY = doublePreferencesKey("wallet")
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
    preferencesViewModel.savePreference(PROFILE_PICTURE_KEY, account.profilePicture)
  }
}

fun setUserProfilePreferences(
    preferencesViewModel: PreferencesViewModelUserProfile,
    userProfile: UserProfile,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.savePreference(WALLET_KEY, userProfile.wallet)
  }
}

fun setWorkerProfilePreferences(
    preferencesViewModel: PreferencesViewModelWorkerProfile,
    workerProfile: WorkerProfile,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.savePreference(WALLET_KEY, workerProfile.wallet)
  }
}

fun clearPreferences(
    preferencesViewModel: PreferencesViewModel,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch { preferencesViewModel.clearAllPreferences() }
}

fun clearUserProfilePreferences(
    preferencesViewModel: PreferencesViewModelUserProfile,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch { preferencesViewModel.clearAllPreferences() }
}

fun clearWorkerProfilePreferences(
    preferencesViewModel: PreferencesViewModelWorkerProfile,
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

fun setProfilePicture(
    preferencesViewModel: PreferencesViewModel,
    profilePicture: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
  CoroutineScope(dispatcher).launch {
    preferencesViewModel.savePreference(PROFILE_PICTURE_KEY, profilePicture)
  }
}

suspend fun loadWallet(preferencesViewModel: PreferencesViewModel): Double {
  return suspendCoroutine { cont ->
    var resumed = false
    preferencesViewModel.loadPreference(WALLET_KEY) { value ->
      if (!resumed) {
        resumed = true
        cont.resume(value ?: 0.0)
      }
    }
  }
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

suspend fun loadProfilePicture(preferencesViewModel: PreferencesViewModel): String {
  return suspendCoroutine { cont ->
    var resumed = false
    preferencesViewModel.loadPreference(PROFILE_PICTURE_KEY) { value ->
      if (!resumed) {
        resumed = true
        cont.resume(value ?: "no_profile_picture")
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
        cont.resume(value ?: "USER")
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
