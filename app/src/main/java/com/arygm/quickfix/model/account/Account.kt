package com.arygm.quickfix.model.account

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Account(
    val uid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: Timestamp,
    val isWorker : Boolean = false
)
