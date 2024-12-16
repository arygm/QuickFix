package com.arygm.quickfix.model.profile

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

  private val profiles_ = MutableStateFlow<List<Profile>>(emptyList())
  val profiles: StateFlow<List<Profile>> = profiles_.asStateFlow()

  companion object {
    val UserFactory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(
                UserProfileRepositoryFirestore(Firebase.firestore, Firebase.storage))
                as T
          }
        }

    val WorkerFactory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(
                WorkerProfileRepositoryFirestore(Firebase.firestore, Firebase.storage))
                as T
          }
        }
  }

  init {
    repository.init { getProfiles() }
  }

  fun getProfiles() {
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

  fun updateProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    repository.updateProfile(
        profile = profile,
        onSuccess = {
          getProfiles()

          onSuccess()
          // fetchUserProfile(profile.uid) { loggedInProfileViewModel.setLoggedInProfile(profile) }
        },
        onFailure = { e ->
          Log.e("ProfileViewModel", "Failed to update profile: ${e.message}")
          onFailure(e)
        })
  }

  fun deleteProfileById(id: String) {
    repository.deleteProfileById(
        id = id,
        onSuccess = { getProfiles() },
        onFailure = { e -> Log.e("ProfileViewModel", "Failed to delete profile: ${e.message}") })
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
        onFailure = { e ->
          Log.e("ProfileViewModel", "Error fetching profile: ${e.message}")
          onResult(null)
        })
  }

  fun uploadProfileImages(
      accountId: String,
      images: List<Bitmap>, // List of image file paths as strings
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    Log.d("UploadingImages", "$images.size")
    repository.uploadProfileImages(
        accountId = accountId,
        images = images,
        onSuccess = { onSuccess(it) },
        onFailure = { e -> onFailure(e) })
  }

  fun fetchProfileImageAsBitmap(
      accountId: String,
      onSuccess: (Bitmap) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.fetchProfileImageAsBitmap(accountId, onSuccess, onFailure)
  }

  fun fetchBannerImageAsBitmap(
      accountId: String,
      onSuccess: (Bitmap) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.fetchBannerImageAsBitmap(accountId, onSuccess, onFailure)
  }
}
