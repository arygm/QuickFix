package com.arygm.quickfix.utils

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.arygm.quickfix.model.profile.LoggedInProfileViewModel
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class SignInWithEmailAndPasswordTest {
  private lateinit var firebaseAuth: FirebaseAuth

  @Mock private lateinit var firebaseUser: FirebaseUser

  @Mock private lateinit var authResult: AuthResult

  @Mock private lateinit var profileRepository: UserProfileRepositoryFirestore

  private lateinit var loggedInProfileViewModel: LoggedInProfileViewModel

  private lateinit var profileViewModel: ProfileViewModel

  private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize FirebaseApp if necessary
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    if (FirebaseApp.getApps(context).isEmpty()) {
      FirebaseApp.initializeApp(context)
    }

    // Mock FirebaseAuth.getInstance()
    firebaseAuthMockedStatic = Mockito.mockStatic(FirebaseAuth::class.java)
    firebaseAuth = Mockito.mock(FirebaseAuth::class.java)

    firebaseAuthMockedStatic
        .`when`<FirebaseAuth> { FirebaseAuth.getInstance() }
        .thenReturn(firebaseAuth)

    // Initialize profileViewModel with the mocked repository
    profileViewModel = ProfileViewModel(profileRepository)
    loggedInProfileViewModel = LoggedInProfileViewModel()

    // Mock FirebaseAuth.getInstance().currentUser
    whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)
  }

  @After
  fun tearDown() {
    firebaseAuthMockedStatic.close()
  }

  @Test
  fun testSignInSuccessAndProfileFetchSuccess() {
    // Prepare test data
    val email = "john.doe@example.com"
    val password = "password123"
    val uid = "testUid"

    val profile =
        UserProfile(
            uid = uid,
            firstName = "John",
            lastName = "Doe",
            email = email,
            birthDate = Timestamp.now(),
            location = GeoPoint(0.0, 0.0))

    // Mock FirebaseAuth behavior
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(any(), any()))
        .thenReturn(signInTaskCompletionSource.task)
    whenever(authResult.user).thenReturn(firebaseUser)
    whenever(firebaseUser.uid).thenReturn(uid)
    whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

    // Mock profileRepository.getProfileById to return the profile
    whenever(profileRepository.getProfileById(eq(uid), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(Profile?) -> Unit>(1)
      onSuccess(profile)
      null
    }

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchProfile(
        email = email,
        password = password,
        userViewModel = profileViewModel,
        loggedInProfileViewModel = loggedInProfileViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        })

    // Simulate task completion
    signInTaskCompletionSource.setResult(authResult)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertTrue(resultValue == true)

    // Verify that the profile was fetched
    verify(profileRepository).getProfileById(eq(uid), any(), any())

    // Verify that the loggedInProfile was set
    assertEquals(profile, loggedInProfileViewModel.loggedInProfile.value)
  }

  @Test
  fun testSignInFailure() {
    // Prepare test data
    val email = "john.doe@example.com"
    val password = "wrongPassword"
    val exception = Exception("Authentication failed")

    // Mock FirebaseAuth behavior
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(any(), any()))
        .thenReturn(signInTaskCompletionSource.task)

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchProfile(
        email = email,
        password = password,
        userViewModel = profileViewModel,
        loggedInProfileViewModel = loggedInProfileViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        })

    // Simulate task failure
    signInTaskCompletionSource.setException(exception)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)

    // Verify that fetchUserProfile was never called
    verify(profileRepository, never()).getProfileById(any(), any(), any())
  }

  @Test
  fun testSignInSuccessButFetchProfileReturnsNull() {
    // Prepare test data
    val email = "john.doe@example.com"
    val password = "password123"
    val uid = "testUid"

    // Mock FirebaseAuth behavior
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(any(), any()))
        .thenReturn(signInTaskCompletionSource.task)
    whenever(authResult.user).thenReturn(firebaseUser)
    whenever(firebaseUser.uid).thenReturn(uid)
    whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

    // Mock profileRepository.getProfileById to return null
    whenever(profileRepository.getProfileById(eq(uid), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(Profile?) -> Unit>(1)
      onSuccess(null)
      null
    }

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchProfile(
        email = email,
        password = password,
        userViewModel = profileViewModel,
        loggedInProfileViewModel = loggedInProfileViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        })

    // Simulate task completion
    signInTaskCompletionSource.setResult(authResult)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)

    // Verify that loggedInProfile was not set
    assertNull(loggedInProfileViewModel.loggedInProfile.value)
  }

  @Test
  fun testSignInSuccessButNoCurrentUser() {
    // Prepare test data
    val email = "john.doe@example.com"
    val password = "password123"

    // Mock FirebaseAuth behavior
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(any(), any()))
        .thenReturn(signInTaskCompletionSource.task)

    // Simulate that currentUser is null
    whenever(firebaseAuth.currentUser).thenReturn(null)

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchProfile(
        email = email,
        password = password,
        userViewModel = profileViewModel,
        loggedInProfileViewModel = loggedInProfileViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        })

    // Simulate task completion
    signInTaskCompletionSource.setResult(authResult)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)

    // Verify that fetchUserProfile was not called
    verify(profileRepository, never()).getProfileById(any(), any(), any())
  }

  @Test
  fun testFetchUserProfileFails() {
    // Prepare test data
    val email = "john.doe@example.com"
    val password = "password123"
    val uid = "testUid"
    val exception = Exception("Profile fetch failed")

    // Mock FirebaseAuth behavior
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(any(), any()))
        .thenReturn(signInTaskCompletionSource.task)
    whenever(authResult.user).thenReturn(firebaseUser)
    whenever(firebaseUser.uid).thenReturn(uid)
    whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

    // Mock profileRepository.getProfileById to call onFailure
    whenever(profileRepository.getProfileById(eq(uid), any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
      onFailure(exception)
      null
    }

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchProfile(
        email = email,
        password = password,
        userViewModel = profileViewModel,
        loggedInProfileViewModel = loggedInProfileViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        })

    // Simulate task completion
    signInTaskCompletionSource.setResult(authResult)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)

    // Verify that loggedInProfile was not set
    assertNull(loggedInProfileViewModel.loggedInProfile.value)
  }

  @Test
  fun testSignInWithEmptyEmail() {
    // Prepare test data
    val email = ""
    val password = "password123"
    val exception = Exception("Email cannot be empty")

    // Mock FirebaseAuth behavior to fail
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(eq(email), any()))
        .thenReturn(signInTaskCompletionSource.task)

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchProfile(
        email = email,
        password = password,
        userViewModel = profileViewModel,
        loggedInProfileViewModel = loggedInProfileViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        })

    // Simulate task failure
    signInTaskCompletionSource.setException(exception)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)

    // Verify that fetchUserProfile was never called
    verify(profileRepository, never()).getProfileById(any(), any(), any())
  }

  @Test
  fun testSignInWithEmptyPassword() {
    // Prepare test data
    val email = "john.doe@example.com"
    val password = ""
    val exception = Exception("Password cannot be empty")

    // Mock FirebaseAuth behavior to fail
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(any(), eq(password)))
        .thenReturn(signInTaskCompletionSource.task)

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchProfile(
        email = email,
        password = password,
        userViewModel = profileViewModel,
        loggedInProfileViewModel = loggedInProfileViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        })

    // Simulate task failure
    signInTaskCompletionSource.setException(exception)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)

    // Verify that fetchUserProfile was never called
    verify(profileRepository, never()).getProfileById(any(), any(), any())
  }

  @Test
  fun testSignInWithInvalidEmailFormat() {
    // Prepare test data
    val email = "invalid-email"
    val password = "password123"
    val exception = Exception("Invalid email format")

    // Mock FirebaseAuth behavior to fail
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(eq(email), any()))
        .thenReturn(signInTaskCompletionSource.task)

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchProfile(
        email = email,
        password = password,
        userViewModel = profileViewModel,
        loggedInProfileViewModel = loggedInProfileViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        })

    // Simulate task failure
    signInTaskCompletionSource.setException(exception)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)

    // Verify that fetchUserProfile was never called
    verify(profileRepository, never()).getProfileById(any(), any(), any())
  }
}
