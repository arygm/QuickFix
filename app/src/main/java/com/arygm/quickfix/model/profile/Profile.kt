package com.arygm.quickfix.model.profile

import com.arygm.quickfix.model.Location.Location
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

open class Profile(
    val uid: String,
    //val quickFixes: List<String>, // common field
)

class UserProfile(
    val locations: List<Location>,
    uid : String,
    //quickFixes: List<String>, // String of uid that will represents the uid of the QuickFixes
): Profile(uid)


class WorkerProfile(
    val fieldOfWork: WorkerCategory? = null,
    val hourlyRate: Double? = null,
    val description: String = "",
    val location : Location?,
    //quickFixes: List<String>,
    uid : String
) : Profile(uid)


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
