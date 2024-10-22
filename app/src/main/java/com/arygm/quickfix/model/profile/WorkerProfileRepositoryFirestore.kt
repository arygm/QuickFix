package com.arygm.quickfix.model.profile

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class WorkerProfileRepositoryFirestore(private val db: FirebaseFirestore) : ProfileRepository {

  private val collectionPath = "workers"

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun getProfiles(onSuccess: (List<Profile>) -> Unit, onFailure: (Exception) -> Unit) {
    Log.d("ProfileRepositoryFirestore", "getProfiles")
    db.collection(collectionPath).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val profiles =
            task.result?.documents?.mapNotNull { document -> documentToWorker(document) }
                ?: emptyList()
        onSuccess(profiles)
      } else {
        task.exception?.let { e ->
          Log.e("WorkerProfileRepositoryFirestore", "Error getting documents", e)
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
            val profile = documentToWorker(document)
            onSuccess(Pair(true, profile))
          } else {
            onSuccess(Pair(false, null))
          }
        }
        .addOnFailureListener { exception ->
          Log.e("WorkerProfileRepositoryFirestore", "Error checking if profile exists", exception)
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
          Log.e("WorkerProfileRepositoryFirestore", "Error performing Firestore operation", e)
          onFailure(e)
        }
      }
    }
  }

  private fun documentToWorker(document: DocumentSnapshot): Profile? {
    return try {
      val uid = document.id
      val firstName = document.getString("firstName") ?: return null
      val lastName = document.getString("lastName") ?: return null
      val email = document.getString("email") ?: return null
      val birthDate = document.getTimestamp("birthDate") ?: return null
      val description = document.getString("description") ?: return null
      val location = document.getGeoPoint("location") ?: return null
      val fieldOfWork = document.getString("fieldOfWork") ?: return null
      val hourlyRate = document.getDouble("hourlyRate") ?: return null

      WorkerProfile(
          uid = uid,
          firstName = firstName,
          lastName = lastName,
          email = email,
          birthDate = birthDate,
          description = description,
          location = location,
          fieldOfWork = fieldOfWork,
          hourlyRate = hourlyRate)
    } catch (e: Exception) {
      Log.e("WorkerProfileRepositoryFirestore", "Error converting document to WorkerProfile", e)
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
            val profile = documentToWorker(document)
            onSuccess(profile)
          } else {
            onSuccess(null)
          }
        }
        .addOnFailureListener { exception ->
          Log.e("WorkerProfileRepositoryFirestore", "Error fetching profile", exception)
          onFailure(exception)
        }
  }
}
