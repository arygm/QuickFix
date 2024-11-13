package com.arygm.quickfix.model.profile

import android.util.Log
import com.arygm.quickfix.model.location.Location
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.math.cos

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
        val workerProfiles =
            task.result?.documents?.mapNotNull { document -> documentToWorker(document) }
                ?: emptyList<WorkerProfile>()
        onSuccess(workerProfiles)
      } else {
        task.exception?.let { e ->
          Log.e("WorkerProfileRepositoryFirestore", "Error getting documents", e)
          onFailure(e)
        }
      }
    }
  }

  override fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val workerProfile = profile as WorkerProfile
    val data =
        mapOf(
            "uid" to workerProfile.uid,
            "hourlyRate" to workerProfile.hourlyRate,
            "description" to workerProfile.description,
            "fieldOfWork" to workerProfile.fieldOfWork, // Convert to string
            "location" to workerProfile.location?.toFirestoreMap())
    performFirestoreOperation(
        db.collection(collectionPath).document(profile.uid).set(data), onSuccess, onFailure)
  }

  override fun updateProfile(
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val workerProfile = profile as WorkerProfile
    val data =
        mapOf(
            "uid" to workerProfile.uid,
            "hourlyRate" to workerProfile.hourlyRate,
            "description" to workerProfile.description,
            "fieldOfWork" to workerProfile.fieldOfWork,
            "location" to workerProfile.location?.toFirestoreMap())
    performFirestoreOperation(
        db.collection(collectionPath).document(profile.uid).set(data), onSuccess, onFailure)
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
          Log.e("WorkerProfileRepositoryFirestore", "Error performing Firestore operation", e)
          onFailure(e)
        }
      }
    }
  }

  private fun documentToWorker(document: DocumentSnapshot): WorkerProfile? {
    return try {
      val uid = document.id
      val description = document.getString("description") ?: return null
      val fieldOfWork = document.getString("fieldOfWork") ?: return null
      val hourlyRate = document.getDouble("hourlyRate") ?: return null
      val locationData = document.get("location") as? Map<*, *> ?: return null
      val location =
          locationData?.let {
            Location(
                latitude = it["latitude"] as? Double ?: 0.0,
                longitude = it["longitude"] as? Double ?: 0.0,
                name = it["name"] as? String ?: "")
          }
      WorkerProfile(
          uid = uid,
          description = description,
          fieldOfWork = fieldOfWork,
          hourlyRate = hourlyRate,
          location = location)
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

  fun filterWorkers(
      hourlyRateThreshold: Double?,
      fieldOfWork: String?,
      location: Location?,
      radiusInKm: Double?,
      onSuccess: (List<WorkerProfile>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    var query: Query = db.collection(collectionPath)

    fieldOfWork?.let { query = query.whereEqualTo("fieldOfWork", it) }

    hourlyRateThreshold?.let { query = query.whereLessThan("hourlyRate", it) }

    if (location != null && radiusInKm != null) {
      val earthRadius = 6371.0
      val lat = location.latitude
      val lon = location.longitude
      val latDelta = radiusInKm / earthRadius
      val lonDelta = radiusInKm / (earthRadius * cos(Math.toRadians(lat)))

      val minLat = lat - Math.toDegrees(latDelta)
      val maxLat = lat + Math.toDegrees(latDelta)
      val minLon = lon - Math.toDegrees(lonDelta)
      val maxLon = lon + Math.toDegrees(lonDelta)

      // Add range filters for latitude and longitude
      query =
          query
              .whereGreaterThanOrEqualTo("location.latitude", minLat)
              .whereLessThanOrEqualTo("location.latitude", maxLat)
              .whereGreaterThanOrEqualTo("location.longitude", minLon)
              .whereLessThanOrEqualTo("location.longitude", maxLon)
    }
    query
        .get()
        .addOnSuccessListener { querySnapshot ->
          val workerProfiles =
              querySnapshot.documents.mapNotNull { it.toObject(WorkerProfile::class.java) }
          onSuccess(workerProfiles)
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }
}
