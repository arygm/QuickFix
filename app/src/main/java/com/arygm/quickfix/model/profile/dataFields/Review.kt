package com.arygm.quickfix.model.profile.dataFields

import android.media.Rating

data class Review(val username: String, val review: String, val rating: Double){
    fun toFirestoreMap(): Map<String, Any> {
        return mapOf("username" to this.username, "review" to this.review, "rating" to this.rating)
    }
}
