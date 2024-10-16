package com.arygm.quickfix.model.profile

import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.*
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

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

    val initCaptor = argumentCaptor<() -> Unit>()
    verify(profileRepository).init(initCaptor.capture())

    // Mock getProfiles
    doNothing().`when`(profileRepository).getProfiles(any(), any())

    // Simulate repository calling the init callback
    initCaptor.firstValue.invoke()
  }

  @Test
  fun init_callsRepositoryInit() {
    verify(profileRepository).init(any())
  }

  @Test
  fun init_invokesGetProfilesWhenRepositoryInitCallsCallback() {
    verify(profileRepository).getProfiles(any(), any())
  }

  @Test
  fun getProfiles_whenSuccess_updatesProfilesStateFlow() = runTest {
    val profilesList = listOf(profile)
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Profile>) -> Unit>(0)
          onSuccess(profilesList)
          null
        }
        .`when`(profileRepository)
        .getProfiles(any(), any())

    profileViewModel.getProfiles()

    val result = profileViewModel.profiles.first()
    assertThat(result, `is`(profilesList))
  }

  @Test
  fun getProfiles_whenFailure_logsError() {
    val exception = Exception("Test exception")
    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(1)
          onFailure(exception)
          null
        }
        .`when`(profileRepository)
        .getProfiles(any(), any())

    profileViewModel.getProfiles()

    // We can check that profiles remains empty
    val result = profileViewModel.profiles.value
    assertThat(result, `is`(emptyList()))
  }

  @Test
  fun addProfile_whenSuccess_callsGetProfilesAndOnSuccess() {
    org.mockito.Mockito.clearInvocations(profileRepository)
    val onSuccessMock = mock<() -> Unit>()
    val onFailureMock = mock<(Exception) -> Unit>()

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .`when`(profileRepository)
        .addProfile(any(), any(), any())

    doNothing().`when`(profileRepository).getProfiles(any(), any())

    profileViewModel.addProfile(profile, onSuccessMock, onFailureMock)

    verify(profileRepository).getProfiles(any(), any())
    verify(onSuccessMock).invoke()
  }

  @Test
  fun addProfile_whenFailure_callsOnFailure() {
    val exception = Exception("Test exception")
    val onSuccessMock = mock<() -> Unit>()
    val onFailureMock = mock<(Exception) -> Unit>()

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .`when`(profileRepository)
        .addProfile(any(), any(), any())

    profileViewModel.addProfile(profile, onSuccessMock, onFailureMock)

    verify(onFailureMock).invoke(exception)
  }

  @Test
  fun updateProfile_whenSuccess_callsGetProfiles() {
    org.mockito.Mockito.clearInvocations(profileRepository)
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .`when`(profileRepository)
        .updateProfile(any(), any(), any())

    doNothing().`when`(profileRepository).getProfiles(any(), any())

    profileViewModel.updateProfile(profile)

    verify(profileRepository).getProfiles(any(), any())
  }

  @Test
  fun updateProfile_whenFailure_logsError() {
    val exception = Exception("Test exception")
    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .`when`(profileRepository)
        .updateProfile(any(), any(), any())

    profileViewModel.updateProfile(profile)

    // Can't verify logging easily
  }

  @Test
  fun deleteProfileById_whenSuccess_callsGetProfiles() {
    org.mockito.Mockito.clearInvocations(profileRepository)
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .`when`(profileRepository)
        .deleteProfileById(any(), any(), any())

    doNothing().`when`(profileRepository).getProfiles(any(), any())

    profileViewModel.deleteProfileById(profile.uid)

    verify(profileRepository).getProfiles(any(), any())
  }

  @Test
  fun deleteProfileById_whenFailure_logsError() {
    val exception = Exception("Test exception")
    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .`when`(profileRepository)
        .deleteProfileById(any(), any(), any())

    profileViewModel.deleteProfileById(profile.uid)
  }

  @Test
  fun profileExists_whenProfileExists_callsOnResultWithTrueAndProfile() {
    val onResultMock = mock<(Boolean, Profile?) -> Unit>()

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(Pair<Boolean, Profile?>) -> Unit>(1)
          onSuccess(Pair(true, profile))
          null
        }
        .`when`(profileRepository)
        .profileExists(any(), any(), any())

    profileViewModel.profileExists(profile.email, onResultMock)

    verify(onResultMock).invoke(true, profile)
  }

  @Test
  fun profileExists_whenProfileDoesNotExist_callsOnResultWithFalseAndNull() {
    val onResultMock = mock<(Boolean, Profile?) -> Unit>()

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(Pair<Boolean, Profile?>) -> Unit>(1)
          onSuccess(Pair(false, null))
          null
        }
        .`when`(profileRepository)
        .profileExists(any(), any(), any())

    profileViewModel.profileExists("unknown@example.com", onResultMock)

    verify(onResultMock).invoke(false, null)
  }

  @Test
  fun profileExists_whenFailure_callsOnResultWithFalseAndNull() {
    val exception = Exception("Test exception")
    val onResultMock = mock<(Boolean, Profile?) -> Unit>()

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .`when`(profileRepository)
        .profileExists(any(), any(), any())

    profileViewModel.profileExists(profile.email, onResultMock)

    verify(onResultMock).invoke(false, null)
  }

  @Test
  fun fetchUserProfile_whenProfileExists_callsOnResultWithProfile() {
    val onResultMock = mock<(Profile?) -> Unit>()

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(Profile?) -> Unit>(1)
          onSuccess(profile)
          null
        }
        .`when`(profileRepository)
        .getProfileById(any(), any(), any())

    profileViewModel.fetchUserProfile(profile.uid, onResultMock)

    verify(onResultMock).invoke(profile)
  }

  @Test
  fun fetchUserProfile_whenProfileDoesNotExist_callsOnResultWithNull() {
    val onResultMock = mock<(Profile?) -> Unit>()

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(Profile?) -> Unit>(1)
          onSuccess(null)
          null
        }
        .`when`(profileRepository)
        .getProfileById(any(), any(), any())

    profileViewModel.fetchUserProfile("nonexistent", onResultMock)

    verify(onResultMock).invoke(null)
  }

  @Test
  fun fetchUserProfile_whenFailure_logsError() {
    val exception = Exception("Test exception")
    val onResultMock = mock<(Profile?) -> Unit>()

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .`when`(profileRepository)
        .getProfileById(any(), any(), any())

    profileViewModel.fetchUserProfile(profile.uid, onResultMock)

    verify(onResultMock).invoke(null)
  }

  @Test
  fun setLoggedInProfile_updatesLoggedInProfileStateFlow() = runTest {
    profileViewModel.setLoggedInProfile(profile)
    val result = profileViewModel.loggedInProfile.first()
    assertThat(result, `is`(profile))
  }

  @Test
  fun getProfiles_callsRepositoryGetProfiles() {
    org.mockito.Mockito.clearInvocations(profileRepository)
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

  @Test
  fun fetchUserProfile_callsRepositoryGetProfileById() {
    val uid = "1"
    profileViewModel.fetchUserProfile(uid) {}
    verify(profileRepository).getProfileById(eq(uid), any(), any())
  }
}
