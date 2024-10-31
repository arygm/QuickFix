package com.arygm.quickfix.model.profile

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.arygm.quickfix.model.Location.Location
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import junit.framework.TestCase.fail
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class WorkerProfileRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockDocumentReference: DocumentReference

  @Mock private lateinit var mockCollectionReference: CollectionReference

  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  @Mock private lateinit var mockProfileQuerySnapshot: QuerySnapshot

  @Mock private lateinit var mockQuery: Query

  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>

  private lateinit var profileRepositoryFirestore: WorkerProfileRepositoryFirestore

  private val profile =
      WorkerProfile(
          uid = "1",
          fieldOfWork = "Plumber",
          hourlyRate = 50.0,
          description = "Experienced plumber with 10 years in the field.",
          location = Location(latitude = 37.7749, longitude = -122.4194, name = "Home"))

  private val profile2 =
      WorkerProfile(
          uid = "2",
          fieldOfWork = "Electrician",
          hourlyRate = 60.0,
          description = "Certified electrician specializing in residential projects.",
          location = Location(latitude = 34.0522, longitude = -118.2437, name = "Work"))

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

    profileRepositoryFirestore = WorkerProfileRepositoryFirestore(mockFirestore)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockProfileQuerySnapshot))

    `when`(mockQuery.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereLessThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
  }

  @After
  fun tearDown() {
    // Close the static mock
    firebaseAuthMockedStatic.close()
  }

  // ----- CRUD Operation Tests -----

  @Test
  fun getProfiles_callsDocuments() {
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockProfileQuerySnapshot))
    `when`(mockProfileQuerySnapshot.documents).thenReturn(listOf())

    profileRepositoryFirestore.getProfiles(
        onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    verify(mockCollectionReference).get()
  }

  @Test
  fun addProfile_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any<Map<String, Any?>>())).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.addProfile(profile, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun addProfile_whenSuccess_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.set(any<Map<String, Any?>>()))
        .thenReturn(taskCompletionSource.task)

    var callbackCalled = false

    profileRepositoryFirestore.addProfile(
        profile = profile,
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(null)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun addProfile_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.set(any<Map<String, Any?>>()))
        .thenReturn(taskCompletionSource.task)

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

    assertTrue(callbackCalled)
    assertEquals(exception, returnedException)
  }

  @Test
  fun updateProfile_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any<Map<String, Any?>>())).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.updateProfile(profile, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun updateProfile_whenSuccess_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.set(any<Map<String, Any?>>()))
        .thenReturn(taskCompletionSource.task)

    var callbackCalled = false

    profileRepositoryFirestore.updateProfile(
        profile = profile,
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(null)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun updateProfile_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.set(any<Map<String, Any?>>()))
        .thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    profileRepositoryFirestore.updateProfile(
        profile = profile,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    taskCompletionSource.setException(exception)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
    assertEquals(exception, returnedException)
  }

  @Test
  fun deleteProfileById_shouldCallDocumentReferenceDelete() {
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.deleteProfileById("1", onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).delete()
  }

  @Test
  fun deleteProfileById_whenSuccess_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.delete()).thenReturn(taskCompletionSource.task)

    var callbackCalled = false

    profileRepositoryFirestore.deleteProfileById(
        id = "1",
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(null)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun deleteProfileById_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.delete()).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    profileRepositoryFirestore.deleteProfileById(
        id = "1",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    taskCompletionSource.setException(exception)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
    assertEquals(exception, returnedException)
  }

  // ----- getProfileById Tests -----

  @Test
  fun getProfileById_whenDocumentExists_callsOnSuccessWithWorkerProfile() {
    val uid = "1"

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)

    // Mocking the data returned from Firestore
    `when`(mockDocumentSnapshot.id).thenReturn(profile.uid)
    `when`(mockDocumentSnapshot.getString("description")).thenReturn(profile.description)
    `when`(mockDocumentSnapshot.getString("fieldOfWork")).thenReturn(profile.fieldOfWork)
    `when`(mockDocumentSnapshot.getDouble("hourlyRate")).thenReturn(profile.hourlyRate)
    `when`(mockDocumentSnapshot.get("location"))
        .thenReturn(
            mapOf(
                "latitude" to profile.location!!.latitude,
                "longitude" to profile.location!!.longitude,
                "name" to profile.location!!.name))

    var callbackCalled = false

    profileRepositoryFirestore.getProfileById(
        uid = uid,
        onSuccess = { foundProfile ->
          callbackCalled = true
          assertEquals(profile, foundProfile)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
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
          assertNull(foundProfile)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
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
          assertEquals(exception, e)
        })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(failureCallbackCalled)
  }

  // ----- getProfiles Tests -----

  @Test
  fun getProfiles_whenSuccess_callsOnSuccessWithWorkerProfiles() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockCollectionReference.get()).thenReturn(taskCompletionSource.task)

    val document1 = Mockito.mock(DocumentSnapshot::class.java)
    val document2 = Mockito.mock(DocumentSnapshot::class.java)

    val documents = listOf(document1, document2)
    `when`(mockProfileQuerySnapshot.documents).thenReturn(documents)

    // Mock data for first document
    `when`(document1.id).thenReturn(profile.uid)
    `when`(document1.getString("description")).thenReturn(profile.description)
    `when`(document1.getString("fieldOfWork")).thenReturn(profile.fieldOfWork)
    `when`(document1.getDouble("hourlyRate")).thenReturn(profile.hourlyRate)
    `when`(document1.get("location"))
        .thenReturn(
            mapOf(
                "latitude" to profile.location!!.latitude,
                "longitude" to profile.location!!.longitude,
                "name" to profile.location!!.name))

    // Mock data for second document
    `when`(document2.id).thenReturn(profile2.uid)
    `when`(document2.getString("description")).thenReturn(profile2.description)
    `when`(document2.getString("fieldOfWork")).thenReturn(profile2.fieldOfWork)
    `when`(document2.getDouble("hourlyRate")).thenReturn(profile2.hourlyRate)
    `when`(document2.get("location"))
        .thenReturn(
            mapOf(
                "latitude" to profile2.location!!.latitude,
                "longitude" to profile2.location!!.longitude,
                "name" to profile2.location!!.name))

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

    assertTrue(callbackCalled)
    assertNotNull(returnedProfiles)
    assertEquals(2, returnedProfiles!!.size)
    assertEquals(profile, returnedProfiles!![0])
    assertEquals(profile2, returnedProfiles!![1])
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

    assertTrue(callbackCalled)
    assertEquals(exception, returnedException)
  }

  // ----- documentToWorker Tests -----

  @Test
  fun documentToWorker_whenAllFieldsArePresent_returnsWorkerProfile() {
    // Arrange
    val document = Mockito.mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(profile.uid)
    `when`(document.getString("description")).thenReturn(profile.description)
    `when`(document.getString("fieldOfWork")).thenReturn(profile.fieldOfWork)
    `when`(document.getDouble("hourlyRate")).thenReturn(profile.hourlyRate)
    `when`(document.get("location"))
        .thenReturn(
            mapOf(
                "latitude" to profile.location!!.latitude,
                "longitude" to profile.location!!.longitude,
                "name" to profile.location!!.name))

    // Act
    val result = invokeDocumentToWorker(document)

    // Assert
    assertNotNull(result)
    assertEquals(profile, result)
  }

  @Test
  fun documentToWorker_whenEssentialFieldsAreMissing_returnsNull() {
    // Arrange
    val document = Mockito.mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(profile.uid)
    // Missing "description", "fieldOfWork", "hourlyRate"
    `when`(document.getString("description")).thenReturn(null)
    `when`(document.getString("fieldOfWork")).thenReturn(null)
    `when`(document.getDouble("hourlyRate")).thenReturn(null)
    `when`(document.get("location"))
        .thenReturn(
            mapOf(
                "latitude" to profile.location!!.latitude,
                "longitude" to profile.location!!.longitude,
                "name" to profile.location!!.name))

    // Act
    val result = invokeDocumentToWorker(document)

    // Assert
    assertNull(result)
  }

  @Test
  fun documentToWorker_whenInvalidDataType_returnsNull() {
    // Arrange
    val document = Mockito.mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(profile.uid)
    `when`(document.getString("description")).thenReturn(profile.description)
    `when`(document.getString("fieldOfWork")).thenReturn(profile.fieldOfWork)
    `when`(document.getDouble("hourlyRate")).thenReturn(profile.hourlyRate)
    // "location" field has invalid data type (not a map)
    `when`(document.get("location")).thenReturn("Invalid data type")

    // Act
    val result = invokeDocumentToWorker(document)

    // Assert
    assertNull(result)
  }

  @Test
  fun documentToWorker_whenExceptionOccurs_returnsNull() {
    // Arrange
    val document = Mockito.mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(profile.uid)
    `when`(document.getString("description")).thenThrow(RuntimeException("Test exception"))

    // Act
    val result = invokeDocumentToWorker(document)

    // Assert
    assertNull(result)
  }

  // ----- Helper Method for Testing Private Method -----

  /** Uses reflection to invoke the private `documentToWorker` method. */
  private fun invokeDocumentToWorker(document: DocumentSnapshot): WorkerProfile? {
    val method =
        WorkerProfileRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToWorker", DocumentSnapshot::class.java)
    method.isAccessible = true
    return method.invoke(profileRepositoryFirestore, document) as WorkerProfile?
  }

  // ----- Init Method Tests -----

  @Test
  fun init_whenCurrentUserNotNull_callsOnSuccess() {
    val authStateListenerCaptor = argumentCaptor<FirebaseAuth.AuthStateListener>()
    val mockFirebaseUser = Mockito.mock(FirebaseUser::class.java)

    doNothing().`when`(mockFirebaseAuth).addAuthStateListener(authStateListenerCaptor.capture())
    `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)

    var callbackCalled = false

    profileRepositoryFirestore.init(onSuccess = { callbackCalled = true })

    // Simulate auth state change
    authStateListenerCaptor.firstValue.onAuthStateChanged(mockFirebaseAuth)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun init_whenCurrentUserIsNull_doesNotCallOnSuccess() {
    val authStateListenerCaptor = argumentCaptor<FirebaseAuth.AuthStateListener>()

    doNothing().`when`(mockFirebaseAuth).addAuthStateListener(authStateListenerCaptor.capture())
    `when`(mockFirebaseAuth.currentUser).thenReturn(null)

    var callbackCalled = false

    profileRepositoryFirestore.init(onSuccess = { callbackCalled = true })

    // Simulate auth state change
    authStateListenerCaptor.firstValue.onAuthStateChanged(mockFirebaseAuth)

    shadowOf(Looper.getMainLooper()).idle()

    assertFalse(callbackCalled)
  }

  //  @Test
  //  fun profileExists_whenProfileExists_callsOnSuccessWithTrueAndProfile() {
  //    val email = "john.doe@example.com"
  //
  //    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockProfileQuerySnapshot))
  //    `when`(mockProfileQuerySnapshot.isEmpty).thenReturn(false)
  //    `when`(mockProfileQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
  //
  //    `when`(mockDocumentSnapshot.id).thenReturn(profile.uid)
  //    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(profile.firstName)
  //    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(profile.lastName)
  //    `when`(mockDocumentSnapshot.getString("email")).thenReturn(profile.email)
  //    `when`(mockDocumentSnapshot.getTimestamp("birthDate")).thenReturn(profile.birthDate)
  //    `when`(mockDocumentSnapshot.getGeoPoint("location")).thenReturn(profile.location)
  //    `when`(mockDocumentSnapshot.getString("fieldOfWork")).thenReturn(profile.fieldOfWork)
  //    `when`(mockDocumentSnapshot.getString("description")).thenReturn(profile.description)
  //    `when`(mockDocumentSnapshot.getDouble("hourlyRate")).thenReturn(profile.hourlyRate)
  //
  //    var callbackCalled = false
  //
  //    profileRepositoryFirestore.profileExists(
  //        email = email,
  //        onSuccess = { (exists, foundProfile) ->
  //          callbackCalled = true
  //          assert(exists)
  //          assert(foundProfile == profile)
  //        },
  //        onFailure = { fail("Failure callback should not be called") })
  //
  //    shadowOf(Looper.getMainLooper()).idle()
  //
  //    assert(callbackCalled)
  //  }
  //
  //  @Test
  //  fun profileExists_whenProfileDoesNotExist_callsOnSuccessWithFalseAndNull() {
  //    val email = "unknown@example.com"
  //
  //    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockProfileQuerySnapshot))
  //    `when`(mockProfileQuerySnapshot.isEmpty).thenReturn(true)
  //
  //    var callbackCalled = false
  //
  //    profileRepositoryFirestore.profileExists(
  //        email = email,
  //        onSuccess = { (exists, foundProfile) ->
  //          callbackCalled = true
  //          assert(!exists)
  //          assert(foundProfile == null)
  //        },
  //        onFailure = { fail("Failure callback should not be called") })
  //
  //    shadowOf(Looper.getMainLooper()).idle()
  //
  //    assert(callbackCalled)
  //  }
  //
  //  @Test
  //  fun profileExists_whenFailure_callsOnFailure() {
  //    val email = "john.doe@example.com"
  //    val exception = Exception("Test exception")
  //
  //    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))
  //
  //    var failureCallbackCalled = false
  //
  //    profileRepositoryFirestore.profileExists(
  //        email = email,
  //        onSuccess = { fail("Success callback should not be called") },
  //        onFailure = { e ->
  //          failureCallbackCalled = true
  //          assert(e == exception)
  //        })
  //
  //    shadowOf(Looper.getMainLooper()).idle()
  //
  //    assert(failureCallbackCalled)
  //  }

  //  @Test
  //  fun filterWorkers_withFieldOfWork_callsOnSuccess() {
  //    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
  //    `when`(mockQuery.get()).thenReturn(taskCompletionSource.task)
  //
  //    // Mock query result to return one worker profile
  //    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
  //    `when`(mockDocumentSnapshot.toObject(Profile::class.java)).thenReturn(profile)
  //
  //    var callbackCalled = false
  //    var returnedProfiles: List<Profile>? = null
  //
  //    profileRepositoryFirestore.filterWorkers(
  //        fieldOfWork = "Plumber",
  //        hourlyRateThreshold = null,
  //        onSuccess = { profiles ->
  //          callbackCalled = true
  //          returnedProfiles = profiles
  //        },
  //        onFailure = { fail("Failure callback should not be called") })
  //
  //    // Simulate Firestore success
  //    taskCompletionSource.setResult(mockQuerySnapshot)
  //    shadowOf(Looper.getMainLooper()).idle()
  //
  //    // Assert that the success callback was called and the profile matches
  //    assert(callbackCalled)
  //    assert(returnedProfiles != null)
  //    assert(returnedProfiles!!.size == 1)
  //    assert(returnedProfiles!![0] == profile)
  //  }
  //
  //  @Test
  //  fun filterWorkers_withHourlyRateThreshold_callsOnSuccess() {
  //    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
  //    `when`(mockQuery.get()).thenReturn(taskCompletionSource.task)
  //
  //    // Mock query result to return one worker profile
  //    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
  //    `when`(mockDocumentSnapshot.toObject(Profile::class.java)).thenReturn(profile)
  //
  //    var callbackCalled = false
  //    var returnedProfiles: List<Profile>? = null
  //
  //    profileRepositoryFirestore.filterWorkers(
  //        hourlyRateThreshold = 30.0,
  //        fieldOfWork = null,
  //        onSuccess = { profiles ->
  //          callbackCalled = true
  //          returnedProfiles = profiles
  //        },
  //        onFailure = { fail("Failure callback should not be called") })
  //
  //    // Simulate Firestore success
  //    taskCompletionSource.setResult(mockQuerySnapshot)
  //    shadowOf(Looper.getMainLooper()).idle()
  //
  //    // Assert that the success callback was called and the profile matches
  //    assert(callbackCalled)
  //    assert(returnedProfiles != null)
  //    assert(returnedProfiles!!.size == 1)
  //    assert(returnedProfiles!![0] == profile)
  //  }
  //
  //  @Test
  //  fun filterWorkers_withFieldOfWorkAndHourlyRateThreshold_callsOnSuccess() {
  //    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
  //    `when`(mockQuery.get()).thenReturn(taskCompletionSource.task)
  //
  //    // Mock query result to return one worker profile
  //    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
  //    `when`(mockDocumentSnapshot.toObject(Profile::class.java)).thenReturn(profile)
  //
  //    var callbackCalled = false
  //    var returnedProfiles: List<Profile>? = null
  //
  //    profileRepositoryFirestore.filterWorkers(
  //        hourlyRateThreshold = 30.0,
  //        fieldOfWork = "Plumber",
  //        onSuccess = { profiles ->
  //          callbackCalled = true
  //          returnedProfiles = profiles
  //        },
  //        onFailure = { fail("Failure callback should not be called") })
  //
  //    // Simulate Firestore success
  //    taskCompletionSource.setResult(mockQuerySnapshot)
  //    shadowOf(Looper.getMainLooper()).idle()
  //
  //    // Assert that the success callback was called and the profile matches
  //    assert(callbackCalled)
  //    assert(returnedProfiles != null)
  //    assert(returnedProfiles!!.size == 1)
  //    assert(returnedProfiles!![0] == profile)
  //  }
  //
  //  @Test
  //  fun filterWorkers_onFailure_callsOnFailure() {
  //    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
  //    `when`(mockQuery.get()).thenReturn(taskCompletionSource.task)
  //
  //    val exception = Exception("Test exception")
  //    var callbackCalled = false
  //    var returnedException: Exception? = null
  //
  //    profileRepositoryFirestore.filterWorkers(
  //        hourlyRateThreshold = 30.0,
  //        fieldOfWork = "Plumber",
  //        onSuccess = { fail("Success callback should not be called") },
  //        onFailure = { e ->
  //          callbackCalled = true
  //          returnedException = e
  //        })
  //
  //    // Simulate Firestore failure
  //    taskCompletionSource.setException(exception)
  //    shadowOf(Looper.getMainLooper()).idle()
  //
  //    // Assert that the failure callback was called and the exception matches
  //    assert(callbackCalled)
  //    assert(returnedException == exception)
  //  }
}
