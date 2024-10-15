package com.arygm.quickfix.model

import android.location.Location

data class WorkerProfile(
    val birthDate: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val uid: String = "",
    val location: Location? = null,
    val hourlyRate: Double = 0.0,
)
