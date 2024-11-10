package com.arygm.quickfix.model.messaging

class Message(
    val messageId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long
)