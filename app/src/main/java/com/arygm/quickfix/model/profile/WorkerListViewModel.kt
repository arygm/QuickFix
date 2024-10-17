package com.arygm.quickfix.model.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class WorkerListViewModel(private val repository: ProfileRepositoryFirestore) : ViewModel() {

  var workerProfiles = mutableStateOf<List<Profile>>(emptyList())
  var errorMessage = mutableStateOf<String?>(null)

  fun filterWorkerProfiles(
      hourlyRateThreshold: Double? = null,
      location: String? = null,
      fieldOfWork: String? = null
  ) {
    repository.filterWorkers(
        hourlyRateThreshold,
        location,
        fieldOfWork,
        { profiles -> workerProfiles.value = profiles },
        { error -> errorMessage.value = error.message })
  }
}
