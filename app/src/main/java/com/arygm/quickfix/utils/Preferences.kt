package com.arygm.quickfix.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// =====Account Preferences=====//
val IS_SIGN_IN_KEY = booleanPreferencesKey("is_sign_in")
val USER_ID_KEY = stringPreferencesKey("user_id")
val FIRST_NAME_KEY = stringPreferencesKey("first_name")
val LAST_NAME_KEY = stringPreferencesKey("last_name")
val EMAIL_KEY = stringPreferencesKey("email")
val DATE_OF_BIRTH_KEY = stringPreferencesKey("date_of_birth")
val IS_WORKER_KEY = booleanPreferencesKey("is_worker")

fun setAccountPreferences(
    preferencesViewModel: PreferencesViewModel,
    account: Account,
    signIn: Boolean = true,
) {
  CoroutineScope(Dispatchers.IO).launch {
    preferencesViewModel.savePreference(IS_SIGN_IN_KEY, signIn)
    preferencesViewModel.savePreference(USER_ID_KEY, account.uid)
    preferencesViewModel.savePreference(FIRST_NAME_KEY, account.firstName)
    preferencesViewModel.savePreference(LAST_NAME_KEY, account.lastName)
    preferencesViewModel.savePreference(EMAIL_KEY, account.email)
    preferencesViewModel.savePreference(DATE_OF_BIRTH_KEY, timestampToString(account.birthDate))
    preferencesViewModel.savePreference(IS_WORKER_KEY, account.isWorker)
  }
}
