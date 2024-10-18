package com.arygm.quickfix.utils

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileViewModel
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
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit,
    profileViewModel: ProfileViewModel
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
          profileViewModel.fetchUserProfile(it.uid) { existingProfile ->
            if (existingProfile != null) {
              profileViewModel.setLoggedInProfile(existingProfile)
              onAuthComplete(authResult)
            } else {
              // Extract user information from Google account
              val firstName = account.givenName ?: ""
              val lastName = account.familyName ?: ""
              val email = account.email ?: ""
              val uid = user.uid

              // Create a new Profile object
              val profile =
                  Profile(
                      uid = uid,
                      firstName = firstName,
                      lastName = lastName,
                      email = email,
                      birthDate = Timestamp.now(),
                      description = "")

              // Save the profile to Firestore
              profileViewModel.addProfile(
                  profile,
                  onSuccess = {
                    profileViewModel.setLoggedInProfile(profile)
                    onAuthComplete(authResult)
                  },
                  onFailure = { exception ->
                    Log.e("Google SignIn", "Failed to save new profile", exception)
                  })
            }
          }
        } ?: run { Log.e("Google SignIn", "User Null After Sign In") }
      }
    } catch (e: ApiException) {
      onAuthError(e)
    }
  }
}

fun signInWithEmailAndFetchProfile(
    email: String,
    password: String,
    profileViewModel: ProfileViewModel,
    onResult: (Boolean) -> Unit
) {
  FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
      task ->
    if (task.isSuccessful) {
      val user = FirebaseAuth.getInstance().currentUser
      user?.let {
        profileViewModel.fetchUserProfile(
            it.uid,
            onResult = { profile ->
              if (profile != null) {
                profileViewModel.setLoggedInProfile(profile)
                onResult(true)
              } else {
                Log.e("Login Screen", "Error Logging in Profile.")
                onResult(false)
              }
            })
      }
          ?: run {
            Log.e("Login Screen", "Error Logging in Profile.")
            onResult(false)
          }
    } else {
      Log.e("Login Screen", "Error Logging in Profile.")
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
    profileViewModel: ProfileViewModel,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
  firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
    if (task.isSuccessful) {
      val user = FirebaseAuth.getInstance().currentUser
      user?.let {
        val profile =
            stringToTimestamp(birthDate)?.let { birthTimestamp ->
              Profile(
                  uid = it.uid,
                  firstName = firstName,
                  lastName = lastName,
                  email = email,
                  birthDate = birthTimestamp,
                  description = "")
            }

        profile?.let { createdProfile ->
          profileViewModel.addProfile(
              createdProfile,
              onSuccess = {
                profileViewModel.setLoggedInProfile(createdProfile)
                onSuccess()
              },
              onFailure = {
                Log.e("Registration", "Failed to save profile")
                onFailure()
              })
        }
            ?: run {
              Log.e("Registration", "Failed to create profile")
              onFailure()
            }
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
