package com.arygm.quickfix.model.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arygm.quickfix.utils.logOut
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

  private val users_ = MutableStateFlow<List<UserProfile>>(emptyList())
  val users: StateFlow<List<UserProfile>> = users_.asStateFlow()

  private val workers_ = MutableStateFlow<List<WorkerProfile>>(emptyList())
  val workers: StateFlow<List<WorkerProfile>> = workers_.asStateFlow()

  private val loggedInProfile_ = MutableStateFlow<Profile?>(null)
  open val loggedInProfile: StateFlow<Profile?> = loggedInProfile_.asStateFlow()

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(ProfileRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }

  init {
    repository.init {
      getProfiles(ProfileType.USER)
      getProfiles(ProfileType.WORKER)
    }
  }

  fun getProfiles(type: ProfileType) {
    repository.getProfiles(
        type,
        onSuccess = { profiles ->
          when (type) {
            ProfileType.USER -> users_.value = profiles.filterIsInstance<UserProfile>()
            ProfileType.WORKER -> workers_.value = profiles.filterIsInstance<WorkerProfile>()
          }
        },
        onFailure = { e -> Log.e("ProfileViewModel", "Failed to fetch profiles: ${e.message}") })
  }

  fun addProfile(
      type: ProfileType,
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.addProfile(
        type,
        profile = profile,
        onSuccess = {
          getProfiles(type)
          onSuccess()
        },
        onFailure = { e ->
          Log.e("ProfileViewModel", "Failed to add profile: ${e.message}")
          onFailure(e)
        })
  }

  fun updateProfile(type: ProfileType, profile: Profile) {
    repository.updateProfile(
        type,
        profile = profile,
        onSuccess = {
          getProfiles(type)
          fetchUserProfile(type, profile.uid) { setLoggedInProfile(profile) }
        },
        onFailure = { e -> Log.e("ProfileViewModel", "Failed to update profile: ${e.message}") })
  }

  fun deleteProfileById(type: ProfileType, id: String) {
    repository.deleteProfileById(
        type,
        id = id,
        onSuccess = { getProfiles(type) },
        onFailure = { e -> Log.e("ProfileViewModel", "Failed to delete profile: ${e.message}") })
  }

  fun profileExists(type: ProfileType, email: String, onResult: (Boolean, Profile?) -> Unit) {
    repository.profileExists(
        type,
        email,
        onSuccess = { (exists, profile) ->
          if (exists) {
            Log.d("ProfileCheck", "Profile with this email exists.")
            onResult(true, profile)
          } else {
            Log.d("ProfileCheck", "No profile found with this email.")
            onResult(false, null)
          }
        },
        onFailure = { exception ->
          Log.e("ProfileCheck", "Error checking profile existence", exception)
          onResult(false, null)
        })
  }

  fun setLoggedInProfile(profile: Profile) {
    loggedInProfile_.value = profile
  }

  fun fetchUserProfile(type: ProfileType, uid: String, onResult: (Profile?) -> Unit) {
    repository.getProfileById(
        type,
        uid,
        onSuccess = { profile ->
          if (profile != null) {
            onResult(profile)
          } else {
            Log.e("ProfileViewModel", "No profile found for user with UID: $uid")
            onResult(null)
          }
        },
        onFailure = { e ->
          Log.e("ProfileViewModel", "Error fetching profile: ${e.message}")
          onResult(null)
        })
  }

  fun logOut(firebasAuth: FirebaseAuth) {
    loggedInProfile_.value = null
    com.arygm.quickfix.utils.logOut(firebasAuth)
  }
}
