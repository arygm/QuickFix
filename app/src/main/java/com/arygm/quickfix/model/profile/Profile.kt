package com.arygm.quickfix.model.profile

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Profile(
    val uid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: Timestamp,
    val description: String,
    val isWorker: Boolean = false,
    val fieldOfWork: String? = null,
    val hourlyRate: Double? = null,
    val location: GeoPoint? = null
)

sealed class WorkerCategory {
  sealed class ConstructionAndMaintenance : WorkerCategory() {
    object GeneralLaborer : ConstructionAndMaintenance()

    object Mason : ConstructionAndMaintenance()
  }

  sealed class HomeImprovementAndRepair : WorkerCategory() {
    object Handyman : HomeImprovementAndRepair()

    object FlooringInstaller : HomeImprovementAndRepair()
  }

  sealed class MechanicalAndVehicleMaintenance : WorkerCategory() {
    object AutoMechanic : MechanicalAndVehicleMaintenance()

    object DieselMechanic : MechanicalAndVehicleMaintenance()
  }
}
