package com.arygm.quickfix.model.profile

import android.util.Log
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

  override fun filterWorkers(
      hourlyRateThreshold: Double?,
      fieldOfWork: String?,
      onSuccess: (List<Profile>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    //        var query: Query = db.collection("worker_profiles")
    //
    //        query = query.whereEqualTo("isWorker", true)
    //
    //        fieldOfWork?.let { query = query.whereEqualTo("fieldOfWork", it) }
    //
    //        hourlyRateThreshold?.let { query = query.whereLessThan("hourlyRate", it) }
    //
    //        query
    //            .get()
    //            .addOnSuccessListener { querySnapshot ->
    //                val workerProfiles =
    //                    querySnapshot.documents.mapNotNull { it.toObject(Profile::class.java) }
    //                onSuccess(workerProfiles)
    //            }
    //            .addOnFailureListener { exception -> onFailure(exception) }
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

  override fun profileExists(
      email: String,
      onSuccess: (Pair<Boolean, Profile?>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { querySnapshot ->
          if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents.first()
            val profile = documentToUser(document)
            onSuccess(Pair(true, profile))
          } else {
            onSuccess(Pair(false, null))
          }
        }
        .addOnFailureListener { exception ->
          Log.e("UserProfileRepositoryFirestore", "Error checking if profile exists", exception)
          onFailure(exception)
        }
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
      val firstName = document.getString("firstName") ?: return null
      val lastName = document.getString("lastName") ?: return null
      val email = document.getString("email") ?: return null
      val birthDate = document.getTimestamp("birthDate") ?: return null
      val location = document.getGeoPoint("location") ?: return null
      val isWorker = document.getBoolean("isWorker") ?: return null

      UserProfile(
          uid = uid,
          firstName = firstName,
          lastName = lastName,
          email = email,
          birthDate = birthDate,
          location = location,
          isWorker = isWorker)
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
