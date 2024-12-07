package com.arygm.quickfix.model.profile.dataFields

data class IncludedService(override val name: String) : Service{
    fun toFirestoreMap(): Map<String, Any> {
        return mapOf("name" to this.name)
    }
}

data class AddOnService(override val name: String) : Service{
    fun toFirestoreMap(): Map<String, Any> {
        return mapOf("name" to this.name)
    }
}
