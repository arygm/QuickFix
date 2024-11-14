package com.arygm.quickfix.model.locations

interface LocationRepository {
    fun search(query: String, onSuccess: (List<Location>) -> Unit, onFailure: (Exception) -> Unit)
}