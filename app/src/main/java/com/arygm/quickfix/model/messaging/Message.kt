package com.arygm.quickfix.model.messaging

import com.google.firebase.Timestamp

class Message(
    val messageId: String,
    val senderId: String,
    val content: String,
    val timestamp: Timestamp
)