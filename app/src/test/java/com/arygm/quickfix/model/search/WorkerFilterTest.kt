package com.arygm.quickfix.model.search

import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class WorkerFilterTest {

  private lateinit var searchViewModel: SearchViewModel
  private lateinit var workerProfileRepo: WorkerProfileRepositoryFirestore

  @Before
  fun setup() {
    workerProfileRepo = mock(WorkerProfileRepositoryFirestore::class.java)
    searchViewModel = SearchViewModel(workerProfileRepo)
  }

  @Test
  fun `worker is available within working hours and not on unavailable days`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                workingHours = Pair(LocalTime.of(8, 30), LocalTime.of(17, 0)),
                unavailability_list = listOf(LocalDate.of(2023, 12, 4))),
            WorkerProfile(
                uid = "worker2",
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(18, 0)),
                unavailability_list = listOf(LocalDate.of(2023, 12, 5))))

    val selectedDays = listOf(LocalDate.of(2023, 12, 3)) // Not on unavailable days
    val selectedHour = 10
    val selectedMinute = 0

    val result =
        searchViewModel.filterWorkersByAvailability(
            workers, selectedDays, selectedHour, selectedMinute)

    assertEquals(2, result.size)
    assertEquals("worker1", result[0].uid)
    assertEquals("worker2", result[1].uid)
  }

  @Test
  fun `worker is excluded if unavailable on selected day`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                workingHours = Pair(LocalTime.of(8, 30), LocalTime.of(17, 0)),
                unavailability_list = listOf(LocalDate.of(2023, 12, 4)) // Unavailable
                ))

    val selectedDays = listOf(LocalDate.of(2023, 12, 4)) // Selected day matches unavailable day
    val selectedHour = 10
    val selectedMinute = 0

    val result =
        searchViewModel.filterWorkersByAvailability(
            workers, selectedDays, selectedHour, selectedMinute)

    assertEquals(0, result.size) // Worker should be excluded
  }

  @Test
  fun `worker is excluded if selected time is before start time`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                unavailability_list = emptyList()))

    val selectedDays = listOf(LocalDate.of(2023, 12, 3))
    val selectedHour = 8
    val selectedMinute = 30 // Before start time

    val result =
        searchViewModel.filterWorkersByAvailability(
            workers, selectedDays, selectedHour, selectedMinute)

    assertEquals(0, result.size) // Worker should be excluded
  }

  @Test
  fun `worker is excluded if selected time is after end time`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                workingHours = Pair(LocalTime.of(8, 30), LocalTime.of(17, 0)),
                unavailability_list = emptyList()))

    val selectedDays = listOf(LocalDate.of(2023, 12, 3))
    val selectedHour = 17
    val selectedMinute = 30 // After end time

    val result =
        searchViewModel.filterWorkersByAvailability(
            workers, selectedDays, selectedHour, selectedMinute)

    assertEquals(0, result.size) // Worker should be excluded
  }

  @Test
  fun `worker is included if selected time is exactly start or end time`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                workingHours = Pair(LocalTime.of(8, 30), LocalTime.of(17, 0)),
                unavailability_list = emptyList()))

    val selectedDays = listOf(LocalDate.of(2023, 12, 3))

    // Test for start time
    var result = searchViewModel.filterWorkersByAvailability(workers, selectedDays, 8, 30)
    assertEquals(1, result.size) // Worker should be included

    // Test for end time
    result = searchViewModel.filterWorkersByAvailability(workers, selectedDays, 17, 0)
    assertEquals(1, result.size) // Worker should be included
  }

  @Test
  fun `multiple workers are filtered correctly availability`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                workingHours = Pair(LocalTime.of(8, 30), LocalTime.of(17, 0)),
                unavailability_list = emptyList()),
            WorkerProfile(
                uid = "worker2",
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(15, 0)),
                unavailability_list = listOf(LocalDate.of(2023, 12, 4))),
            WorkerProfile(
                uid = "worker3",
                workingHours = Pair(LocalTime.of(7, 0), LocalTime.of(19, 0)),
                unavailability_list = listOf(LocalDate.of(2023, 12, 5))))

    val selectedDays = listOf(LocalDate.of(2023, 12, 4)) // One worker is unavailable on this day
    val selectedHour = 9
    val selectedMinute = 30

    val result =
        searchViewModel.filterWorkersByAvailability(
            workers, selectedDays, selectedHour, selectedMinute)

    assertEquals(2, result.size) // Only two workers should be included
    assertEquals("worker1", result[0].uid)
    assertEquals("worker3", result[1].uid)
  }

  @Test
  fun `worker is included if all selected services are in tags`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                tags = listOf("Exterior Painter", "Interior Painter", "Plumber"),
                displayName = "John Doe"),
            WorkerProfile(
                uid = "worker2",
                tags = listOf("Interior Painter", "Electrician"),
                displayName = "Jane Smith"))

    val selectedServices = listOf("Exterior Painter", "Interior Painter")

    val result = searchViewModel.filterWorkersByServices(workers, selectedServices)

    assertEquals(1, result.size)
    assertEquals("worker1", result[0].uid)
  }

  @Test
  fun `worker is excluded if one selected service is not in tags`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                tags = listOf("Exterior Painter", "Interior Painter", "Plumber"),
                displayName = "John Doe"),
            WorkerProfile(
                uid = "worker2",
                tags = listOf("Interior Painter", "Electrician"),
                displayName = "Jane Smith"))

    val selectedServices = listOf("Exterior Painter", "Electrician")

    val result = searchViewModel.filterWorkersByServices(workers, selectedServices)

    assertEquals(0, result.size) // No workers match all selected services
  }

  @Test
  fun `worker is included if no services are selected`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                tags = listOf("Exterior Painter", "Interior Painter", "Plumber"),
                displayName = "John Doe"),
            WorkerProfile(
                uid = "worker2",
                tags = listOf("Interior Painter", "Electrician"),
                displayName = "Jane Smith"))

    val selectedServices = emptyList<String>()

    val result = searchViewModel.filterWorkersByServices(workers, selectedServices)

    assertEquals(2, result.size) // All workers are included since no services are selected
  }

  @Test
  fun `multiple workers are filtered correctly services`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                tags = listOf("Exterior Painter", "Interior Painter", "Plumber"),
                displayName = "John Doe"),
            WorkerProfile(
                uid = "worker2",
                tags = listOf("Interior Painter", "Electrician"),
                displayName = "Jane Smith"),
            WorkerProfile(
                uid = "worker3",
                tags = listOf("Exterior Painter", "Electrician"),
                displayName = "Sam Brown"))

    val selectedServices = listOf("Interior Painter")

    val result = searchViewModel.filterWorkersByServices(workers, selectedServices)

    assertEquals(2, result.size)
    assertEquals("worker1", result[0].uid)
    assertEquals("worker2", result[1].uid)
  }

  @Test
  fun `worker is excluded if tags list is empty`() {
    val workers =
        listOf(
            WorkerProfile(uid = "worker1", tags = emptyList(), displayName = "John Doe"),
            WorkerProfile(
                uid = "worker2",
                tags = listOf("Interior Painter", "Electrician"),
                displayName = "Jane Smith"))

    val selectedServices = listOf("Interior Painter")

    val result = searchViewModel.filterWorkersByServices(workers, selectedServices)

    assertEquals(1, result.size) // Only "worker2" matches
    assertEquals("worker2", result[0].uid)
  }

  @Test
  fun `sortWorkersByRating sorts workers by rating in descending order`() {
    val workers =
        listOf(
            WorkerProfile(uid = "worker1", rating = 4.5, displayName = "Alice"),
            WorkerProfile(uid = "worker2", rating = 3.8, displayName = "Bob"),
            WorkerProfile(uid = "worker3", rating = 5.0, displayName = "Charlie"),
            WorkerProfile(uid = "worker4", rating = 4.0, displayName = "Diana"))

    val sortedWorkers = searchViewModel.sortWorkersByRating(workers)

    assertEquals("worker3", sortedWorkers[0].uid) // Highest rating
    assertEquals("worker1", sortedWorkers[1].uid)
    assertEquals("worker4", sortedWorkers[2].uid)
    assertEquals("worker2", sortedWorkers[3].uid) // Lowest rating
  }

  @Test
  fun `sortWorkersByRating returns empty list when input is empty`() {
    val workers = emptyList<WorkerProfile>()

    val sortedWorkers = searchViewModel.sortWorkersByRating(workers)

    assertEquals(0, sortedWorkers.size)
  }

  @Test
  fun `sortWorkersByRating maintains order for workers with the same rating`() {
    val workers =
        listOf(
            WorkerProfile(uid = "worker1", rating = 4.5, displayName = "Alice"),
            WorkerProfile(uid = "worker2", rating = 4.5, displayName = "Bob"),
            WorkerProfile(uid = "worker3", rating = 5.0, displayName = "Charlie"),
            WorkerProfile(uid = "worker4", rating = 4.5, displayName = "Diana"))

    val sortedWorkers = searchViewModel.sortWorkersByRating(workers)

    assertEquals("worker3", sortedWorkers[0].uid) // Highest rating
    assertEquals("worker1", sortedWorkers[1].uid) // Maintains order for same ratings
    assertEquals("worker2", sortedWorkers[2].uid)
    assertEquals("worker4", sortedWorkers[3].uid)
  }

  @Test
  fun `sortWorkersByRating handles single worker list correctly`() {
    val workers = listOf(WorkerProfile(uid = "worker1", rating = 4.5, displayName = "Alice"))

    val sortedWorkers = searchViewModel.sortWorkersByRating(workers)

    assertEquals(1, sortedWorkers.size)
    assertEquals("worker1", sortedWorkers[0].uid)
  }

  @Test
  fun `combine filters and sort workers correctly`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                rating = 4.5,
                displayName = "Alice",
                tags = listOf("Interior Painter", "Electrician"),
                workingHours = Pair(LocalTime.of(8, 30), LocalTime.of(17, 0)),
                unavailability_list = listOf(LocalDate.of(2023, 12, 4))),
            WorkerProfile(
                uid = "worker2",
                rating = 5.0,
                displayName = "Bob",
                tags = listOf("Exterior Painter", "Interior Painter"),
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(18, 0)),
                unavailability_list = emptyList()),
            WorkerProfile(
                uid = "worker3",
                rating = 3.8,
                displayName = "Charlie",
                tags = listOf("Electrician"),
                workingHours = Pair(LocalTime.of(7, 0), LocalTime.of(15, 0)),
                unavailability_list = emptyList()))

    val selectedDays = listOf(LocalDate.of(2023, 12, 3)) // Available day
    val selectedHour = 10
    val selectedMinute = 0
    val selectedServices = listOf("Interior Painter")

    val availableWorkers =
        searchViewModel.filterWorkersByAvailability(
            workers, selectedDays, selectedHour, selectedMinute)
    val filteredWorkers =
        searchViewModel.filterWorkersByServices(availableWorkers, selectedServices)
    val sortedWorkers = searchViewModel.sortWorkersByRating(filteredWorkers)

    assertEquals(2, sortedWorkers.size) // Two workers match all conditions
    assertEquals("worker2", sortedWorkers[0].uid) // Highest rating
    assertEquals("worker1", sortedWorkers[1].uid)
  }

  @Test
  fun `combine filters excludes workers unavailable or missing services`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                rating = 4.5,
                displayName = "Alice",
                tags = listOf("Interior Painter", "Electrician"),
                workingHours = Pair(LocalTime.of(8, 30), LocalTime.of(17, 0)),
                unavailability_list = listOf(LocalDate.of(2023, 12, 4))),
            WorkerProfile(
                uid = "worker2",
                rating = 5.0,
                displayName = "Bob",
                tags = listOf("Exterior Painter"),
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(18, 0)),
                unavailability_list = emptyList()),
            WorkerProfile(
                uid = "worker3",
                rating = 3.8,
                displayName = "Charlie",
                tags = listOf("Electrician"),
                workingHours = Pair(LocalTime.of(7, 0), LocalTime.of(15, 0)),
                unavailability_list = listOf(LocalDate.of(2023, 12, 3))))

    val selectedDays = listOf(LocalDate.of(2023, 12, 3)) // Available day
    val selectedHour = 10
    val selectedMinute = 0
    val selectedServices = listOf("Interior Painter")

    val availableWorkers =
        searchViewModel.filterWorkersByAvailability(
            workers, selectedDays, selectedHour, selectedMinute)
    val filteredWorkers =
        searchViewModel.filterWorkersByServices(availableWorkers, selectedServices)
    val sortedWorkers = searchViewModel.sortWorkersByRating(filteredWorkers)

    assertEquals(1, sortedWorkers.size) // Only one worker matches all conditions
    assertEquals("worker1", sortedWorkers[0].uid)
  }

  @Test
  fun `combine filters returns empty list when no workers match all conditions`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                rating = 4.5,
                displayName = "Alice",
                tags = listOf("Interior Painter"),
                workingHours = Pair(LocalTime.of(8, 30), LocalTime.of(17, 0)),
                unavailability_list = listOf(LocalDate.of(2023, 12, 4))),
            WorkerProfile(
                uid = "worker2",
                rating = 5.0,
                displayName = "Bob",
                tags = listOf("Exterior Painter"),
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(18, 0)),
                unavailability_list = emptyList()))

    val selectedDays = listOf(LocalDate.of(2023, 12, 3)) // Available day
    val selectedHour = 19 // After all working hours
    val selectedMinute = 0
    val selectedServices = listOf("Electrician") // No workers provide this service

    val availableWorkers =
        searchViewModel.filterWorkersByAvailability(
            workers, selectedDays, selectedHour, selectedMinute)
    val filteredWorkers =
        searchViewModel.filterWorkersByServices(availableWorkers, selectedServices)
    val sortedWorkers = searchViewModel.sortWorkersByRating(filteredWorkers)

    assertEquals(0, sortedWorkers.size) // No workers match all conditions
  }
}
