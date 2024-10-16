package com.arygm.quickfix.model.profile

import com.google.firebase.Timestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class ProfileViewModelTest {
  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel

  private val profile =
      Profile(
          uid = "1",
          firstName = "John",
          lastName = "Doe",
          email = "john.doe@example.com",
          birthDate = Timestamp.now(),
          description = "Sample description")

  @Before
  fun setUp() {
    profileRepository = mock(ProfileRepository::class.java)
    profileViewModel = ProfileViewModel(profileRepository)
  }

  @Test
  fun getProfiles_callsRepositoryGetProfiles() {
    profileViewModel.getProfiles()
    verify(profileRepository).getProfiles(any(), any())
  }

  @Test
  fun addProfile_callsRepositoryAddProfile() {
    val onSuccess = {}
    val onFailure: (Exception) -> Unit = {}

    profileViewModel.addProfile(profile, onSuccess, onFailure)
    verify(profileRepository).addProfile(eq(profile), any(), any())
  }

  @Test
  fun updateProfile_callsRepositoryUpdateProfile() {
    profileViewModel.updateProfile(profile)
    verify(profileRepository).updateProfile(eq(profile), any(), any())
  }

  @Test
  fun deleteProfileById_callsRepositoryDeleteProfileById() {
    val id = "1"
    profileViewModel.deleteProfileById(id)
    verify(profileRepository).deleteProfileById(eq(id), any(), any())
  }

  @Test
  fun profileExists_callsRepositoryProfileExists() {
    val email = "john.doe@example.com"
    profileViewModel.profileExists(email) { _, _ -> }
    verify(profileRepository).profileExists(eq(email), any(), any())
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun setLoggedInProfile_updatesLoggedInProfileStateFlow() = runBlockingTest {
    profileViewModel.setLoggedInProfile(profile)
    val result = profileViewModel.loggedInProfile.first()
    assertThat(result, `is`(profile))
  }

  @Test
  fun fetchUserProfile_callsRepositoryGetProfileById() {
    val uid = "1"
    profileViewModel.fetchUserProfile(uid) {}
    verify(profileRepository).getProfileById(eq(uid), any(), any())
  }
}
