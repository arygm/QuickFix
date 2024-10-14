package com.arygm.quickfix.model.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

  private val profiles_ = MutableStateFlow<List<Profile>>(emptyList())
  val profiles: StateFlow<List<Profile>> = profiles_.asStateFlow()

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
    repository.init { getProfiles() }
  }

  fun getNewUid(): String {
    return repository.getNewUid()
  }

  private fun getProfiles() {
    repository.getProfiles(
        onSuccess = { profiles_.value = it },
        onFailure = { e -> Log.e("ProfileViewModel", "Failed to fetch profiles: ${e.message}") })
  }

  fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    repository.addProfile(
        profile = profile,
        onSuccess = {
          getProfiles()
          onSuccess()
        },
        onFailure = { e ->
          Log.e("ProfileViewModel", "Failed to add profile: ${e.message}")
          onFailure(e)
        })
  }

  fun updateProfile(profile: Profile) {
    repository.updateProfile(
        profile = profile,
        onSuccess = { getProfiles() },
        onFailure = { e -> Log.e("ProfileViewModel", "Failed to update profile: ${e.message}") })
  }

  fun deleteProfileById(id: String) {
    repository.deleteProfileById(
        id = id,
        onSuccess = { getProfiles() },
        onFailure = { e -> Log.e("ProfileViewModel", "Failed to delete profile: ${e.message}") })
  }

  fun profileExists(email: String, onResult: (Boolean, Profile?) -> Unit) {
    repository.profileExists(
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

  fun fetchUserProfile(uid: String, onResult: (Profile?) -> Unit) {
    repository.getProfileById(
        uid,
        onSuccess = { profile ->
          if (profile != null) {
            onResult(profile)
          } else {
            Log.e("ProfileViewModel", "No profile found for user with UID: $uid")
            onResult(null)
          }
        },
        onFailure = { e -> Log.e("ProfileViewModel", "Error fetching profile: ${e.message}") })
  }
}
