package com.arygm.quickfix.model.locations

data class Location(val latitude: Double, val longitude: Double, val name: String) {
  fun toFirestoreMap(): Map<String, Any> {
    return mapOf("latitude" to this.latitude, "longitude" to this.longitude, "name" to this.name)
  }
}
