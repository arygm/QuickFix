package com.arygm.quickfix.model.profile

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.fail
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ProfileRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot
  @Mock private lateinit var mockProfileQuerySnapshot: QuerySnapshot
  @Mock private lateinit var mockQuery: Query

  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>

  private lateinit var profileRepositoryFirestore: ProfileRepositoryFirestore

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
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    firebaseAuthMockedStatic = Mockito.mockStatic(FirebaseAuth::class.java)
    mockFirebaseAuth = Mockito.mock(FirebaseAuth::class.java)

    // Mock FirebaseAuth.getInstance() to return the mockFirebaseAuth
    firebaseAuthMockedStatic
        .`when`<FirebaseAuth> { FirebaseAuth.getInstance() }
        .thenReturn(mockFirebaseAuth)

    profileRepositoryFirestore = ProfileRepositoryFirestore(mockFirestore)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockProfileQuerySnapshot))
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)

    `when`(mockQuery.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereLessThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
  }

  @Test
  fun getProfiles_callsDocuments() {
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockProfileQuerySnapshot))

    `when`(mockProfileQuerySnapshot.documents).thenReturn(listOf())

    profileRepositoryFirestore.getProfiles(
        onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    verify(timeout(100)) { (mockProfileQuerySnapshot).documents }
  }

  @Test
  fun addProfile_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.addProfile(profile, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun updateProfile_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.updateProfile(profile, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun deleteProfileById_shouldCallDocumentReferenceDelete() {
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.deleteProfileById("1", onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).delete()
  }

  @Test
  fun profileExists_whenProfileExists_callsOnSuccessWithTrueAndProfile() {
    val email = "john.doe@example.com"

    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockProfileQuerySnapshot))
    `when`(mockProfileQuerySnapshot.isEmpty).thenReturn(false)
    `when`(mockProfileQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

    `when`(mockDocumentSnapshot.id).thenReturn(profile.uid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(profile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(profile.lastName)
    `when`(mockDocumentSnapshot.getString("email")).thenReturn(profile.email)
    `when`(mockDocumentSnapshot.getTimestamp("birthDate")).thenReturn(profile.birthDate)
    `when`(mockDocumentSnapshot.getString("description")).thenReturn(profile.description)

    var callbackCalled = false

    profileRepositoryFirestore.profileExists(
        email = email,
        onSuccess = { (exists, foundProfile) ->
          callbackCalled = true
          assert(exists)
          assert(foundProfile == profile)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun profileExists_whenProfileDoesNotExist_callsOnSuccessWithFalseAndNull() {
    val email = "unknown@example.com"

    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockProfileQuerySnapshot))
    `when`(mockProfileQuerySnapshot.isEmpty).thenReturn(true)

    var callbackCalled = false

    profileRepositoryFirestore.profileExists(
        email = email,
        onSuccess = { (exists, foundProfile) ->
          callbackCalled = true
          assert(!exists)
          assert(foundProfile == null)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun profileExists_whenFailure_callsOnFailure() {
    val email = "john.doe@example.com"
    val exception = Exception("Test exception")

    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    var failureCallbackCalled = false

    profileRepositoryFirestore.profileExists(
        email = email,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          failureCallbackCalled = true
          assert(e == exception)
        })

    shadowOf(Looper.getMainLooper()).idle()

    assert(failureCallbackCalled)
  }

  @Test
  fun getProfileById_whenDocumentExists_callsOnSuccessWithProfile() {
    val uid = "1"

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.id).thenReturn(profile.uid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(profile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(profile.lastName)
    `when`(mockDocumentSnapshot.getString("email")).thenReturn(profile.email)
    `when`(mockDocumentSnapshot.getTimestamp("birthDate")).thenReturn(profile.birthDate)
    `when`(mockDocumentSnapshot.getString("description")).thenReturn(profile.description)

    var callbackCalled = false

    profileRepositoryFirestore.getProfileById(
        uid = uid,
        onSuccess = { foundProfile ->
          callbackCalled = true
          assert(foundProfile == profile)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun getProfileById_whenDocumentDoesNotExist_callsOnSuccessWithNull() {
    val uid = "nonexistent"

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(false)

    var callbackCalled = false

    profileRepositoryFirestore.getProfileById(
        uid = uid,
        onSuccess = { foundProfile ->
          callbackCalled = true
          assert(foundProfile == null)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun getProfileById_whenFailure_callsOnFailure() {
    val uid = "1"
    val exception = Exception("Test exception")

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    var failureCallbackCalled = false

    profileRepositoryFirestore.getProfileById(
        uid = uid,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          failureCallbackCalled = true
          assert(e == exception)
        })

    shadowOf(Looper.getMainLooper()).idle()

    assert(failureCallbackCalled)
  }

  @After
  fun tearDown() {
    // Close the static mock
    firebaseAuthMockedStatic.close()
  }

  @Test
  fun init_whenCurrentUserNotNull_callsOnSuccess() {
    val authStateListenerCaptor = argumentCaptor<FirebaseAuth.AuthStateListener>()
    val mockFirebaseUser = Mockito.mock(FirebaseUser::class.java)

    Mockito.doNothing()
        .`when`(mockFirebaseAuth)
        .addAuthStateListener(authStateListenerCaptor.capture())
    `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)

    var callbackCalled = false

    profileRepositoryFirestore.init(onSuccess = { callbackCalled = true })

    authStateListenerCaptor.firstValue.onAuthStateChanged(mockFirebaseAuth)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun init_whenCurrentUserIsNull_doesNotCallOnSuccess() {
    val authStateListenerCaptor = argumentCaptor<FirebaseAuth.AuthStateListener>()

    Mockito.doNothing()
        .`when`(mockFirebaseAuth)
        .addAuthStateListener(authStateListenerCaptor.capture())
    `when`(mockFirebaseAuth.currentUser).thenReturn(null)

    var callbackCalled = false

    profileRepositoryFirestore.init(onSuccess = { callbackCalled = true })

    authStateListenerCaptor.firstValue.onAuthStateChanged(mockFirebaseAuth)

    shadowOf(Looper.getMainLooper()).idle()

    assert(!callbackCalled)
  }

  @Test
  fun getProfiles_whenSuccess_callsOnSuccessWithProfiles() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockCollectionReference.get()).thenReturn(taskCompletionSource.task)

    val document1 = Mockito.mock(DocumentSnapshot::class.java)
    val document2 = Mockito.mock(DocumentSnapshot::class.java)

    val profile2 =
        Profile(
            uid = "2",
            firstName = "Jane",
            lastName = "Smith",
            email = "jane.smith@example.com",
            birthDate = Timestamp.now(),
            description = "Another description")

    val documents = listOf(document1, document2)
    `when`(mockProfileQuerySnapshot.documents).thenReturn(documents)

    `when`(document1.id).thenReturn(profile.uid)
    `when`(document1.getString("firstName")).thenReturn(profile.firstName)
    `when`(document1.getString("lastName")).thenReturn(profile.lastName)
    `when`(document1.getString("email")).thenReturn(profile.email)
    `when`(document1.getTimestamp("birthDate")).thenReturn(profile.birthDate)
    `when`(document1.getString("description")).thenReturn(profile.description)

    `when`(document2.id).thenReturn(profile2.uid)
    `when`(document2.getString("firstName")).thenReturn(profile2.firstName)
    `when`(document2.getString("lastName")).thenReturn(profile2.lastName)
    `when`(document2.getString("email")).thenReturn(profile2.email)
    `when`(document2.getTimestamp("birthDate")).thenReturn(profile2.birthDate)
    `when`(document2.getString("description")).thenReturn(profile2.description)

    var callbackCalled = false
    var returnedProfiles: List<Profile>? = null

    profileRepositoryFirestore.getProfiles(
        onSuccess = { profiles ->
          callbackCalled = true
          returnedProfiles = profiles
        },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(mockProfileQuerySnapshot)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
    assert(returnedProfiles != null)
    assert(returnedProfiles!!.size == 2)
    assert(returnedProfiles!![0] == profile)
    assert(returnedProfiles!![1] == profile2)
  }

  @Test
  fun getProfiles_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockCollectionReference.get()).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")

    var callbackCalled = false
    var returnedException: Exception? = null

    profileRepositoryFirestore.getProfiles(
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    taskCompletionSource.setException(exception)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
    assert(returnedException == exception)
  }

  @Test
  fun addProfile_whenSuccess_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.set(profile)).thenReturn(taskCompletionSource.task)

    var callbackCalled = false

    profileRepositoryFirestore.addProfile(
        profile = profile,
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(null)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun addProfile_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.set(profile)).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    profileRepositoryFirestore.addProfile(
        profile = profile,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    taskCompletionSource.setException(exception)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
    assert(returnedException == exception)
  }

  @Test
  fun documentToProfile_whenFieldsAreMissing_returnsNull() {
    val document = Mockito.mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(profile.uid)
    `when`(document.getString("lastName")).thenReturn(profile.lastName)
    `when`(document.getString("email")).thenReturn(profile.email)
    `when`(document.getTimestamp("birthDate")).thenReturn(profile.birthDate)
    `when`(document.getString("description")).thenReturn(profile.description)

    val result = invokeDocumentToProfile(document)

    assert(result == null)
  }

  @Test
  fun documentToProfile_whenExceptionOccurs_returnsNull() {
    val document = Mockito.mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(profile.uid)
    `when`(document.getString("firstName")).thenThrow(RuntimeException("Test exception"))
    `when`(document.getString("lastName")).thenReturn(profile.lastName)
    `when`(document.getString("email")).thenReturn(profile.email)
    `when`(document.getTimestamp("birthDate")).thenReturn(profile.birthDate)
    `when`(document.getString("description")).thenReturn(profile.description)

    val result = invokeDocumentToProfile(document)

    assert(result == null)
  }

  private fun invokeDocumentToProfile(document: DocumentSnapshot): Profile? {
    val method =
        ProfileRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true
    return method.invoke(profileRepositoryFirestore, document) as Profile?
  }

  @Test
  fun documentToProfile_whenInvalidDataType_returnsNull() {
    val document = Mockito.mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(profile.uid)
    `when`(document.getString("firstName")).thenReturn(profile.firstName)
    `when`(document.getString("lastName")).thenReturn(profile.lastName)
    `when`(document.getString("email")).thenReturn(profile.email)
    `when`(document.getTimestamp("birthDate")).thenReturn(null)
    `when`(document.getString("description")).thenReturn(profile.description)

    val result = invokeDocumentToProfile(document)

    assert(result == null)
  }

  @Test
  fun filterWorkers_withFieldOfWork_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockQuery.get()).thenReturn(taskCompletionSource.task)

    // Mock query result to return one worker profile
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Profile::class.java)).thenReturn(profile)

    var callbackCalled = false
    var returnedProfiles: List<Profile>? = null

    profileRepositoryFirestore.filterWorkers(
        fieldOfWork = "Plumber",
        hourlyRateThreshold = null,
        onSuccess = { profiles ->
          callbackCalled = true
          returnedProfiles = profiles
        },
        onFailure = { fail("Failure callback should not be called") })

    // Simulate Firestore success
    taskCompletionSource.setResult(mockQuerySnapshot)
    shadowOf(Looper.getMainLooper()).idle()

    // Assert that the success callback was called and the profile matches
    assert(callbackCalled)
    assert(returnedProfiles != null)
    assert(returnedProfiles!!.size == 1)
    assert(returnedProfiles!![0] == profile)
  }

  @Test
  fun filterWorkers_withHourlyRateThreshold_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockQuery.get()).thenReturn(taskCompletionSource.task)

    // Mock query result to return one worker profile
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Profile::class.java)).thenReturn(profile)

    var callbackCalled = false
    var returnedProfiles: List<Profile>? = null

    profileRepositoryFirestore.filterWorkers(
        hourlyRateThreshold = 30.0,
        fieldOfWork = null,
        onSuccess = { profiles ->
          callbackCalled = true
          returnedProfiles = profiles
        },
        onFailure = { fail("Failure callback should not be called") })

    // Simulate Firestore success
    taskCompletionSource.setResult(mockQuerySnapshot)
    shadowOf(Looper.getMainLooper()).idle()

    // Assert that the success callback was called and the profile matches
    assert(callbackCalled)
    assert(returnedProfiles != null)
    assert(returnedProfiles!!.size == 1)
    assert(returnedProfiles!![0] == profile)
  }

  @Test
  fun filterWorkers_withFieldOfWorkAndHourlyRateThreshold_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockQuery.get()).thenReturn(taskCompletionSource.task)

    // Mock query result to return one worker profile
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Profile::class.java)).thenReturn(profile)

    var callbackCalled = false
    var returnedProfiles: List<Profile>? = null

    profileRepositoryFirestore.filterWorkers(
        hourlyRateThreshold = 30.0,
        fieldOfWork = "Plumber",
        onSuccess = { profiles ->
          callbackCalled = true
          returnedProfiles = profiles
        },
        onFailure = { fail("Failure callback should not be called") })

    // Simulate Firestore success
    taskCompletionSource.setResult(mockQuerySnapshot)
    shadowOf(Looper.getMainLooper()).idle()

    // Assert that the success callback was called and the profile matches
    assert(callbackCalled)
    assert(returnedProfiles != null)
    assert(returnedProfiles!!.size == 1)
    assert(returnedProfiles!![0] == profile)
  }

  @Test
  fun filterWorkers_onFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockQuery.get()).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    profileRepositoryFirestore.filterWorkers(
        hourlyRateThreshold = 30.0,
        fieldOfWork = "Plumber",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    // Simulate Firestore failure
    taskCompletionSource.setException(exception)
    shadowOf(Looper.getMainLooper()).idle()

    // Assert that the failure callback was called and the exception matches
    assert(callbackCalled)
    assert(returnedException == exception)
  }
}
