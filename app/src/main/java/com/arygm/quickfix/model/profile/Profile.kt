package com.arygm.quickfix.model.profile

data class Profile(
    val uid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: com.google.firebase.Timestamp,
    val description: String,
    val isWorker: Boolean = false,
    val fieldOfWork: String? = null,
    val hourlyRate: Double? = null
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
