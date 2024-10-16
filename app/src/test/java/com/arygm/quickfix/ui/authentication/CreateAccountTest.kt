package com.arygm.quickfix.ui.authentication

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class CreateAccountTest {

    private lateinit var firebaseAuth: FirebaseAuth

    @Mock
    private lateinit var firebaseUser: FirebaseUser

    @Mock
    private lateinit var authResult: AuthResult

    @Mock
    private lateinit var profileRepository: ProfileRepository

    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Initialize FirebaseApp if necessary
        if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
            FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        }

        // Mock FirebaseAuth.getInstance()
        firebaseAuthMockedStatic = Mockito.mockStatic(FirebaseAuth::class.java)
        firebaseAuth = Mockito.mock(FirebaseAuth::class.java)

        // Mock FirebaseAuth.getInstance() to return the mockFirebaseAuth
        firebaseAuthMockedStatic
            .`when`<FirebaseAuth> { FirebaseAuth.getInstance() }
            .thenReturn(firebaseAuth)
        // Alternatively, for Firebase.auth
        firebaseAuthMockedStatic
            .`when`<FirebaseAuth> { FirebaseAuth.getInstance() }
            .thenReturn(firebaseAuth)

        // Initialize profileViewModel with the mocked repository
        profileViewModel = ProfileViewModel(profileRepository)
    }

    @After
    fun tearDown() {
        // Close the static mock
        firebaseAuthMockedStatic.close()
    }

    @Test
    fun testCreateAccountSuccess() {
        // Prepare test data
        val firstName = "John"
        val lastName = "Doe"
        val email = "john.doe@example.com"
        val password = "Password1"
        val birthDate = "01/01/1990"
        val uid = "testUid"

        // Mock FirebaseAuth behavior
        val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
        whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(authTaskCompletionSource.task)
        whenever(authResult.user).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(uid)
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

        // Mock ProfileRepository behavior to succeed
        whenever(profileRepository.addProfile(any(), any(), any())).thenAnswer { invocation ->
            val onSuccessCallback = invocation.getArgument<() -> Unit>(1)
            onSuccessCallback()
            null
        }

        // Flags to check callbacks
        var successCalled = false
        var failureCalled = false

        // Invoke the function under test
        createAccountWithEmailAndPassword(
            firebaseAuth = firebaseAuth,
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            birthDate = birthDate,
            profileViewModel = profileViewModel,
            onSuccess = { successCalled = true },
            onFailure = { failureCalled = true }
        )

        // Simulate task completion
        authTaskCompletionSource.setResult(authResult)

        // Allow background tasks to complete
        shadowOf(Looper.getMainLooper()).idle()

        // Assertions
        assertTrue(successCalled)
        assertFalse(failureCalled)

        // Verify that the profile was added correctly
        val profileCaptor = argumentCaptor<Profile>()
        verify(profileRepository).addProfile(profileCaptor.capture(), any(), any())
        val capturedProfile = profileCaptor.firstValue
        assertEquals(uid, capturedProfile.uid)
        assertEquals(firstName, capturedProfile.firstName)
        assertEquals(lastName, capturedProfile.lastName)
        assertEquals(email, capturedProfile.email)
    }

    @Test
    fun testCreateAccountFirebaseFailure() {
        // Prepare test data
        val firstName = "John"
        val lastName = "Doe"
        val email = "john.doe@example.com"
        val password = "Password1"
        val birthDate = "01/01/1990"
        val exception = Exception("Firebase error")

        // Mock FirebaseAuth failure
        val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
        whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(authTaskCompletionSource.task)

        // Flags to check callbacks
        var successCalled = false
        var failureCalled = false

        // Invoke the function under test
        createAccountWithEmailAndPassword(
            firebaseAuth = firebaseAuth,
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            birthDate = birthDate,
            profileViewModel = profileViewModel,
            onSuccess = { successCalled = true },
            onFailure = { failureCalled = true }
        )

        // Simulate task failure
        authTaskCompletionSource.setException(exception)

        // Allow background tasks to complete
        shadowOf(Looper.getMainLooper()).idle()

        // Assertions
        assertFalse(successCalled)
        assertTrue(failureCalled)
    }

    @Test
    fun testCreateAccountProfileFailure() {
        // Prepare test data
        val firstName = "John"
        val lastName = "Doe"
        val email = "john.doe@example.com"
        val password = "Password1"
        val birthDate = "01/01/1990"
        val uid = "testUid"
        val exception = Exception("Profile creation failed")

        // Mock FirebaseAuth success
        val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
        whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(authTaskCompletionSource.task)
        whenever(authResult.user).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(uid)
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

        // Mock ProfileRepository behavior to fail
        whenever(profileRepository.addProfile(any(), any(), any())).thenAnswer { invocation ->
            val onFailureCallback = invocation.getArgument<(Exception) -> Unit>(2)
            onFailureCallback(exception)
            null
        }

        // Flags to check callbacks
        var successCalled = false
        var failureCalled = false

        // Invoke the function under test
        createAccountWithEmailAndPassword(
            firebaseAuth = firebaseAuth,
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            birthDate = birthDate,
            profileViewModel = profileViewModel,
            onSuccess = { successCalled = true },
            onFailure = { failureCalled = true }
        )

        // Simulate task completion
        authTaskCompletionSource.setResult(authResult)

        // Allow background tasks to complete
        shadowOf(Looper.getMainLooper()).idle()

        // Assertions
        assertFalse(successCalled)
        assertTrue(failureCalled)
    }

    @Test
    fun testCreateAccountWithInvalidBirthDate() {
        // Prepare test data
        val firstName = "Tom"
        val lastName = "Brown"
        val email = "tom.brown@example.com"
        val password = "Password456"
        val birthDate = "invalid date"
        val uid = "testUid"

        // Mock FirebaseAuth behavior
        val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
        whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(authTaskCompletionSource.task)
        whenever(authResult.user).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(uid)
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

        // Mock ProfileRepository behavior to succeed
        whenever(profileRepository.addProfile(any(), any(), any())).thenAnswer { invocation ->
            val onSuccessCallback = invocation.getArgument<() -> Unit>(1)
            onSuccessCallback()
            null
        }

        // Flags to check callbacks
        var successCalled = false
        var failureCalled = false

        // Invoke the function under test
        createAccountWithEmailAndPassword(
            firebaseAuth = firebaseAuth,
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            birthDate = birthDate,
            profileViewModel = profileViewModel,
            onSuccess = { successCalled = true },
            onFailure = { failureCalled = true }
        )

        // Simulate task completion
        authTaskCompletionSource.setResult(authResult)

        // Allow background tasks to complete
        shadowOf(Looper.getMainLooper()).idle()

        // Assertions
        assertFalse(successCalled)
        assertTrue(failureCalled)
    }

    @Test
    fun testCreateAccountWithNullCallbacks() {
        // Prepare test data
        val firstName = "Alice"
        val lastName = "Johnson"
        val email = "alice.johnson@example.com"
        val password = "Password789"
        val birthDate = "03/03/1993"
        val uid = "testUid"

        // Mock FirebaseAuth behavior
        val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
        whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(authTaskCompletionSource.task)
        whenever(authResult.user).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(uid)
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

        // Invoke the function under test without callbacks
        createAccountWithEmailAndPassword(
            firebaseAuth = firebaseAuth,
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            birthDate = birthDate,
            profileViewModel = profileViewModel,
            onSuccess = {},
            onFailure = {}
        )

        // Simulate task completion
        authTaskCompletionSource.setResult(authResult)

        // Allow background tasks to complete
        shadowOf(Looper.getMainLooper()).idle()

        // Since callbacks are empty, we just verify that no exceptions are thrown
    }

    @Test
    fun testCreateAccountWithInvalidEmail() {
        // Prepare test data
        val firstName = "Noah"
        val lastName = "Anderson"
        val email = "invalid-email"
        val password = "Password444"
        val birthDate = "08/08/1998"

        // Mock FirebaseAuth failure due to invalid email
        val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
        val exception = Exception("Invalid email format")
        whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(authTaskCompletionSource.task)

        // Flags to check callbacks
        var successCalled = false
        var failureCalled = false

        // Invoke the function under test
        createAccountWithEmailAndPassword(
            firebaseAuth = firebaseAuth,
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            birthDate = birthDate,
            profileViewModel = profileViewModel,
            onSuccess = { successCalled = true },
            onFailure = { failureCalled = true }
        )

        // Simulate task failure
        authTaskCompletionSource.setException(exception)

        // Allow background tasks to complete
        shadowOf(Looper.getMainLooper()).idle()

        // Assertions
        assertFalse(successCalled)
        assertTrue(failureCalled)
    }

    @Test
    fun testCreateAccountWhenUserAlreadyExists() {
        // Prepare test data
        val firstName = "Olivia"
        val lastName = "Thomas"
        val email = "olivia.thomas@example.com"
        val password = "Password555"
        val birthDate = "09/09/1999"
        val exception = Exception("User already exists")

        // Mock FirebaseAuth failure due to user already existing
        val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
        whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(authTaskCompletionSource.task)

        // Flags to check callbacks
        var successCalled = false
        var failureCalled = false

        // Invoke the function under test
        createAccountWithEmailAndPassword(
            firebaseAuth = firebaseAuth,
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            birthDate = birthDate,
            profileViewModel = profileViewModel,
            onSuccess = { successCalled = true },
            onFailure = { failureCalled = true }
        )

        // Simulate task failure
        authTaskCompletionSource.setException(exception)

        // Allow background tasks to complete
        shadowOf(Looper.getMainLooper()).idle()

        // Assertions
        assertFalse(successCalled)
        assertTrue(failureCalled)
    }
}