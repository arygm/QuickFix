package com.arygm.quickfix.model.profile

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

open class Profile(
    val uid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: Timestamp,
    val location: GeoPoint? = null
)

class UserProfile(
    val isWorker: Boolean = false,
    uid: String,
    firstName: String,
    lastName: String,
    email: String,
    birthDate: Timestamp,
    location: GeoPoint? = null
) : Profile(uid, firstName, lastName, email, birthDate, location)

class WorkerProfile(
    val fieldOfWork: String? = null,
    val hourlyRate: Double? = null,
    val description: String = "",
    uid: String,
    firstName: String,
    lastName: String,
    email: String,
    birthDate: Timestamp,
    location: GeoPoint? = null
) : Profile(uid, firstName, lastName, email, birthDate, location)

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
