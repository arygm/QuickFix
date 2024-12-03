package com.arygm.quickfix.model.search

import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
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
  private lateinit var categoryRepo: CategoryRepositoryFirestore

  @Before
  fun setup() {
    workerProfileRepo = mock(WorkerProfileRepositoryFirestore::class.java)
    categoryRepo = mock(CategoryRepositoryFirestore::class.java)
    searchViewModel = SearchViewModel(workerProfileRepo, categoryRepo)
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
  fun `multiple workers are filtered correctly`() {
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
}
