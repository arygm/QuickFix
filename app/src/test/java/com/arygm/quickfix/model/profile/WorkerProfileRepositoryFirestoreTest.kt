package com.arygm.quickfix.model.profile

import android.graphics.Bitmap
import android.net.Uri
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.profile.dataFields.Review
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalTime
import junit.framework.TestCase.fail
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.any
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever
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

  // Mocks for FirebaseStorage
  @Mock private lateinit var mockStorage: FirebaseStorage
  @Mock private lateinit var storageRef: StorageReference
  @Mock private lateinit var storageRef1: StorageReference
  @Mock private lateinit var storageRef2: StorageReference
  @Mock private lateinit var workerProfileFolderRef: StorageReference

  private lateinit var mockImageRef: StorageReference
  private lateinit var repository: WorkerProfileRepositoryFirestore

  private lateinit var profileRepositoryFirestore: WorkerProfileRepositoryFirestore

  private val profile =
      WorkerProfile(
          uid = "1",
          fieldOfWork = "Plumber",
          price = 50.0,
          description = "Experienced plumber with 10 years in the field.",
          location = Location(latitude = 37.7749, longitude = -122.4194, name = "Home"),
          displayName = "John Doe",
          reviews =
              ArrayDeque(
                  listOf(
                      Review("ramy", "great job", 4.5),
                      Review("moha", "Highly Recommended!", 3.0))),
          includedServices = listOf(IncludedService("Service 1"), IncludedService("Service 2")),
          addOnServices = listOf(AddOnService("Service 3"), AddOnService("Service 4")),
          bannerPicture = "bannerPicture",
          profilePicture = "profilePicture",
          workingHours = Pair(LocalTime.now(), LocalTime.now()),
          unavailability_list = listOf(LocalDate.now(), LocalDate.now()),
          tags = listOf("tag1", "tag2"),
          quickFixes = listOf("quickFix1", "quickFix2"),
      )

  private val profile2 =
      WorkerProfile(
          uid = "2",
          fieldOfWork = "Electrician",
          price = 60.0,
          description = "Certified electrician specializing in residential projects.",
          location = Location(latitude = 34.0522, longitude = -118.2437, name = "Work"),
          displayName = "John Doe",
          reviews =
              ArrayDeque(
                  listOf(
                      Review("ramy", "great job", 4.5),
                      Review("moha", "Highly Recommended!", 3.0))),
          includedServices = listOf(IncludedService("Service 1"), IncludedService("Service 2")),
          addOnServices = listOf(AddOnService("Service 3"), AddOnService("Service 4")),
          bannerPicture = "bannerPicture",
          profilePicture = "profilePicture",
          workingHours = Pair(LocalTime.now(), LocalTime.now()),
          unavailability_list = listOf(LocalDate.now(), LocalDate.now()),
          tags = listOf("tag1", "tag2"),
          quickFixes = listOf("quickFix1", "quickFix2"),
      )

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    firebaseAuthMockedStatic = mockStatic(FirebaseAuth::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    mockStorage = mock(FirebaseStorage::class.java)
    mockFirestore = mock(FirebaseFirestore::class.java)
    // Mock FirebaseAuth.getInstance() to return the mockFirebaseAuth
    firebaseAuthMockedStatic
        .`when`<FirebaseAuth> { FirebaseAuth.getInstance() }
        .thenReturn(mockFirebaseAuth)

    mockCollectionReference = mock(CollectionReference::class.java)
    mockDocumentReference = mock(DocumentReference::class.java)

    whenever(mockStorage.reference).thenReturn(storageRef)
    whenever(storageRef.child(anyString())).thenReturn(storageRef1)
    whenever(storageRef1.child(anyString())).thenReturn(storageRef2)
    whenever(storageRef2.child(anyString())).thenReturn(workerProfileFolderRef)

    profileRepositoryFirestore = WorkerProfileRepositoryFirestore(mockFirestore, mockStorage)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)

    mockImageRef = mock(StorageReference::class.java)
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
    `when`(document.getString("displayName")).thenReturn(profile.displayName)
    `when`(document.get("includedServices"))
        .thenReturn(
            listOf(
                mapOf("name" to profile.includedServices[0].name),
                mapOf("name" to profile.includedServices[0].name)))
    `when`(document.get("addOnServices"))
        .thenReturn(
            listOf(
                mapOf("name" to profile.addOnServices[0].name),
                mapOf("name" to profile.addOnServices[0].name)))
    `when`(document.getString("bannerImageUrl")).thenReturn(profile.bannerPicture)
    `when`(document.getString("profileImageUrl")).thenReturn(profile.profilePicture)
    `when`(document.get("workingHours"))
        .thenReturn(
            mapOf(
                "start" to profile.workingHours.first.toString(),
                "end" to profile.workingHours.second.toString()))
    `when`(document.get("unavailability_list"))
        .thenReturn(
            listOf(
                profile.unavailability_list[0].toString(),
                profile.unavailability_list[1].toString()))
    `when`(document.get("tags")).thenReturn(profile.tags)
    `when`(document.get("quickFixes")).thenReturn(profile.quickFixes)
    `when`(document.get("reviews"))
        .thenReturn(
            listOf(
                mapOf(
                    "username" to profile.reviews[0].username,
                    "review" to profile.reviews[0].review,
                    "rating" to profile.reviews[0].rating),
                mapOf(
                    "username" to profile.reviews[1].username,
                    "review" to profile.reviews[1].review,
                    "rating" to profile.reviews[1].rating)))

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
  fun uploadWorkerProfileImages_success() {
    val accountId = "workerProfileId"
    val bitmaps = listOf(mock(Bitmap::class.java), mock(Bitmap::class.java))
    val expectedUrls = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")
    val baos = ByteArrayOutputStream()
    bitmaps.forEach { bitmap -> bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos) }
    val imageData = baos.toByteArray()

    // Mock storageRef.child("workerProfiles/$workerProfileId")
    `when`(storageRef.child("profiles").child(accountId).child("worker"))
        .thenReturn(workerProfileFolderRef)

    // For each image, when workerProfileFolderRef.child(anyString()) is called, return a new
    // fileRef
    val fileRef1 = mock(StorageReference::class.java)
    val fileRef2 = mock(StorageReference::class.java)
    val fileRefs = listOf(fileRef1, fileRef2)
    var fileRefIndex = 0
    `when`(workerProfileFolderRef.child(anyString())).thenAnswer { fileRefs[fileRefIndex++] }

    // Mock putBytes
    val mockUploadTask1 = mock(UploadTask::class.java)
    val mockUploadTask2 = mock(UploadTask::class.java)
    val mockUploadTasks = listOf(mockUploadTask1, mockUploadTask2)
    val imageDatas = listOf(imageData, imageData) // Assuming same data for simplicity

    // Mock fileRef.putBytes(imageData)
    `when`(fileRef1.putBytes(imageDatas[0])).thenReturn(mockUploadTask1)
    `when`(fileRef2.putBytes(imageDatas[1])).thenReturn(mockUploadTask2)

    // Mock mockUploadTask.addOnSuccessListener(...)
    mockUploadTasks.forEachIndexed { index, mockUploadTask ->
      `when`(mockUploadTask.addOnSuccessListener(org.mockito.kotlin.any())).thenAnswer { invocation
        ->
        val listener = invocation.getArgument<OnSuccessListener<UploadTask.TaskSnapshot>>(0)
        val taskSnapshot = mock(UploadTask.TaskSnapshot::class.java) // Mock the snapshot
        listener.onSuccess(taskSnapshot)
        mockUploadTask // continue the chain
      }
    }

    // Mock fileRef.downloadUrl
    `when`(fileRef1.downloadUrl).thenReturn(Tasks.forResult(Uri.parse(expectedUrls[0])))
    `when`(fileRef2.downloadUrl).thenReturn(Tasks.forResult(Uri.parse(expectedUrls[1])))

    // Act
    var resultUrls = listOf<String>()
    profileRepositoryFirestore.uploadProfileImages(
        accountId,
        bitmaps,
        onSuccess = {
          resultUrls = it
          assertEquals(expectedUrls, resultUrls)
        },
        onFailure = { fail("onFailure should not be called") })

    // Wait for tasks to complete
    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun uploadWorkerProfileImages_failure() {
    val accountId = "testWorkerProfileId"
    val bitmap1 = mock(Bitmap::class.java)
    val images = listOf(bitmap1)
    val exception = Exception("Upload failed")

    // Mock storageRef.child("workerProfiles/$workerProfileId")
    `when`(storageRef.child("profiles").child(accountId).child("worker"))
        .thenReturn(workerProfileFolderRef)
    // Mock fileRef
    val fileRef = mock(StorageReference::class.java)
    whenever(workerProfileFolderRef.child(anyString())).thenReturn(fileRef)

    // Mock putBytes
    val uploadTask = mock(UploadTask::class.java)
    whenever(fileRef.putBytes(org.mockito.kotlin.any())).thenReturn(uploadTask)

    // Mock uploadTask.addOnSuccessListener and addOnFailureListener
    whenever(uploadTask.addOnSuccessListener(org.mockito.kotlin.any())).thenReturn(uploadTask)
    whenever(uploadTask.addOnFailureListener(org.mockito.kotlin.any())).thenAnswer { invocation ->
      val listener = invocation.arguments[0] as OnFailureListener
      listener.onFailure(exception)
      uploadTask
    }

    // Act
    var onFailureCalled = false
    var exceptionReceived: Exception? = null
    profileRepositoryFirestore.uploadProfileImages(
        accountId,
        images,
        onSuccess = { fail("onSuccess should not be called when upload fails") },
        onFailure = { e ->
          onFailureCalled = true
          exceptionReceived = e
        })

    // Wait for tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assert
    assertTrue(onFailureCalled)
    assertEquals(exception, exceptionReceived)
  }
}
