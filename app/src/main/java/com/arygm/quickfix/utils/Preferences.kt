package com.arygm.quickfix.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

val IS_SIGN_IN_KEY = booleanPreferencesKey("is_sign_in")
val USER_ID_KEY = stringPreferencesKey("user_id")
val FIRST_NAME_KEY = stringPreferencesKey("first_name")
val LAST_NAME_KEY = stringPreferencesKey("last_name")
val EMAIL_KEY = stringPreferencesKey("email")
val DATE_OF_BIRTH_KEY = stringPreferencesKey("date_of_birth")