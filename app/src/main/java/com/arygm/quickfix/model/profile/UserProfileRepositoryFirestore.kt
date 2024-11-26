package com.arygm.quickfix.model.profile

import android.util.Log
import com.arygm.quickfix.model.locations.Location
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileRepositoryFirestore(private val db: FirebaseFirestore) : ProfileRepository {

  private val collectionPath = "users"

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun getProfiles(onSuccess: (List<Profile>) -> Unit, onFailure: (Exception) -> Unit) {
    Log.d("UserProfileRepositoryFirestore", "getProfiles")
    db.collection(collectionPath).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val profiles =
            task.result?.documents?.mapNotNull { document -> documentToUser(document) }
                ?: emptyList()
        onSuccess(profiles)
      } else {
        task.exception?.let { e ->
          Log.e("UserProfileRepositoryFirestore", "Error getting documents", e)
          onFailure(e)
        }
      }
    }
  }

  override fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(
        db.collection(collectionPath).document(profile.uid).set(profile), onSuccess, onFailure)
  }

  override fun updateProfile(
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(profile.uid).set(profile), onSuccess, onFailure)
  }

  override fun deleteProfileById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(id).delete(), onSuccess, onFailure)
  }

  private fun performFirestoreOperation(
      task: Task<Void>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    task.addOnCompleteListener { result ->
      if (result.isSuccessful) {
        onSuccess()
      } else {
        result.exception?.let { e ->
          Log.e("UserProfileRepositoryFirestore", "Error performing Firestore operation", e)
          onFailure(e)
        }
      }
    }
  }

  private fun documentToUser(document: DocumentSnapshot): UserProfile? {
    return try {
      val uid = document.id
      val locationsData = document.get("locations") as? List<Map<String, Any>> ?: emptyList()
      val locations =
          locationsData.map { map ->
            Location(
                latitude = map["latitude"] as? Double ?: 0.0,
                longitude = map["longitude"] as? Double ?: 0.0,
                name = map["name"] as? String ?: "")
          }
      val announcements = document.get("announcements") as? List<String> ?: emptyList()
      UserProfile(uid = uid, locations = locations, announcements = announcements)
    } catch (e: Exception) {
      Log.e("UserProfileRepositoryFirestore", "Error converting document to UserProfile", e)
      null
    }
  }

  override fun getProfileById(
      uid: String,
      onSuccess: (Profile?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
          if (document.exists()) {
            val profile = documentToUser(document)
            onSuccess(profile)
          } else {
            onSuccess(null)
          }
        }
        .addOnFailureListener { exception ->
          Log.e("UserProfileRepositoryFirestore", "Error fetching profile", exception)
          onFailure(exception)
        }
  }
}
