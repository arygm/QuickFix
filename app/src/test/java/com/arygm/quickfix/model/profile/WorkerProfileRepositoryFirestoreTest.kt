package com.arygm.quickfix.model.profile

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.arygm.quickfix.model.locations.Location
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.FirebaseApp
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.any
import org.mockito.Mockito.anyDouble
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class WorkerProfileRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockDocumentReference: DocumentReference

  @Mock private lateinit var mockCollectionReference: CollectionReference

  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>

  private lateinit var profileRepositoryFirestore: WorkerProfileRepositoryFirestore

  private val profile =
      WorkerProfile(
          uid = "1",
          fieldOfWork = "Plumber",
          price = 50.0,
          description = "Experienced plumber with 10 years in the field.",
          location = Location(latitude = 37.7749, longitude = -122.4194, name = "Home"))

  private val profile2 =
      WorkerProfile(
          uid = "2",
          fieldOfWork = "Electrician",
          price = 60.0,
          description = "Certified electrician specializing in residential projects.",
          location = Location(latitude = 34.0522, longitude = -118.2437, name = "Work"))

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    firebaseAuthMockedStatic = mockStatic(FirebaseAuth::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)

    // Mock FirebaseAuth.getInstance() to return the mockFirebaseAuth
    firebaseAuthMockedStatic
        .`when`<FirebaseAuth> { FirebaseAuth.getInstance() }
        .thenReturn(mockFirebaseAuth)

    profileRepositoryFirestore = WorkerProfileRepositoryFirestore(mockFirestore)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
  }

  @After
  fun tearDown() {
    // Close the static mock
    firebaseAuthMockedStatic.close()
  }

  // ----- CRUD Operation Tests -----

  @Test
  fun getProfiles_callsDocuments() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockCollectionReference.get()).thenReturn(taskCompletionSource.task)
    `when`(mockQuerySnapshot.documents).thenReturn(listOf())

    profileRepositoryFirestore.getProfiles(
        onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(mockQuerySnapshot)
    shadowOf(Looper.getMainLooper()).idle()

    verify(mockCollectionReference).get()
  }

  @Test
  fun addProfile_shouldCallFirestoreCollection() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.set(any<Map<String, Any?>>()))
        .thenReturn(taskCompletionSource.task)

    profileRepositoryFirestore.addProfile(profile, onSuccess = {}, onFailure = {})

    taskCompletionSource.setResult(null)
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
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.set(any<Map<String, Any?>>()))
        .thenReturn(taskCompletionSource.task)

    profileRepositoryFirestore.updateProfile(profile, onSuccess = {}, onFailure = {})

    taskCompletionSource.setResult(null)
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
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.delete()).thenReturn(taskCompletionSource.task)

    profileRepositoryFirestore.deleteProfileById("1", onSuccess = {}, onFailure = {})

    taskCompletionSource.setResult(null)
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

    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    `when`(mockDocumentReference.get()).thenReturn(taskCompletionSource.task)
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)

    // Mocking the data returned from Firestore
    `when`(mockDocumentSnapshot.id).thenReturn(profile.uid)
    `when`(mockDocumentSnapshot.getString("rating")).thenReturn(profile.rating.toString())
    `when`(mockDocumentSnapshot.get("reviews")).thenReturn(profile.reviews)
    `when`(mockDocumentSnapshot.getString("description")).thenReturn(profile.description)
    `when`(mockDocumentSnapshot.getString("fieldOfWork")).thenReturn(profile.fieldOfWork)
    `when`(mockDocumentSnapshot.getDouble("price")).thenReturn(profile.price)
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

    taskCompletionSource.setResult(mockDocumentSnapshot)
    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun getProfileById_whenDocumentDoesNotExist_callsOnSuccessWithNull() {
    val uid = "nonexistent"

    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    `when`(mockDocumentReference.get()).thenReturn(taskCompletionSource.task)
    `when`(mockDocumentSnapshot.exists()).thenReturn(false)

    var callbackCalled = false

    profileRepositoryFirestore.getProfileById(
        uid = uid,
        onSuccess = { foundProfile ->
          callbackCalled = true
          assertNull(foundProfile)
        },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(mockDocumentSnapshot)
    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun getProfileById_whenFailure_callsOnFailure() {
    val uid = "1"
    val exception = Exception("Test exception")

    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    `when`(mockDocumentReference.get()).thenReturn(taskCompletionSource.task)

    var failureCallbackCalled = false

    profileRepositoryFirestore.getProfileById(
        uid = uid,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          failureCallbackCalled = true
          assertEquals(exception, e)
        })

    taskCompletionSource.setException(exception)
    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(failureCallbackCalled)
  }

  // ----- getProfiles Tests -----

  @Test
  fun getProfiles_whenSuccess_callsOnSuccessWithWorkerProfiles() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockCollectionReference.get()).thenReturn(taskCompletionSource.task)

    val document1 = mock(DocumentSnapshot::class.java)
    val document2 = mock(DocumentSnapshot::class.java)

    val documents = listOf(document1, document2)
    `when`(mockQuerySnapshot.documents).thenReturn(documents)

    // Mock data for first document
    `when`(document1.id).thenReturn(profile.uid)
    `when`(document1.getString("rating")).thenReturn(profile.rating.toString())
    `when`(document1.get("reviews")).thenReturn(profile.reviews)
    `when`(document1.getString("description")).thenReturn(profile.description)
    `when`(document1.getString("fieldOfWork")).thenReturn(profile.fieldOfWork)
    `when`(document1.getDouble("price")).thenReturn(profile.price)
    `when`(document1.get("location"))
        .thenReturn(
            mapOf(
                "latitude" to profile.location!!.latitude,
                "longitude" to profile.location!!.longitude,
                "name" to profile.location!!.name))

    // Mock data for second document
    `when`(document2.id).thenReturn(profile2.uid)
    `when`(document2.getString("rating")).thenReturn(profile.rating.toString())
    `when`(document2.get("reviews")).thenReturn(profile.reviews)
    `when`(document2.getString("description")).thenReturn(profile2.description)
    `when`(document2.getString("fieldOfWork")).thenReturn(profile2.fieldOfWork)
    `when`(document2.getDouble("price")).thenReturn(profile2.price)
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

    taskCompletionSource.setResult(mockQuerySnapshot)
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
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(profile.uid)
    `when`(document.getString("rating")).thenReturn(profile.rating.toString())
    `when`(document.get("reviews")).thenReturn(profile.reviews)
    `when`(document.getString("description")).thenReturn(profile.description)
    `when`(document.getString("fieldOfWork")).thenReturn(profile.fieldOfWork)
    `when`(document.getDouble("price")).thenReturn(profile.price)
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
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(profile.uid)
    // Missing "description", "fieldOfWork", "price"
    `when`(document.getString("description")).thenReturn(null)
    `when`(document.getString("fieldOfWork")).thenReturn(null)
    `when`(document.getDouble("price")).thenReturn(null)
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
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(profile.uid)
    `when`(document.getString("description")).thenReturn(profile.description)
    `when`(document.getString("fieldOfWork")).thenReturn(profile.fieldOfWork)
    `when`(document.getDouble("price")).thenReturn(profile.price)
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
    val document = mock(DocumentSnapshot::class.java)
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
    val mockFirebaseUser = mock(FirebaseUser::class.java)

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

  // ----- filterWorkers Tests -----

  @Test
  fun filterWorkers_withFieldOfWork_callsOnSuccess() {
    // Create mocks for the chained methods
    val mockQueryAfterFieldOfWork = mock(Query::class.java)

    // Mock method chaining
    `when`(mockCollectionReference.whereEqualTo(eq("fieldOfWork"), eq("Plumber")))
        .thenReturn(mockQueryAfterFieldOfWork)

    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockQueryAfterFieldOfWork.get()).thenReturn(taskCompletionSource.task)

    // Mock query result to return one worker profile
    // Mock query result to return one worker profile
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

    // Mock data for first document
    `when`(mockDocumentSnapshot.id).thenReturn(profile.uid)
    `when`(mockDocumentSnapshot.getString("rating")).thenReturn(profile.rating.toString())
    `when`(mockDocumentSnapshot.get("reviews")).thenReturn(profile.reviews)
    `when`(mockDocumentSnapshot.getString("description")).thenReturn(profile.description)
    `when`(mockDocumentSnapshot.getString("fieldOfWork")).thenReturn(profile.fieldOfWork)
    `when`(mockDocumentSnapshot.getDouble("price")).thenReturn(profile.price)
    `when`(mockDocumentSnapshot.get("location"))
        .thenReturn(
            mapOf(
                "latitude" to profile.location!!.latitude,
                "longitude" to profile.location!!.longitude,
                "name" to profile.location!!.name))

    var callbackCalled = false
    var returnedProfiles: List<WorkerProfile>? = null

    profileRepositoryFirestore.filterWorkers(
        rating = null,
        reviews = null,
        fieldOfWork = "Plumber",
        price = null,
        location = null,
        radiusInKm = null,
        onSuccess = { profiles ->
          callbackCalled = true
          returnedProfiles = profiles
        },
        onFailure = { fail("Failure callback should not be called") })

    // Simulate Firestore success
    taskCompletionSource.setResult(mockQuerySnapshot)
    shadowOf(Looper.getMainLooper()).idle()

    // Assert that the success callback was called and the profile matches
    assertTrue(callbackCalled)
    assertNotNull(returnedProfiles)
    assertEquals(1, returnedProfiles!!.size)
    assertEquals(profile, returnedProfiles!![0])
  }

  @Test
  fun filterWorkers_withHourlyRateThreshold_callsOnSuccess() {
    // Create mocks for the chained methods
    val mockQueryAfterHourlyRate = mock(Query::class.java)

    // Mock method chaining
    `when`(mockCollectionReference.whereLessThan(eq("price"), eq(30.0)))
        .thenReturn(mockQueryAfterHourlyRate)

    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockQueryAfterHourlyRate.get()).thenReturn(taskCompletionSource.task)

    // Mock query result to return one worker profile
    // Mock query result to return one worker profile
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

    // Mock data for first document
    `when`(mockDocumentSnapshot.id).thenReturn(profile.uid)
    `when`(mockDocumentSnapshot.getString("rating")).thenReturn(profile.rating.toString())
    `when`(mockDocumentSnapshot.get("reviews")).thenReturn(profile.reviews)
    `when`(mockDocumentSnapshot.getString("description")).thenReturn(profile.description)
    `when`(mockDocumentSnapshot.getString("fieldOfWork")).thenReturn(profile.fieldOfWork)
    `when`(mockDocumentSnapshot.getDouble("price")).thenReturn(profile.price)
    `when`(mockDocumentSnapshot.get("location"))
        .thenReturn(
            mapOf(
                "latitude" to profile.location!!.latitude,
                "longitude" to profile.location!!.longitude,
                "name" to profile.location!!.name))

    var callbackCalled = false
    var returnedProfiles: List<WorkerProfile>? = null

    profileRepositoryFirestore.filterWorkers(
        rating = null,
        reviews = null,
        price = 30.0,
        fieldOfWork = null,
        location = null,
        radiusInKm = null,
        onSuccess = { profiles ->
          callbackCalled = true
          returnedProfiles = profiles
        },
        onFailure = { fail("Failure callback should not be called") })

    // Simulate Firestore success
    taskCompletionSource.setResult(mockQuerySnapshot)
    shadowOf(Looper.getMainLooper()).idle()

    // Assert that the success callback was called and the profile matches
    assertTrue(callbackCalled)
    assertNotNull(returnedProfiles)
    assertEquals(1, returnedProfiles!!.size)
    assertEquals(profile, returnedProfiles!![0])
  }

  @Test
  fun filterWorkers_withFieldOfWorkAndHourlyRateThreshold_callsOnSuccess() {
    // Create mocks for the chained methods
    val mockQueryAfterFieldOfWork = mock(Query::class.java)
    val mockQueryAfterHourlyRate = mock(Query::class.java)

    // Mock method chaining
    `when`(mockCollectionReference.whereEqualTo(eq("fieldOfWork"), eq("Plumber")))
        .thenReturn(mockQueryAfterFieldOfWork)
    `when`(mockQueryAfterFieldOfWork.whereLessThan(eq("price"), eq(30.0)))
        .thenReturn(mockQueryAfterHourlyRate)

    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockQueryAfterHourlyRate.get()).thenReturn(taskCompletionSource.task)

    // Mock query result to return one worker profile
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

    // Mock data for first document
    `when`(mockDocumentSnapshot.id).thenReturn(profile.uid)
    `when`(mockDocumentSnapshot.getString("rating")).thenReturn(profile.rating.toString())
    `when`(mockDocumentSnapshot.get("reviews")).thenReturn(profile.reviews)
    `when`(mockDocumentSnapshot.getString("description")).thenReturn(profile.description)
    `when`(mockDocumentSnapshot.getString("fieldOfWork")).thenReturn(profile.fieldOfWork)
    `when`(mockDocumentSnapshot.getDouble("price")).thenReturn(profile.price)
    `when`(mockDocumentSnapshot.get("location"))
        .thenReturn(
            mapOf(
                "latitude" to profile.location!!.latitude,
                "longitude" to profile.location!!.longitude,
                "name" to profile.location!!.name))

    var callbackCalled = false
    var returnedProfiles: List<WorkerProfile>? = null

    profileRepositoryFirestore.filterWorkers(
        rating = null,
        reviews = null,
        price = 30.0,
        fieldOfWork = "Plumber",
        location = null,
        radiusInKm = null,
        onSuccess = { profiles ->
          callbackCalled = true
          returnedProfiles = profiles
        },
        onFailure = { fail("Failure callback should not be called") })

    // Simulate Firestore success
    taskCompletionSource.setResult(mockQuerySnapshot)
    shadowOf(Looper.getMainLooper()).idle()

    // Assert that the success callback was called and the profile matches
    assertTrue(callbackCalled)
    assertNotNull(returnedProfiles)
    assertEquals(1, returnedProfiles!!.size)
    assertEquals(profile, returnedProfiles!![0])
  }

  @Test
  fun filterWorkers_withLocationAndRadius_callsOnSuccess() {
    // Create mocks for the chained methods
    val mockQueryAfterLatitudeMin = mock(Query::class.java)
    val mockQueryAfterLatitudeMax = mock(Query::class.java)
    val mockQueryAfterLongitudeMin = mock(Query::class.java)
    val mockQueryAfterLongitudeMax = mock(Query::class.java)

    val location = Location(latitude = 37.7749, longitude = -122.4194, name = "Home")

    // Starting from the collection reference
    val query = mockCollectionReference as Query

    // Mock whereGreaterThanOrEqualTo for latitude
    `when`(query.whereGreaterThanOrEqualTo(eq("location.latitude"), anyDouble()))
        .thenReturn(mockQueryAfterLatitudeMin)

    // Mock whereLessThanOrEqualTo for latitude
    `when`(mockQueryAfterLatitudeMin.whereLessThanOrEqualTo(eq("location.latitude"), anyDouble()))
        .thenReturn(mockQueryAfterLatitudeMax)

    // Mock whereGreaterThanOrEqualTo for longitude
    `when`(
            mockQueryAfterLatitudeMax.whereGreaterThanOrEqualTo(
                eq("location.longitude"), anyDouble()))
        .thenReturn(mockQueryAfterLongitudeMin)

    // Mock whereLessThanOrEqualTo for longitude
    `when`(mockQueryAfterLongitudeMin.whereLessThanOrEqualTo(eq("location.longitude"), anyDouble()))
        .thenReturn(mockQueryAfterLongitudeMax)

    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockQueryAfterLongitudeMax.get()).thenReturn(taskCompletionSource.task)

    // Mock query result to return one worker profile
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

    // Mock data for first document
    `when`(mockDocumentSnapshot.id).thenReturn(profile.uid)
    `when`(mockDocumentSnapshot.getString("rating")).thenReturn(profile.rating.toString())
    `when`(mockDocumentSnapshot.get("reviews")).thenReturn(profile.reviews)
    `when`(mockDocumentSnapshot.getString("description")).thenReturn(profile.description)
    `when`(mockDocumentSnapshot.getString("fieldOfWork")).thenReturn(profile.fieldOfWork)
    `when`(mockDocumentSnapshot.get("location"))
        .thenReturn(
            mapOf(
                "latitude" to location.latitude,
                "longitude" to location.longitude,
                "name" to location.name))

    var callbackCalled = false
    var returnedProfiles: List<WorkerProfile>? = null

    profileRepositoryFirestore.filterWorkers(
        rating = null,
        reviews = null,
        price = null,
        fieldOfWork = null,
        location = location,
        radiusInKm = 50.0,
        onSuccess = { profiles ->
          callbackCalled = true
          returnedProfiles = profiles
        },
        onFailure = { fail("Failure callback should not be called") })

    // Simulate Firestore success
    taskCompletionSource.setResult(mockQuerySnapshot)
    shadowOf(Looper.getMainLooper()).idle()

    // Assert that the success callback was called and the profile matches
    assertTrue(callbackCalled)
    assertNotNull(returnedProfiles)
    assertEquals(1, returnedProfiles!!.size)
    assertEquals(profile, returnedProfiles!![0])
  }

  @Test
  fun filterWorkers_onFailure_callsOnFailure() {
    // Create mocks for the chained methods
    val mockQueryAfterFieldOfWork = mock(Query::class.java)
    val mockQueryAfterHourlyRate = mock(Query::class.java)

    // Mock method chaining
    `when`(mockCollectionReference.whereEqualTo(eq("fieldOfWork"), eq("Plumber")))
        .thenReturn(mockQueryAfterFieldOfWork)
    `when`(mockQueryAfterFieldOfWork.whereLessThan(eq("price"), eq(30.0)))
        .thenReturn(mockQueryAfterHourlyRate)

    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockQueryAfterHourlyRate.get()).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    profileRepositoryFirestore.filterWorkers(
        rating = null,
        reviews = null,
        price = 30.0,
        fieldOfWork = "Plumber",
        location = null,
        radiusInKm = null,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    // Simulate Firestore failure
    taskCompletionSource.setException(exception)
    shadowOf(Looper.getMainLooper()).idle()

    // Assert that the failure callback was called and the exception matches
    assertTrue(callbackCalled)
    assertEquals(exception, returnedException)
  }
}
