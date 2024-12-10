package com.arygm.quickfix.model.messaging

import com.google.firebase.Timestamp

data class Message(
    val messageId: String = "", // Default value
    val senderId: String = "", // Default value
    val content: String = "", // Default value
    val timestamp: Timestamp = Timestamp.now() // Default value
)
