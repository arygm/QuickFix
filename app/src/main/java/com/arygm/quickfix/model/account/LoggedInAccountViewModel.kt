package com.arygm.quickfix.model.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Deprecated("Replaced by PreferencesViewModel")
class LoggedInAccountViewModel(
    private val userProfileRepo: ProfileRepository,
    private val workerProfileRepo: ProfileRepository
) : ViewModel() {

  val loggedInAccount_ = MutableStateFlow<Account?>(null)
  val loggedInAccount: StateFlow<Account?> = loggedInAccount_.asStateFlow()
  val userProfile_ = MutableStateFlow<UserProfile?>(null)
  val userProfile: StateFlow<UserProfile?> = userProfile_.asStateFlow()
  val workerProfile_ = MutableStateFlow<WorkerProfile?>(null)
  val workerProfile: StateFlow<WorkerProfile?> = workerProfile_.asStateFlow()

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoggedInAccountViewModel(
                UserProfileRepositoryFirestore(Firebase.firestore, Firebase.storage),
                WorkerProfileRepositoryFirestore(Firebase.firestore, Firebase.storage))
                as T
          }
        }
  }

  fun setLoggedInAccount(account: Account) {
    loggedInAccount_.value = account
    userProfileRepo.getProfileById(
        account.uid,
        onSuccess = { userProfile_.value = it as UserProfile? },
        onFailure = { e -> println("Failed to fetch user profile: ${e.message}") })
    if (account.isWorker) {
      workerProfileRepo.getProfileById(
          account.uid,
          onSuccess = { workerProfile_.value = it as WorkerProfile? },
          onFailure = { e -> println("Failed to fetch worker profile: ${e.message}") })
    }
  }

  fun logOut(firebaseAuth: FirebaseAuth) {
    loggedInAccount_.value = null
    userProfile_.value = null
    workerProfile_.value = null
    com.arygm.quickfix.utils.logOut(firebaseAuth)
  }
}
