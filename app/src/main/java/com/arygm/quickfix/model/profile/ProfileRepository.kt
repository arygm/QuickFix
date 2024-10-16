package com.arygm.quickfix.model.profile

interface ProfileRepository {

  fun init(onSuccess: () -> Unit)

  fun getProfiles(onSuccess: (List<Profile>) -> Unit, onFailure: (Exception) -> Unit)

  fun filterWorkers(
      hourlyRateThreshold: Double? = null,
      fieldOfWork: String? = null,
      onSuccess: (List<Profile>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun updateProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deleteProfileById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun profileExists(
      email: String,
      onSuccess: (Pair<Boolean, Profile?>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun getProfileById(uid: String, onSuccess: (Profile?) -> Unit, onFailure: (Exception) -> Unit)
}
