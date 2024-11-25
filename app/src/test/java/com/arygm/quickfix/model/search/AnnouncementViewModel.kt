package com.arygm.quickfix.model.search

import com.arygm.quickfix.model.locations.Location
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AnnouncementViewModelTest {

  @Mock private lateinit var mockRepository: AnnouncementRepository

  private lateinit var announcementViewModel: AnnouncementViewModel

  private val testDispatcher = UnconfinedTestDispatcher()

  // Test data
  private val announcement1 =
      Announcement(
          announcementId = "announcement1",
          userId = "user1",
          title = "Test Announcement",
          category = "Test Category",
          description = "Test Description",
          location = Location(37.7749, -122.4194, "San Francisco"),
          availability =
              listOf(
                  AvailabilitySlot(
                      start = LocalDateTime.parse("2024-11-24T10:00:00"),
                      end = LocalDateTime.parse("2024-11-24T14:00:00"))),
          quickFixImages = listOf("image1.jpg", "image2.jpg"))

  private val announcement2 =
      Announcement(
          announcementId = "announcement2",
          userId = "user2",
          title = "Another Announcement",
          category = "Other Category",
          description = "Another Description",
          location = Location(40.7128, -74.0060, "New York"),
          availability =
              listOf(
                  AvailabilitySlot(
                      start = LocalDateTime.parse("2024-11-25T08:00:00"),
                      end = LocalDateTime.parse("2024-11-25T12:00:00"))),
          quickFixImages = listOf("image3.jpg", "image4.jpg"))

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    Dispatchers.setMain(testDispatcher)
    announcementViewModel = AnnouncementViewModel(mockRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  // ----- getNewUid Tests -----

  @Test
  fun `getNewUid returns a new unique ID`() {
    // Arrange
    val expectedUid = "unique-id-123"
    whenever(mockRepository.getNewUid()).thenReturn(expectedUid)

    // Act
    val actualUid = announcementViewModel.getNewUid()

    // Assert
    assertEquals(actualUid, expectedUid)
  }

  // ----- getAnnouncements Tests -----

  @Test
  fun getAnnouncements_whenSuccess_updatesAnnouncements() = runTest {
    val announcementsList = listOf(announcement1, announcement2)

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Announcement>) -> Unit>(0)
          onSuccess(announcementsList)
          null
        }
        .`when`(mockRepository)
        .getAnnouncements(any(), any())

    announcementViewModel.getAnnouncements()

    testScheduler.advanceUntilIdle()

    assertEquals(announcementsList, announcementViewModel.announcements.value)
    verify(mockRepository).getAnnouncements(any(), any())
  }

  @Test
  fun getAnnouncements_whenFailure_logsError() = runTest {
    val exception = Exception("Test exception")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(1)
          onFailure(exception)
          null
        }
        .`when`(mockRepository)
        .getAnnouncements(any(), any())

    announcementViewModel.getAnnouncements()

    testScheduler.advanceUntilIdle()

    assertEquals(emptyList<Announcement>(), announcementViewModel.announcements.value)
    verify(mockRepository).getAnnouncements(any(), any())
  }

  // ----- getAnnouncementsForUser Tests -----

  @Test
  fun `getAnnouncementsForUser updates announcementsForUser_ on success`() {
    // Arrange
    val testAnnouncements = listOf("announcement1", "announcement2")
    val fetchedAnnouncements = listOf(announcement1, announcement2)
    whenever(
            mockRepository.getAnnouncementsForUser(
                announcements = eq(testAnnouncements), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Announcement>) -> Unit>(1)
          onSuccess(fetchedAnnouncements)
        }

    // Act
    announcementViewModel.getAnnouncementsForUser(testAnnouncements)

    // Assert
    assertEquals(announcementViewModel.announcementsForUser.value, fetchedAnnouncements)
  }

  @Test
  fun `getAnnouncementsForUser logs error when fetching fails`() {
    // Arrange
    val testAnnouncements = listOf("announcement1", "announcement2")
    val exception = Exception("Network error")
    whenever(mockRepository.getAnnouncementsForUser(any(), any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.getArgument<(Throwable) -> Unit>(2)
      onFailure(exception)
    }

    // Act
    announcementViewModel.getAnnouncementsForUser(testAnnouncements)

    // Assert
    assertTrue(
        announcementViewModel.announcementsForUser.value.isEmpty()) // Ensure no data was added
  }

  // ----- announce Tests -----

  @Test
  fun announce_whenSuccess_updatesAnnouncements() = runTest {
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .`when`(mockRepository)
        .announce(eq(announcement1), any(), any())

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Announcement>) -> Unit>(0)
          onSuccess(listOf(announcement1))
          null
        }
        .`when`(mockRepository)
        .getAnnouncements(any(), any())

    announcementViewModel.announce(announcement1)

    testScheduler.advanceUntilIdle()

    assertEquals(listOf(announcement1), announcementViewModel.announcements.value)
    verify(mockRepository).announce(eq(announcement1), any(), any())
    verify(mockRepository).getAnnouncements(any(), any())
  }

  @Test
  fun announce_whenFailure_logsError() = runTest {
    val exception = Exception("Test exception")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .`when`(mockRepository)
        .announce(eq(announcement1), any(), any())

    announcementViewModel.announce(announcement1)

    testScheduler.advanceUntilIdle()

    verify(mockRepository).announce(eq(announcement1), any(), any())
    verify(mockRepository, never()).getAnnouncements(any(), any())
  }

  // ----- updateAnnouncement Tests -----

  @Test
  fun updateAnnouncement_whenSuccess_updatesAnnouncements() = runTest {
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .`when`(mockRepository)
        .updateAnnouncement(eq(announcement1), any(), any())

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Announcement>) -> Unit>(0)
          onSuccess(listOf(announcement1))
          null
        }
        .`when`(mockRepository)
        .getAnnouncements(any(), any())

    announcementViewModel.updateAnnouncement(announcement1)

    testScheduler.advanceUntilIdle()

    assertEquals(listOf(announcement1), announcementViewModel.announcements.value)
    verify(mockRepository).updateAnnouncement(eq(announcement1), any(), any())
    verify(mockRepository).getAnnouncements(any(), any())
  }

  @Test
  fun updateAnnouncement_whenFailure_logsError() = runTest {
    val exception = Exception("Test exception")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .`when`(mockRepository)
        .updateAnnouncement(eq(announcement1), any(), any())

    announcementViewModel.updateAnnouncement(announcement1)

    testScheduler.advanceUntilIdle()

    verify(mockRepository).updateAnnouncement(eq(announcement1), any(), any())
    verify(mockRepository, never()).getAnnouncements(any(), any())
  }

  // ----- deleteAnnouncementById Tests -----

  @Test
  fun deleteAnnouncementById_whenSuccess_updatesAnnouncements() = runTest {
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(2)
          onSuccess()
          null
        }
        .`when`(mockRepository)
        .deleteAnnouncementById(eq("user1"), eq("announcement1"), any(), any())

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Announcement>) -> Unit>(0)
          onSuccess(emptyList())
          null
        }
        .`when`(mockRepository)
        .getAnnouncements(any(), any())

    announcementViewModel.deleteAnnouncementById("user1", "announcement1")

    testScheduler.advanceUntilIdle()

    assertEquals(emptyList<Announcement>(), announcementViewModel.announcements.value)
    verify(mockRepository).deleteAnnouncementById(eq("user1"), eq("announcement1"), any(), any())
    verify(mockRepository).getAnnouncements(any(), any())
  }

  @Test
  fun deleteAnnouncementById_whenFailure_logsError() = runTest {
    val exception = Exception("Test exception")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(3)
          onFailure(exception)
          null
        }
        .`when`(mockRepository)
        .deleteAnnouncementById(eq("user1"), eq("announcement1"), any(), any())

    announcementViewModel.deleteAnnouncementById("user1", "announcement1")

    testScheduler.advanceUntilIdle()

    verify(mockRepository).deleteAnnouncementById(eq("user1"), eq("announcement1"), any(), any())
    verify(mockRepository, never()).getAnnouncements(any(), any())
  }
}
