package com.arygm.quickfix.model.search

import android.graphics.Bitmap
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.utils.UID_KEY
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class AnnouncementViewModelTest {

  @Mock private lateinit var mockRepository: AnnouncementRepository
  @Mock private lateinit var mockPreferencesRepository: PreferencesRepository
  @Mock private lateinit var mockProfileRepository: ProfileRepository

  private lateinit var announcementViewModel: AnnouncementViewModel

  private val testDispatcher = UnconfinedTestDispatcher()

  private val timestamp = Timestamp.now()

  // Test data
  private val announcement1 =
      Announcement(
          announcementId = "announcement1",
          userId = "user1",
          title = "Test Announcement",
          category = "Test Category",
          description = "Test Description",
          location = Location(37.7749, -122.4194, "San Francisco"),
          availability = listOf(AvailabilitySlot(start = timestamp, end = timestamp)),
          quickFixImages = listOf("image1.jpg", "image2.jpg"))

  private val announcement2 =
      Announcement(
          announcementId = "announcement2",
          userId = "user2",
          title = "Another Announcement",
          category = "Other Category",
          description = "Another Description",
          location = Location(40.7128, -74.0060, "New York"),
          availability = listOf(AvailabilitySlot(start = timestamp, end = timestamp)),
          quickFixImages = listOf("image3.jpg", "image4.jpg"))

  private val userProfile =
      UserProfile(
          locations = listOf(),
          announcements = listOf("announcement1"),
          wallet = 0.0,
          uid = "user1",
          quickFixes = listOf())

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    Dispatchers.setMain(testDispatcher)
    // Mock init calls
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(0)
          onSuccess()
          null
        }
        .whenever(mockRepository)
        .init(any())

    announcementViewModel =
        AnnouncementViewModel(mockRepository, mockPreferencesRepository, mockProfileRepository)

    // By default, return a valid userId when UID_KEY is requested
    whenever(mockPreferencesRepository.getPreferenceByKey(UID_KEY)).thenReturn(flowOf("user1"))
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `getNewUid returns a new unique ID`() {
    val expectedUid = "unique-id-123"
    whenever(mockRepository.getNewUid()).thenReturn(expectedUid)

    val actualUid = announcementViewModel.getNewUid()

    assertEquals(expectedUid, actualUid)
  }

  @Test
  fun `getAnnouncements success updates announcements`() = runTest {
    val announcementsList = listOf(announcement1, announcement2)

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Announcement>) -> Unit>(0)
          onSuccess(announcementsList)
          null
        }
        .whenever(mockRepository)
        .getAnnouncements(any(), any())

    announcementViewModel.getAnnouncements()

    assertEquals(announcementsList, announcementViewModel.announcements.value)
    verify(mockRepository).getAnnouncements(any(), any())
  }

  @Test
  fun `getAnnouncements failure logs error`() = runTest {
    val exception = Exception("Test exception")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(1)
          onFailure(exception)
          null
        }
        .whenever(mockRepository)
        .getAnnouncements(any(), any())

    announcementViewModel.getAnnouncements()

    assertTrue(announcementViewModel.announcements.value.isEmpty())
    verify(mockRepository).getAnnouncements(any(), any())
  }

  @Test
  fun `getAnnouncementsForUser updates announcementsForUser on success and fetches images`() =
      runTest {
        val announcementIds = listOf("announcement1", "announcement2")
        val fetchedAnnouncements = listOf(announcement1, announcement2)

        // Mock fetch images for each announcement
        doAnswer { invocation ->
              val onSuccess = invocation.getArgument<(List<Announcement>) -> Unit>(1)
              onSuccess(fetchedAnnouncements)
              null
            }
            .whenever(mockRepository)
            .getAnnouncementsForUser(eq(announcementIds), any(), any())

        // Mock fetch images success
        doAnswer { invocation ->
              val onSuccess = invocation.getArgument<(List<Pair<String, Bitmap>>) -> Unit>(1)
              onSuccess(emptyList()) // No images
              null
            }
            .whenever(mockRepository)
            .fetchAnnouncementsImagesAsBitmaps(anyString(), any(), any())

        announcementViewModel.getAnnouncementsForUser(announcementIds)

        assertEquals(fetchedAnnouncements, announcementViewModel.announcementsForUser.value)
        verify(mockRepository, times(fetchedAnnouncements.size))
            .fetchAnnouncementsImagesAsBitmaps(anyString(), any(), any())
      }

  @Test
  fun `getAnnouncementsForUser logs error on failure`() = runTest {
    val announcementIds = listOf("announcement1")
    val exception = Exception("Network error")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .whenever(mockRepository)
        .getAnnouncementsForUser(eq(announcementIds), any(), any())

    announcementViewModel.getAnnouncementsForUser(announcementIds)

    assertTrue(announcementViewModel.announcementsForUser.value.isEmpty())
  }

  @Test
  fun `getAnnouncementsForCurrentUser no userId logs error`() = runTest {
    whenever(mockPreferencesRepository.getPreferenceByKey(UID_KEY)).thenReturn(flowOf(null))

    announcementViewModel.getAnnouncementsForCurrentUser()

    // No announcements should be fetched since userId is null
    assertTrue(announcementViewModel.announcementsForUser.value.isEmpty())
    verify(mockProfileRepository, never()).getProfileById(anyString(), any(), any())
  }

  @Test
  fun `getAnnouncementsForCurrentUser no announcements in profile`() = runTest {
    val profileWithNoAnnouncements = UserProfile(listOf(), emptyList(), 0.0, "user1", listOf())

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(Profile) -> Unit>(1)
          onSuccess(profileWithNoAnnouncements)
          null
        }
        .whenever(mockProfileRepository)
        .getProfileById(eq("user1"), any(), any())

    announcementViewModel.getAnnouncementsForCurrentUser()

    // No announcements fetched
    assertTrue(announcementViewModel.announcementsForUser.value.isEmpty())
  }

  @Test
  fun `getAnnouncementsForCurrentUser fetches announcements on success`() = runTest {
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(Profile) -> Unit>(1)
          onSuccess(userProfile)
          null
        }
        .whenever(mockProfileRepository)
        .getProfileById(eq("user1"), any(), any())

    // Mock announcements fetch
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Announcement>) -> Unit>(1)
          onSuccess(listOf(announcement1))
          null
        }
        .whenever(mockRepository)
        .getAnnouncementsForUser(eq(listOf("announcement1")), any(), any())

    // Mock fetch images success
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Pair<String, Bitmap>>) -> Unit>(1)
          onSuccess(emptyList())
          null
        }
        .whenever(mockRepository)
        .fetchAnnouncementsImagesAsBitmaps(anyString(), any(), any())

    announcementViewModel.getAnnouncementsForCurrentUser()

    assertEquals(listOf(announcement1), announcementViewModel.announcementsForUser.value)
  }

  @Test
  fun `getAnnouncementsForCurrentUser profile fetch failure logs error`() = runTest {
    val exception = Exception("Profile error")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .whenever(mockProfileRepository)
        .getProfileById(eq("user1"), any(), any())

    announcementViewModel.getAnnouncementsForCurrentUser()

    assertTrue(announcementViewModel.announcementsForUser.value.isEmpty())
  }

  @Test
  fun `announce success updates announcementsForUser and fetches images`() = runTest {
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(mockRepository)
        .announce(eq(announcement1), any(), any())

    // Mock fetch images after announcing
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Pair<String, Bitmap>>) -> Unit>(1)
          onSuccess(emptyList())
          null
        }
        .whenever(mockRepository)
        .fetchAnnouncementsImagesAsBitmaps(eq("announcement1"), any(), any())

    announcementViewModel.announce(announcement1)

    assertEquals(listOf(announcement1), announcementViewModel.announcementsForUser.value)
  }

  @Test
  fun `announce failure logs error`() = runTest {
    val exception = Exception("announce failed")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .whenever(mockRepository)
        .announce(eq(announcement1), any(), any())

    announcementViewModel.announce(announcement1)

    assertTrue(announcementViewModel.announcementsForUser.value.isEmpty())
  }

  @Test
  fun `uploadAnnouncementImages success calls onSuccess`() = runTest {
    val images = listOf(mock(Bitmap::class.java), mock(Bitmap::class.java))
    val expectedUrls = listOf("url1", "url2")

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<String>) -> Unit>(2)
          onSuccess(expectedUrls)
          null
        }
        .whenever(mockRepository)
        .uploadAnnouncementImages(eq("announcement1"), eq(images), any(), any())

    var actualUrls: List<String>? = null
    var onSuccessCalled = false
    var onFailureCalled = false

    announcementViewModel.uploadAnnouncementImages(
        "announcement1",
        images,
        onSuccess = {
          actualUrls = it
          onSuccessCalled = true
        },
        onFailure = { onFailureCalled = true })

    assertTrue(onSuccessCalled)
    assertFalse(onFailureCalled)
    assertEquals(expectedUrls, actualUrls)
  }

  @Test
  fun `uploadAnnouncementImages failure calls onFailure`() = runTest {
    val images = listOf(mock(Bitmap::class.java))
    val exception = Exception("Upload failed")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(3)
          onFailure(exception)
          null
        }
        .whenever(mockRepository)
        .uploadAnnouncementImages(eq("announcement1"), eq(images), any(), any())

    var onSuccessCalled = false
    var onFailureCalled = false
    var actualException: Exception? = null

    announcementViewModel.uploadAnnouncementImages(
        "announcement1",
        images,
        onSuccess = { onSuccessCalled = true },
        onFailure = {
          actualException = it
          onFailureCalled = true
        })

    assertFalse(onSuccessCalled)
    assertTrue(onFailureCalled)
    assertEquals(exception, actualException)
  }

  @Test
  fun `fetchAnnouncementImagesAsBitmaps success updates map`() = runTest {
    val pairs = listOf("url1" to mock(Bitmap::class.java))
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Pair<String, Bitmap>>) -> Unit>(1)
          onSuccess(pairs)
          null
        }
        .whenever(mockRepository)
        .fetchAnnouncementsImagesAsBitmaps(eq("announcement1"), any(), any())

    announcementViewModel.fetchAnnouncementImagesAsBitmaps("announcement1")

    assertEquals(pairs, announcementViewModel.announcementImagesMap.value["announcement1"])
  }

  @Test
  fun `fetchAnnouncementImagesAsBitmaps failure logs error`() = runTest {
    val exception = Exception("Images fetch failed")
    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .whenever(mockRepository)
        .fetchAnnouncementsImagesAsBitmaps(eq("announcement1"), any(), any())

    announcementViewModel.fetchAnnouncementImagesAsBitmaps("announcement1")

    // The map should remain unchanged (empty)
    assertTrue(announcementViewModel.announcementImagesMap.value.isEmpty())
  }

  @Test
  fun `updateAnnouncement success replaces old announcement and fetches images`() = runTest {
    // First announce to have something in announcementsForUser
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(mockRepository)
        .announce(eq(announcement1), any(), any())

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Pair<String, Bitmap>>) -> Unit>(1)
          onSuccess(emptyList())
          null
        }
        .whenever(mockRepository)
        .fetchAnnouncementsImagesAsBitmaps(eq("announcement1"), any(), any())

    announcementViewModel.announce(announcement1)
    assertEquals(listOf(announcement1), announcementViewModel.announcementsForUser.value)

    val updatedAnnouncement1 = announcement1.copy(title = "Updated")
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(mockRepository)
        .updateAnnouncement(eq(updatedAnnouncement1), any(), any())

    announcementViewModel.updateAnnouncement(updatedAnnouncement1)

    assertEquals(listOf(updatedAnnouncement1), announcementViewModel.announcementsForUser.value)
  }

  @Test
  fun `updateAnnouncement failure logs error`() = runTest {
    val exception = Exception("Update failed")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .whenever(mockRepository)
        .updateAnnouncement(eq(announcement1), any(), any())

    announcementViewModel.updateAnnouncement(announcement1)

    // announcementsForUser should remain unchanged
    assertTrue(announcementViewModel.announcementsForUser.value.isEmpty())
  }

  @Test
  fun `deleteAnnouncementById success updates announcementsForUser and removes from profile`() =
      runTest {
        // Setup announcements and profile
        // First announce it
        doAnswer { invocation ->
              val onSuccess = invocation.getArgument<() -> Unit>(1)
              onSuccess()
              null
            }
            .whenever(mockRepository)
            .announce(eq(announcement1), any(), any())
        announcementViewModel.announce(announcement1)
        assertEquals(listOf(announcement1), announcementViewModel.announcementsForUser.value)

        // Mock profile fetch
        doAnswer { invocation ->
              val onSuccess = invocation.getArgument<(Profile) -> Unit>(1)
              onSuccess(userProfile)
              null
            }
            .whenever(mockProfileRepository)
            .getProfileById(eq("user1"), any(), any())

        // Mock profile update success
        doAnswer { invocation ->
              val onSuccess = invocation.getArgument<() -> Unit>(1)
              onSuccess()
              null
            }
            .whenever(mockProfileRepository)
            .updateProfile(any(), any(), any())

        // Mock deleteAnnouncementById success
        doAnswer { invocation ->
              val onSuccess = invocation.getArgument<() -> Unit>(1)
              onSuccess()
              null
            }
            .whenever(mockRepository)
            .deleteAnnouncementById(eq("announcement1"), any(), any())

        announcementViewModel.deleteAnnouncementById("announcement1")

        assertTrue(announcementViewModel.announcementsForUser.value.isEmpty())
      }

  @Test
  fun `deleteAnnouncementById failure logs error`() = runTest {
    // Announce first
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(mockRepository)
        .announce(eq(announcement1), any(), any())
    announcementViewModel.announce(announcement1)

    val exception = Exception("Delete failed")
    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .whenever(mockRepository)
        .deleteAnnouncementById(eq("announcement1"), any(), any())

    announcementViewModel.deleteAnnouncementById("announcement1")

    // Announcement should still remain
    assertEquals(listOf(announcement1), announcementViewModel.announcementsForUser.value)
  }

  @Test
  fun `addUploadedImage adds image to uploadedImages`() = runTest {
    val image = mock(Bitmap::class.java)
    assertTrue(announcementViewModel.uploadedImages.value.isEmpty())

    announcementViewModel.addUploadedImage(image)

    assertEquals(listOf(image), announcementViewModel.uploadedImages.value)
  }

  @Test
  fun `deleteUploadedImages removes images from uploadedImages`() = runTest {
    val image1 = mock(Bitmap::class.java)
    val image2 = mock(Bitmap::class.java)
    val image3 = mock(Bitmap::class.java)

    announcementViewModel.addUploadedImage(image1)
    announcementViewModel.addUploadedImage(image2)
    announcementViewModel.addUploadedImage(image3)

    announcementViewModel.deleteUploadedImages(listOf(image2))

    assertEquals(listOf(image1, image3), announcementViewModel.uploadedImages.value)
  }

  @Test
  fun `clearUploadedImages clears uploadedImages`() = runTest {
    val image1 = mock(Bitmap::class.java)
    val image2 = mock(Bitmap::class.java)

    announcementViewModel.addUploadedImage(image1)
    announcementViewModel.addUploadedImage(image2)

    announcementViewModel.clearUploadedImages()

    assertTrue(announcementViewModel.uploadedImages.value.isEmpty())
  }

  @Test
  fun `selectAnnouncement sets selectedAnnouncement`() {
    announcementViewModel.selectAnnouncement(announcement1)
    assertEquals(announcement1, announcementViewModel.selectedAnnouncement.value)
  }

  @Test
  fun `unselectAnnouncement clears selectedAnnouncement`() {
    announcementViewModel.selectAnnouncement(announcement1)
    announcementViewModel.unselectAnnouncement()
    assertNull(announcementViewModel.selectedAnnouncement.value)
  }

  @Test
  fun `setAnnouncementImagesMap updates the map`() {
    val pairs = listOf("url1" to mock(Bitmap::class.java))
    val updatedMap = mutableMapOf("announcement1" to pairs)

    announcementViewModel.setAnnouncementImagesMap(updatedMap)

    assertEquals(pairs, announcementViewModel.announcementImagesMap.value["announcement1"])
  }
}
