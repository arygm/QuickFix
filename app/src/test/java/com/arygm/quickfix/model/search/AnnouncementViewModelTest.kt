package com.arygm.quickfix.model.search

import com.arygm.quickfix.model.locations.Location
import com.google.firebase.Timestamp
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
  fun announce_whenSuccess_updatesAnnouncementsForUser() = runTest {
    // Mock the repository's `announce` to simulate success
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess() // Trigger the success callback
          null
        }
        .`when`(mockRepository)
        .announce(eq(announcement1), any(), any())

    // Step 1: Call the `announce` method to add the announcement
    announcementViewModel.announce(announcement1)

    // Simulate time passing to ensure any async operations complete
    testScheduler.advanceUntilIdle()

    // Step 2: Verify that the announcement is added to `announcementsForUser`
    assertEquals(listOf(announcement1), announcementViewModel.announcementsForUser.value)

    // Verify the repository interaction
    verify(mockRepository).announce(eq(announcement1), any(), any())
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
  fun updateAnnouncement_whenSuccess_updatesAnnouncementsForUser() = runTest {
    val updatedAnnouncement1 =
        Announcement(
            announcementId = "announcement1",
            userId = "user1",
            title = "Updated Test Announcement",
            category = "Test Category",
            description = "Test Description",
            location = Location(37.7749, -122.4194, "San Francisco"),
            availability = listOf(AvailabilitySlot(start = timestamp, end = timestamp)),
            quickFixImages = listOf("image1.jpg", "image2.jpg"))
    // Mock the repository's `announce` to simulate success
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess() // Trigger the success callback
          null
        }
        .`when`(mockRepository)
        .announce(eq(announcement1), any(), any())

    // Mock the repository's `updateAnnouncement` to simulate success
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess() // Trigger the success callback
          null
        }
        .`when`(mockRepository)
        .updateAnnouncement(eq(updatedAnnouncement1), any(), any())

    // Step 1: Announce the initial announcement to populate `announcementsForUser_`
    announcementViewModel.announce(announcement1)
    testScheduler.advanceUntilIdle()

    // Verify that the initial announcement is in the list
    assertEquals(listOf(announcement1), announcementViewModel.announcementsForUser.value)

    // Step 2: Update the announcement
    announcementViewModel.updateAnnouncement(updatedAnnouncement1)
    testScheduler.advanceUntilIdle()

    // Verify that the updated announcement replaces the old one
    assertEquals(listOf(updatedAnnouncement1), announcementViewModel.announcementsForUser.value)

    // Verify repository interactions
    verify(mockRepository).announce(eq(announcement1), any(), any())
    verify(mockRepository).updateAnnouncement(eq(updatedAnnouncement1), any(), any())
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
  fun deleteAnnouncementById_whenSuccess_updatesAnnouncementsForUser() = runTest {
    // Mock the repository's `announce` to simulate success and add the announcement
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess() // Trigger the success callback for announcing
          null
        }
        .`when`(mockRepository)
        .announce(eq(announcement1), any(), any())

    // Step 1: Announce the announcement to add it to the state
    announcementViewModel.announce(announcement1)

    // Simulate time passing to ensure async operations complete
    testScheduler.advanceUntilIdle()

    // Verify that the announcement is added to `announcementsForUser`
    assertEquals(listOf(announcement1), announcementViewModel.announcementsForUser.value)

    // Mock the repository's `deleteAnnouncementById` to simulate success
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess() // Trigger the success callback for deletion
          null
        }
        .`when`(mockRepository)
        .deleteAnnouncementById(eq("announcement1"), any(), any())

    // Step 2: Call `deleteAnnouncementById` to delete the announcement
    announcementViewModel.deleteAnnouncementById("user1", "announcement1")

    // Simulate time passing again to ensure async operations complete
    testScheduler.advanceUntilIdle()

    // Step 3: Verify that the announcement is removed from `announcementsForUser`
    assertEquals(emptyList<Announcement>(), announcementViewModel.announcementsForUser.value)

    // Verify the repository interaction
    verify(mockRepository).deleteAnnouncementById(eq("announcement1"), any(), any())
  }

  @Test
  fun deleteAnnouncementById_whenFailure_logsError() = runTest {
    val exception = Exception("Test exception")

    // Mock the repository's `deleteAnnouncementById` to simulate a failure
    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception) // Trigger the failure callback
          null
        }
        .`when`(mockRepository)
        .deleteAnnouncementById(eq("announcement1"), any(), any())

    // Step 1: Announce the announcement to add it to the state
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess() // Trigger the success callback for announcing
          null
        }
        .`when`(mockRepository)
        .announce(eq(announcement1), any(), any())

    announcementViewModel.announce(announcement1)

    // Simulate time passing to ensure async operations complete
    testScheduler.advanceUntilIdle()

    // Step 2: Call `deleteAnnouncementById` to simulate failure
    announcementViewModel.deleteAnnouncementById("user1", "announcement1")

    // Simulate time passing again to ensure async operations complete
    testScheduler.advanceUntilIdle()

    // Step 3: Verify that no change happened to the announcements list
    assertEquals(listOf(announcement1), announcementViewModel.announcementsForUser.value)

    // Verify the repository interaction
    verify(mockRepository).deleteAnnouncementById(eq("announcement1"), any(), any())
    verify(mockRepository, never()).getAnnouncements(any(), any())
  }
}
