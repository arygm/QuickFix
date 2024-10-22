package com.arygm.quickfix.model.profile

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProfileRepositoryFirestore(private val db: FirebaseFirestore) : ProfileRepository {

  private val usersCollectionPath = "users"
  private val workersCollectionPath = "workers"

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun getProfiles(
      type: ProfileType,
      onSuccess: (List<Profile>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    Log.d("ProfileRepositoryFirestore", "getProfiles")
    db.collection(if (type == ProfileType.USER) usersCollectionPath else workersCollectionPath)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            val profiles =
                task.result?.documents?.mapNotNull { document ->
                  if (type == ProfileType.USER) documentToUser(document)
                  else documentToWorker(document)
                } ?: emptyList()
            onSuccess(profiles)
          } else {
            task.exception?.let { e ->
              Log.e("ProfileRepositoryFirestore", "Error getting documents", e)
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
    var query: Query = db.collection("worker_profiles")

    query = query.whereEqualTo("isWorker", true)

    fieldOfWork?.let { query = query.whereEqualTo("fieldOfWork", it) }

    hourlyRateThreshold?.let { query = query.whereLessThan("hourlyRate", it) }

    query
        .get()
        .addOnSuccessListener { querySnapshot ->
          val workerProfiles =
              querySnapshot.documents.mapNotNull { it.toObject(Profile::class.java) }
          onSuccess(workerProfiles)
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  override fun addProfile(
      type: ProfileType,
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(if (type == ProfileType.USER) usersCollectionPath else workersCollectionPath)
            .document(profile.uid)
            .set(profile),
        onSuccess,
        onFailure)
  }

  override fun updateProfile(
      type: ProfileType,
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(if (type == ProfileType.USER) usersCollectionPath else workersCollectionPath)
            .document(profile.uid)
            .set(profile),
        onSuccess,
        onFailure)
  }

  override fun deleteProfileById(
      type: ProfileType,
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(if (type == ProfileType.USER) usersCollectionPath else workersCollectionPath)
            .document(id)
            .delete(),
        onSuccess,
        onFailure)
  }

  override fun profileExists(
      type: ProfileType,
      email: String,
      onSuccess: (Pair<Boolean, Profile?>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(if (type == ProfileType.USER) usersCollectionPath else workersCollectionPath)
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { querySnapshot ->
          if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents.first()
            val profile =
                if (type == ProfileType.USER) documentToUser(document)
                else documentToWorker(document)
            onSuccess(Pair(true, profile))
          } else {
            onSuccess(Pair(false, null))
          }
        }
        .addOnFailureListener { exception ->
          Log.e("ProfileRepositoryFirestore", "Error checking if profile exists", exception)
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
          Log.e("ProfileRepositoryFirestore", "Error performing Firestore operation", e)
          onFailure(e)
        }
      }
    }
  }

  private fun documentToProfileBase(document: DocumentSnapshot): Profile? {
    return try {
      val uid = document.id
      val firstName = document.getString("firstName") ?: return null
      val lastName = document.getString("lastName") ?: return null
      val email = document.getString("email") ?: return null
      val birthDate = document.getTimestamp("birthDate") ?: return null
      val description = document.getString("description") ?: return null
      val location = document.getGeoPoint("location") ?: return null

      Profile(uid, firstName, lastName, email, birthDate, description, location)
    } catch (e: Exception) {
      Log.e("ProfileRepositoryFirestore", "Error converting document to base profile", e)
      null
    }
  }

  private fun documentToUser(document: DocumentSnapshot): UserProfile? {
    return try {
      val profileBase = documentToProfileBase(document) ?: return null
      val isWorker = document.getBoolean("isWorker") ?: return null

      UserProfile(
          uid = profileBase.uid,
          firstName = profileBase.firstName,
          lastName = profileBase.lastName,
          email = profileBase.email,
          birthDate = profileBase.birthDate,
          description = profileBase.description,
          location = profileBase.location,
          isWorker = isWorker)
    } catch (e: Exception) {
      Log.e("ProfileRepositoryFirestore", "Error converting document to base profile", e)
      null
    }
  }

  private fun documentToWorker(document: DocumentSnapshot): WorkerProfile? {
    return try {
      val profileBase = documentToProfileBase(document) ?: return null
      val fieldOfWork = document.getString("fieldOfWork") ?: return null
      val hourlyRate = document.getDouble("hourlyRate") ?: return null

      WorkerProfile(
          uid = profileBase.uid,
          firstName = profileBase.firstName,
          lastName = profileBase.lastName,
          email = profileBase.email,
          birthDate = profileBase.birthDate,
          description = profileBase.description,
          location = profileBase.location,
          fieldOfWork = fieldOfWork,
          hourlyRate = hourlyRate)
    } catch (e: Exception) {
      Log.e("ProfileRepositoryFirestore", "Error converting document to base profile", e)
      null
    }
  }

  override fun getProfileById(
      type: ProfileType,
      uid: String,
      onSuccess: (Profile?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(if (type == ProfileType.USER) usersCollectionPath else workersCollectionPath)
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
          if (document.exists()) {
            val profile =
                if (type == ProfileType.USER) documentToUser(document)
                else documentToWorker(document)
            onSuccess(profile)
          } else {
            onSuccess(null)
          }
        }
        .addOnFailureListener { exception ->
          Log.e("ProfileRepositoryFirestore", "Error fetching profile", exception)
          onFailure(exception)
        }
  }
}
