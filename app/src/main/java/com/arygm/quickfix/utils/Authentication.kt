package com.arygm.quickfix.utils

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.arygm.quickfix.model.profile.LoggedInProfileViewModel
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
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit,
    userViewModel: ProfileViewModel,
    loggedInProfileViewModel: LoggedInProfileViewModel
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
          userViewModel.fetchUserProfile(it.uid) { existingProfile ->
            if (existingProfile != null) {
              if (existingProfile is UserProfile) {
                Log.d("HELLLOO", "ALOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO")
                loggedInProfileViewModel.setLoggedInProfile(existingProfile)
              }
              onAuthComplete(authResult)
            } else {
              // Extract user information from Google account
              val firstName = account.givenName ?: ""
              val lastName = account.familyName ?: ""
              val email = account.email ?: ""
              val uid = user.uid

              // Create a new Profile object
              val profile =
                  UserProfile(
                      uid = uid,
                      firstName = firstName,
                      lastName = lastName,
                      email = email,
                      birthDate = Timestamp.now())

              // Save the profile to Firestore
              userViewModel.addProfile(
                  profile,
                  onSuccess = {
                    loggedInProfileViewModel.setLoggedInProfile(profile)
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
    userViewModel: ProfileViewModel,
    loggedInProfileViewModel: LoggedInProfileViewModel,
    onResult: (Boolean) -> Unit
) {
  FirebaseAuth.getInstance()
      .signInWithEmailAndPassword(email, password)
      .addOnCompleteListener { // it fails here since the task is not successful for the CI i don't
          // know why but it works on my local machine
          task ->
        if (task.isSuccessful) {
          val user = FirebaseAuth.getInstance().currentUser
          user?.let {
            userViewModel.fetchUserProfile(
                it.uid,
                onResult = { profile ->
                  if (profile != null) {
                    if (profile is UserProfile) {
                      loggedInProfileViewModel.setLoggedInProfile(profile)
                    }
                    onResult(true)
                  } else {
                    Log.e("Login Screen", "Error Logging in Profile. profile is null")
                    onResult(false)
                  }
                })
          }
              ?: run {
                Log.e("Login Screen", "Error Logging in Profile. user is null")
                onResult(false)
              }
        } else {
          Log.e("Login Screen", "Error Logging in Profile. Exception: ${task.exception}")
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
    userViewModel: ProfileViewModel,
    loggedInProfileViewModel: LoggedInProfileViewModel,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
  firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
    if (task.isSuccessful) {
      val user = FirebaseAuth.getInstance().currentUser
      user?.let {
        val profile =
            stringToTimestamp(birthDate)?.let { birthTimestamp ->
              UserProfile(
                  uid = it.uid,
                  firstName = firstName,
                  lastName = lastName,
                  email = email,
                  birthDate = birthTimestamp,
                  location = GeoPoint(0.0, 0.0),
                  isWorker = false)
            }

        profile?.let { createdProfile ->
          userViewModel.addProfile(
              createdProfile,
              onSuccess = {
                loggedInProfileViewModel.setLoggedInProfile(createdProfile)
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
