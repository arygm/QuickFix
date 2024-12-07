package com.arygm.quickfix.model.profile

import android.util.Log
import com.arygm.quickfix.model.locations.Location
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.cos

open class WorkerProfileRepositoryFirestore(private val db: FirebaseFirestore) : ProfileRepository {

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
      val locationData = document.get("location") as? Map<String, Any> ?: return null
      val price = document.getDouble("price") ?: 0.0
      val rating = document.getDouble("rating") ?: 0.0
      val location =
          locationData.let {
            Location(
                latitude = it["latitude"] as? Double ?: 0.0,
                longitude = it["longitude"] as? Double ?: 0.0,
                name = it["name"] as? String ?: "")
          }
      val unavailability_list =
          document.get("unavailability_list") as? List<LocalDate> ?: emptyList<LocalDate>()
      val workingHours =
          document.get("workingHours") as? Pair<LocalTime, LocalTime>
              ?: Pair(LocalTime.now(), LocalTime.now())
      WorkerProfile(
          rating = rating,
          uid = uid,
          price = price,
          description = description,
          fieldOfWork = fieldOfWork,
          location = location,
          unavailability_list = unavailability_list,
          workingHours = workingHours)
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
      rating: Double?,
      reviews: List<String>?,
      price: Double?,
      fieldOfWork: String?,
      location: Location?,
      radiusInKm: Double?,
      onSuccess: (List<WorkerProfile>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    var query: Query = db.collection(collectionPath)

    rating?.let { query = query.whereEqualTo("rating", it) }
    reviews?.takeIf { it.isNotEmpty() }?.let { query = query.whereArrayContainsAny("reviews", it) }

    fieldOfWork?.let { query = query.whereEqualTo("fieldOfWork", it) }

    price?.let { query = query.whereLessThan("price", it) }

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
          Log.d(
              "WorkerProfileRepositoryFirestore",
              "Successfully fetched worker profiles : ${querySnapshot.documents.size}")
          val workerProfiles = querySnapshot.documents.mapNotNull { documentToWorker(it) }
          onSuccess(workerProfiles)
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }
}
