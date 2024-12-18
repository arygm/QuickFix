package com.arygm.quickfix.utils

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModelUserProfile
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthCompleteOne: (AuthResult) -> Unit,
    onAuthCompleteTwo: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit,
    accountViewModel: AccountViewModel,
    userViewModel: ProfileViewModel,
    preferencesViewModel: PreferencesViewModel,
    userPreferencesViewModel: PreferencesViewModelUserProfile
): ManagedActivityResultLauncher<Intent, ActivityResult> {
  val scope = rememberCoroutineScope()

  return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      result ->
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    try {
      val account = task.getResult(ApiException::class.java)!!
      val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
      scope.launch {
        val authResult = Firebase.auth.signInWithCredential(credential).await()
        val user = Firebase.auth.currentUser

        user?.let {
          accountViewModel.fetchUserAccount(it.uid) { existingAccount ->
            if (existingAccount != null) {
              setAccountPreferences(preferencesViewModel, existingAccount)
              userViewModel.fetchUserProfile(it.uid) { userProfile ->
                val profileFetched = userProfile as UserProfile
                setUserProfilePreferences(userPreferencesViewModel, profileFetched)
              }
              onAuthCompleteOne(authResult)
            } else {
              // Extract user information from Google account
              val firstName = account.givenName ?: ""
              val lastName = account.familyName ?: ""
              val email = account.email ?: ""
              val uid = user.uid

              // Create a new Account object
              val newAccount =
                  Account(
                      uid = uid,
                      firstName = firstName,
                      lastName = lastName,
                      email = email,
                      birthDate = Timestamp.now())
              setAccountPreferences(preferencesViewModel, newAccount, false)
              val defaultLocation = Location(0.0, 0.0, "defaultLocation")
              val defaultUserProfile =
                  UserProfile(
                      uid = it.uid,
                      locations = listOf(defaultLocation),
                      announcements = emptyList())
              setUserProfilePreferences(userPreferencesViewModel, defaultUserProfile)
              userViewModel.addProfile(
                  defaultUserProfile,
                  onSuccess = {
                    accountViewModel.addAccount(
                        newAccount,
                        onSuccess = { onAuthCompleteTwo(authResult) },
                        onFailure = { Log.e("Registration", "Failed to save account") })
                  },
                  onFailure = { Log.e("Registration", "Failed to create User Profile") })
            }
          }
        } ?: run { Log.e("Google SignIn", "User Null After Sign In") }
      }
    } catch (e: ApiException) {
      onAuthError(e)
    }
  }
}

fun signInWithEmailAndFetchAccount(
    email: String,
    password: String,
    accountViewModel: AccountViewModel,
    preferencesViewModel: PreferencesViewModel,
    onResult: (Boolean) -> Unit,
    userPreferencesViewModel: PreferencesViewModelUserProfile,
    userViewModel: ProfileViewModel
) {
  FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
      task ->
    if (task.isSuccessful) {
      val user = FirebaseAuth.getInstance().currentUser
      user?.let {
        accountViewModel.fetchUserAccount(
            it.uid,
            onResult = { account ->
              if (account != null) {
                setAccountPreferences(preferencesViewModel, account)
                userViewModel.fetchUserProfile(it.uid) { userProfile ->
                  val profileFetched = userProfile as UserProfile
                  setUserProfilePreferences(userPreferencesViewModel, profileFetched)
                }
                onResult(true)
              } else {
                Log.e("Login Screen", "Error Logging in Account.")
                onResult(false)
              }
            })
      }
          ?: run {
            Log.e("Login Screen", "Error Logging in Account.")
            onResult(false)
          }
    } else {
      Log.e("Login Screen", "Error Logging in Account.")
      onResult(false)
    }
  }
}

fun createAccountWithEmailAndPassword(
    firebaseAuth: FirebaseAuth,
    firstName: String,
    lastName: String,
    email: String,
    password: String,
    birthDate: String,
    accountViewModel: AccountViewModel,
    userViewModel: ProfileViewModel,
    preferencesViewModel: PreferencesViewModel,
    userPreferencesViewModel: PreferencesViewModelUserProfile,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
  firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
    if (task.isSuccessful) {
      val user = FirebaseAuth.getInstance().currentUser
      user?.let {
        val account =
            stringToTimestamp(birthDate)?.let { birthTimestamp ->
              Account(
                  uid = it.uid,
                  firstName = firstName,
                  lastName = lastName,
                  email = email,
                  birthDate = birthTimestamp)
            }
        if (account == null) {
          Log.e("Registration", "Invalid BirthDate.")
          onFailure()
        }
        val defaultLocation = Location(0.0, 0.0, "defaultLocation")
        val defaultUserProfile =
            UserProfile(
                uid = it.uid, locations = listOf(defaultLocation), announcements = emptyList())
        userViewModel.addProfile(
            defaultUserProfile,
            onSuccess = {
              account?.let { createdAccount ->
                accountViewModel.addAccount(
                    createdAccount,
                    onSuccess = {
                      setAccountPreferences(preferencesViewModel, createdAccount)
                      setUserProfilePreferences(userPreferencesViewModel, defaultUserProfile)
                      onSuccess()
                    },
                    onFailure = {
                      Log.e("Registration", "Failed to save account")
                      onFailure()
                    })
              }
            },
            onFailure = {
              Log.e("Registration", "Failed to create User Profile")
              onFailure()
            })
      }
          ?: run {
            Log.e("Registration", "Failed to create account")
            onFailure()
          }
    } else {
      Log.e("Registration", "Error creating account: ${task.exception?.message}")
      onFailure()
    }
  }
}

fun logOut(firebaseAuth: FirebaseAuth) {
  firebaseAuth.signOut()
}
