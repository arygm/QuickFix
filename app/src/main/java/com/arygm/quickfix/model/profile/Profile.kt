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