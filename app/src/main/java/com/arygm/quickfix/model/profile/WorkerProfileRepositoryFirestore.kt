package com.arygm.quickfix.model.profile

import android.util.Log
import com.arygm.quickfix.model.Location.Location
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
      val data = mapOf(
          "uid" to workerProfile.uid,
          "hourlyRate" to workerProfile.hourlyRate,
          "description" to workerProfile.description,
          "fieldOfWork" to workerProfile.fieldOfWork?.toFirestoreString(), // Convert to string
          "location" to workerProfile.location?.toFirestoreMap()
      )
      performFirestoreOperation(
        db.collection(collectionPath).document(profile.uid).set(data), onSuccess, onFailure)
  }

  override fun updateProfile(
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
      val workerProfile = profile as WorkerProfile
      val data = mapOf(
          "uid" to workerProfile.uid,
          "hourlyRate" to workerProfile.hourlyRate,
          "description" to workerProfile.description,
          "fieldOfWork" to workerProfile.fieldOfWork?.toFirestoreString(), // Convert to string
          "location" to workerProfile.location?.toFirestoreMap()
      )
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
        val locationData = document.get("location") as? Map<*, *>
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
          fieldOfWork = fieldOfWork.toWorkerCategory(),
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
    private fun String.toWorkerCategory(): WorkerCategory? {
        return when (this) {
            "ConstructionAndMaintenance.GeneralLaborer" -> WorkerCategory.ConstructionAndMaintenance.GeneralLaborer
            "ConstructionAndMaintenance.Mason" -> WorkerCategory.ConstructionAndMaintenance.Mason
            "HomeImprovementAndRepair.Handyman" -> WorkerCategory.HomeImprovementAndRepair.Handyman
            "HomeImprovementAndRepair.FlooringInstaller" -> WorkerCategory.HomeImprovementAndRepair.FlooringInstaller
            "MechanicalAndVehicleMaintenance.AutoMechanic" -> WorkerCategory.MechanicalAndVehicleMaintenance.AutoMechanic
            "MechanicalAndVehicleMaintenance.DieselMechanic" -> WorkerCategory.MechanicalAndVehicleMaintenance.DieselMechanic
            else -> null
        }
    }

    private fun WorkerCategory.toFirestoreString(): String {
        return when (this) {
            is WorkerCategory.ConstructionAndMaintenance.GeneralLaborer -> "ConstructionAndMaintenance.GeneralLaborer"
            is WorkerCategory.ConstructionAndMaintenance.Mason -> "ConstructionAndMaintenance.Mason"
            is WorkerCategory.HomeImprovementAndRepair.Handyman -> "HomeImprovementAndRepair.Handyman"
            is WorkerCategory.HomeImprovementAndRepair.FlooringInstaller -> "HomeImprovementAndRepair.FlooringInstaller"
            is WorkerCategory.MechanicalAndVehicleMaintenance.AutoMechanic -> "MechanicalAndVehicleMaintenance.AutoMechanic"
            is WorkerCategory.MechanicalAndVehicleMaintenance.DieselMechanic -> "MechanicalAndVehicleMaintenance.DieselMechanic"
        }
    }

    private fun WorkerProfile.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "uid" to this.uid,
            "hourlyRate" to this.hourlyRate,
            "description" to this.description,
            "fieldOfWork" to this.fieldOfWork?.toFirestoreString() // Convert sealed class to string
        )
    }


}
