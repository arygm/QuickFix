package com.arygm.quickfix.model.profile

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import junit.framework.TestCase.fail
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.eq
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class WorkerProfileRepositoryFirestoreImageFetchTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockStorage: FirebaseStorage
  @Mock private lateinit var mockStorageRef: StorageReference
  @Mock private lateinit var mockImageRef: StorageReference

  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>
  private lateinit var repository: WorkerProfileRepositoryFirestore

  private val accountId = "testAccountId"

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    firebaseAuthMockedStatic = Mockito.mockStatic(FirebaseAuth::class.java)
    mockFirebaseAuth = Mockito.mock(FirebaseAuth::class.java)
    firebaseAuthMockedStatic
        .`when`<FirebaseAuth> { FirebaseAuth.getInstance() }
        .thenReturn(mockFirebaseAuth)

    `when`(mockFirestore.collection(anyString())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)

    `when`(mockStorage.reference).thenReturn(mockStorageRef)

    repository = WorkerProfileRepositoryFirestore(mockFirestore, mockStorage)
  }

  @After
  fun tearDown() {
    firebaseAuthMockedStatic.close()
  }

  // Helper method to create a bitmap as returned data
  private fun createTestBitmap(): Bitmap {
    val width = 10
    val height = 10
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.eraseColor(Color.GREEN)
    return bitmap
  }

  // --- fetchProfileImageAsBitmap tests ---

  @Test
  fun fetchProfileImageAsBitmap_emptyUrl_returnsFallbackImage() {
    // Firestore returns empty URL
    val tcs = TaskCompletionSource<DocumentSnapshot>()
    `when`(mockDocumentReference.get()).thenReturn(tcs.task)
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.get("profileImageUrl")).thenReturn("")
    tcs.setResult(mockDocumentSnapshot)

    var onSuccessCalled = false
    repository.fetchProfileImageAsBitmap(
        accountId,
        onSuccess = { bitmap ->
          onSuccessCalled = true
          assertNotNull(bitmap)
        },
        onFailure = { fail("Should not fail") })

    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(onSuccessCalled)
  }

  @Test
  fun fetchProfileImageAsBitmap_urlContainsLocalEmulator_returnsFallbackImage() {
    // Firestore returns a URL containing "10.0.2.2:9199"
    val tcs = TaskCompletionSource<DocumentSnapshot>()
    `when`(mockDocumentReference.get()).thenReturn(tcs.task)
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.get("profileImageUrl"))
        .thenReturn("http://10.0.2.2:9199/someimage.jpg")
    tcs.setResult(mockDocumentSnapshot)

    var onSuccessCalled = false
    repository.fetchProfileImageAsBitmap(
        accountId,
        onSuccess = { bitmap ->
          onSuccessCalled = true
          assertNotNull(bitmap)
        },
        onFailure = { fail("Should not fail") })

    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(onSuccessCalled)
  }

  @Test
  fun fetchProfileImageAsBitmap_validUrl_fetchesFromStorageSuccess() {
    // Firestore returns a valid URL
    val imageUrl = "https://firebasestorage.googleapis.com/v0/b/test/o/someimage.jpg"
    val tcs = TaskCompletionSource<DocumentSnapshot>()
    `when`(mockDocumentReference.get()).thenReturn(tcs.task)
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.get("profileImageUrl")).thenReturn(imageUrl)
    tcs.setResult(mockDocumentSnapshot)

    // Mock storage fetch
    `when`(mockStorage.getReferenceFromUrl(eq(imageUrl))).thenReturn(mockImageRef)
    val testBitmap = createTestBitmap()
    val baos = ByteArrayOutputStream()
    testBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
    val bytes = baos.toByteArray()

    val tcsGetBytes = TaskCompletionSource<ByteArray>()
    `when`(mockImageRef.getBytes(Long.MAX_VALUE)).thenReturn(tcsGetBytes.task)

    var onSuccessCalled = false
    repository.fetchProfileImageAsBitmap(
        accountId,
        onSuccess = { bitmap ->
          onSuccessCalled = true
          assertNotNull(bitmap)
          // Check if bitmap matches what we "returned"
          // We won't decode it again here, but we can at least assert non-null
        },
        onFailure = { fail("Should not fail") })

    tcsGetBytes.setResult(bytes)
    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(onSuccessCalled)
  }

  @Test
  fun fetchProfileImageAsBitmap_validUrl_fetchesFromStorageFailure() {
    // Firestore returns a valid URL
    val imageUrl = "https://firebasestorage.googleapis.com/v0/b/test/o/someimage.jpg"
    val tcs = TaskCompletionSource<DocumentSnapshot>()
    `when`(mockDocumentReference.get()).thenReturn(tcs.task)
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.get("profileImageUrl")).thenReturn(imageUrl)
    tcs.setResult(mockDocumentSnapshot)

    // Mock storage fetch failure
    `when`(mockStorage.getReferenceFromUrl(eq(imageUrl))).thenReturn(mockImageRef)
    val tcsGetBytes = TaskCompletionSource<ByteArray>()
    `when`(mockImageRef.getBytes(Long.MAX_VALUE)).thenReturn(tcsGetBytes.task)

    var onFailureCalled = false
    repository.fetchProfileImageAsBitmap(
        accountId,
        onSuccess = { fail("Should not succeed") },
        onFailure = {
          onFailureCalled = true
          assertTrue(it is Exception)
        })

    tcsGetBytes.setException(Exception("Storage error"))
    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(onFailureCalled)
  }

  @Test
  fun fetchProfileImageAsBitmap_firestoreFailure() {
    // Firestore get fails
    val tcs = TaskCompletionSource<DocumentSnapshot>()
    `when`(mockDocumentReference.get()).thenReturn(tcs.task)

    var onFailureCalled = false
    repository.fetchProfileImageAsBitmap(
        accountId,
        onSuccess = { fail("Should not succeed") },
        onFailure = {
          onFailureCalled = true
          assertTrue(it is Exception)
        })

    tcs.setException(Exception("Firestore error"))
    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(onFailureCalled)
  }

  // --- fetchBannerImageAsBitmap tests ---

  @Test
  fun fetchBannerImageAsBitmap_emptyUrl_returnsFallbackImage() {
    val tcs = TaskCompletionSource<DocumentSnapshot>()
    `when`(mockDocumentReference.get()).thenReturn(tcs.task)
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.get("bannerImageUrl")).thenReturn("")
    tcs.setResult(mockDocumentSnapshot)

    var onSuccessCalled = false
    repository.fetchBannerImageAsBitmap(
        accountId,
        onSuccess = { bitmap ->
          onSuccessCalled = true
          assertNotNull(bitmap)
        },
        onFailure = { fail("Should not fail") })

    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(onSuccessCalled)
  }

  @Test
  fun fetchBannerImageAsBitmap_urlContainsLocalEmulator_returnsFallbackImage() {
    val tcs = TaskCompletionSource<DocumentSnapshot>()
    `when`(mockDocumentReference.get()).thenReturn(tcs.task)
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.get("bannerImageUrl")).thenReturn("http://10.0.2.2:9199/banner.jpg")
    tcs.setResult(mockDocumentSnapshot)

    var onSuccessCalled = false
    repository.fetchBannerImageAsBitmap(
        accountId,
        onSuccess = { bitmap ->
          onSuccessCalled = true
          assertNotNull(bitmap)
        },
        onFailure = { fail("Should not fail") })

    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(onSuccessCalled)
  }

  @Test
  fun fetchBannerImageAsBitmap_validUrl_fetchesFromStorageSuccess() {
    val imageUrl = "https://firebasestorage.googleapis.com/v0/b/test/o/banner.jpg"
    val tcs = TaskCompletionSource<DocumentSnapshot>()
    `when`(mockDocumentReference.get()).thenReturn(tcs.task)
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.get("bannerImageUrl")).thenReturn(imageUrl)
    tcs.setResult(mockDocumentSnapshot)

    `when`(mockStorage.getReferenceFromUrl(eq(imageUrl))).thenReturn(mockImageRef)
    val testBitmap = createTestBitmap()
    val baos = ByteArrayOutputStream()
    testBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
    val bytes = baos.toByteArray()

    val tcsGetBytes = TaskCompletionSource<ByteArray>()
    `when`(mockImageRef.getBytes(Long.MAX_VALUE)).thenReturn(tcsGetBytes.task)

    var onSuccessCalled = false
    repository.fetchBannerImageAsBitmap(
        accountId,
        onSuccess = { bitmap ->
          onSuccessCalled = true
          assertNotNull(bitmap)
        },
        onFailure = { fail("Should not fail") })

    tcsGetBytes.setResult(bytes)
    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(onSuccessCalled)
  }

  @Test
  fun fetchBannerImageAsBitmap_validUrl_fetchesFromStorageFailure() {
    val imageUrl = "https://firebasestorage.googleapis.com/v0/b/test/o/banner.jpg"
    val tcs = TaskCompletionSource<DocumentSnapshot>()
    `when`(mockDocumentReference.get()).thenReturn(tcs.task)
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.get("bannerImageUrl")).thenReturn(imageUrl)
    tcs.setResult(mockDocumentSnapshot)

    `when`(mockStorage.getReferenceFromUrl(eq(imageUrl))).thenReturn(mockImageRef)
    val tcsGetBytes = TaskCompletionSource<ByteArray>()
    `when`(mockImageRef.getBytes(Long.MAX_VALUE)).thenReturn(tcsGetBytes.task)

    var onFailureCalled = false
    repository.fetchBannerImageAsBitmap(
        accountId,
        onSuccess = { fail("Should not succeed") },
        onFailure = {
          onFailureCalled = true
          assertTrue(it is Exception)
        })

    tcsGetBytes.setException(Exception("Storage error"))
    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(onFailureCalled)
  }

  @Test
  fun fetchBannerImageAsBitmap_firestoreFailure() {
    val tcs = TaskCompletionSource<DocumentSnapshot>()
    `when`(mockDocumentReference.get()).thenReturn(tcs.task)

    var onFailureCalled = false
    repository.fetchBannerImageAsBitmap(
        accountId,
        onSuccess = { fail("Should not succeed") },
        onFailure = {
          onFailureCalled = true
          assertTrue(it is Exception)
        })

    tcs.setException(Exception("Firestore error"))
    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(onFailureCalled)
  }
}
