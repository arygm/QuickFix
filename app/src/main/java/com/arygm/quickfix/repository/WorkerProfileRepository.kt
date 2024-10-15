package com.arygm.quickfix.repository

import com.arygm.quickfix.model.WorkerProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class WorkerProfileRepository {
  private val db = FirebaseFirestore.getInstance()

  fun filterWorkers(
      hourlyRateThreshold: Double? = null,
      location: String? = null,
      onSuccess: (List<WorkerProfile>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    var query: Query = db.collection("worker_profiles")

    hourlyRateThreshold?.let { query = query.whereLessThan("hourlyRate", it) }

    location?.let { query = query.whereEqualTo("location", it) }

    // Execute the query
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
