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
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Profile) return false

    return uid == other.uid &&
        firstName == other.firstName &&
        lastName == other.lastName &&
        email == other.email &&
        birthDate == other.birthDate &&
        location == other.location
  }

  override fun hashCode(): Int {
    return listOf(uid, firstName, lastName, email, birthDate, location).hashCode()
  }
}

class UserProfile(
    val isWorker: Boolean = false,
    uid: String,
    firstName: String,
    lastName: String,
    email: String,
    birthDate: Timestamp,
    location: GeoPoint? = null
) : Profile(uid, firstName, lastName, email, birthDate, location) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is UserProfile) return false
    if (!super.equals(other)) return false

    return isWorker == other.isWorker
  }

  override fun hashCode(): Int {
    return listOf(super.hashCode(), isWorker).hashCode()
  }
}

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
) : Profile(uid, firstName, lastName, email, birthDate, location) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is WorkerProfile) return false
    if (!super.equals(other)) return false

    return fieldOfWork == other.fieldOfWork && hourlyRate == other.hourlyRate
  }

  override fun hashCode(): Int {
    return listOf(super.hashCode(), fieldOfWork, hourlyRate).hashCode()
  }
}

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
