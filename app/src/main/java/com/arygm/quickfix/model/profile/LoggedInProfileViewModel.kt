package com.arygm.quickfix.model.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoggedInProfileViewModel : ViewModel() {

  private val loggedInProfile_ = MutableStateFlow<Profile?>(null)
  val loggedInProfile: StateFlow<Profile?> = loggedInProfile_.asStateFlow()

  fun setLoggedInProfile(profile: Profile) {
    loggedInProfile_.value = profile
  }

  fun logOut(firebasAuth: FirebaseAuth) {
    loggedInProfile_.value = null
    com.arygm.quickfix.utils.logOut(firebasAuth)
  }
}
