package com.arygm.quickfix.model.account

import com.google.firebase.Timestamp

data class Account(
    val uid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: Timestamp,
    val isWorker: Boolean = false,
    val activeChats: List<String> = emptyList(),
    val profilePicture: String =
        "https://example.com/default-profile-pic.jpg" // Default profile picture URL
)
