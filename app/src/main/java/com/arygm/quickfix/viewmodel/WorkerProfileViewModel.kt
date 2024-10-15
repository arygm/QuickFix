package com.arygm.quickfix.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.arygm.quickfix.model.WorkerProfile
import com.arygm.quickfix.repository.WorkerProfileRepository

class WorkerProfileViewModel(private val repository: WorkerProfileRepository) : ViewModel() {

  var workerProfiles = mutableStateOf<List<WorkerProfile>>(emptyList())
  var errorMessage = mutableStateOf<String?>(null)

  fun filterWorkerProfiles(hourlyRateThreshold: Double? = null, location: String? = null) {
    repository.filterWorkers(
        hourlyRateThreshold,
        location,
        { profiles -> workerProfiles.value = profiles },
        { error -> errorMessage.value = error.message })
  }
}
