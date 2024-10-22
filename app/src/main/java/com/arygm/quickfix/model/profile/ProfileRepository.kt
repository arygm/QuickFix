package com.arygm.quickfix.model.profile

interface ProfileRepository {

  fun init(onSuccess: () -> Unit)

  fun getProfiles(
      type: ProfileType,
      onSuccess: (List<Profile>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun filterWorkers(
      hourlyRateThreshold: Double? = null,
      fieldOfWork: String? = null,
      onSuccess: (List<Profile>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun addProfile(
      type: ProfileType,
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun updateProfile(
      type: ProfileType,
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun deleteProfileById(
      type: ProfileType,
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun profileExists(
      type: ProfileType,
      email: String,
      onSuccess: (Pair<Boolean, Profile?>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun getProfileById(
      type: ProfileType,
      uid: String,
      onSuccess: (Profile?) -> Unit,
      onFailure: (Exception) -> Unit
  )
}
