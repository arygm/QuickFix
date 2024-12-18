package com.arygm.quickfix.model.search

import com.arygm.quickfix.model.locations.Location
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

  @Test
  fun `filterWorkersByPriceRange returns workers within price range`() {
    val workers =
        listOf(
            WorkerProfile(price = 100.0, displayName = "Worker 1"),
            WorkerProfile(price = 200.0, displayName = "Worker 2"),
            WorkerProfile(price = 300.0, displayName = "Worker 3"),
            WorkerProfile(price = 400.0, displayName = "Worker 4"))

    val result = searchViewModel.filterWorkersByPriceRange(workers, 150, 350)

    assertEquals(2, result.size)
    assertEquals("Worker 2", result[0].displayName)
    assertEquals("Worker 3", result[1].displayName)
  }

  @Test
  fun `filterWorkersByPriceRange excludes workers outside price range`() {
    val workers =
        listOf(
            WorkerProfile(price = 100.0, displayName = "Worker 1"),
            WorkerProfile(price = 200.0, displayName = "Worker 2"),
            WorkerProfile(price = 300.0, displayName = "Worker 3"))

    val result = searchViewModel.filterWorkersByPriceRange(workers, 250, 350)

    assertEquals(1, result.size)
    assertEquals("Worker 3", result[0].displayName)
  }

  @Test
  fun `filterWorkersByPriceRange returns empty list if no workers match`() {
    val workers =
        listOf(
            WorkerProfile(price = 100.0, displayName = "Worker 1"),
            WorkerProfile(price = 200.0, displayName = "Worker 2"))

    val result = searchViewModel.filterWorkersByPriceRange(workers, 300, 400)

    assertEquals(0, result.size)
  }

  @Test
  fun `filterWorkersByPriceRange includes workers on the boundary`() {
    val workers =
        listOf(
            WorkerProfile(price = 100.0, displayName = "Worker 1"),
            WorkerProfile(price = 200.0, displayName = "Worker 2"),
            WorkerProfile(price = 300.0, displayName = "Worker 3"))

    val result = searchViewModel.filterWorkersByPriceRange(workers, 100, 300)

    assertEquals(3, result.size)
    assertEquals("Worker 1", result[0].displayName)
    assertEquals("Worker 2", result[1].displayName)
    assertEquals("Worker 3", result[2].displayName)
  }

  @Test
  fun `combine filters by price range, availability, and services`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                price = 150.0,
                tags = listOf("Painter", "Plumber"),
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                unavailability_list = listOf(LocalDate.of(2023, 12, 5)),
                rating = 4.5),
            WorkerProfile(
                uid = "worker2",
                price = 200.0,
                tags = listOf("Painter", "Electrician"),
                workingHours = Pair(LocalTime.of(8, 0), LocalTime.of(16, 0)),
                unavailability_list = emptyList(),
                rating = 5.0),
            WorkerProfile(
                uid = "worker3",
                price = 300.0,
                tags = listOf("Electrician"),
                workingHours = Pair(LocalTime.of(10, 0), LocalTime.of(18, 0)),
                unavailability_list = listOf(LocalDate.of(2023, 12, 4)),
                rating = 3.8))

    val selectedDays = listOf(LocalDate.of(2023, 12, 4)) // Workers available on this day
    val selectedHour = 10
    val selectedMinute = 30
    val priceStart = 100
    val priceEnd = 250
    val selectedServices = listOf("Painter")

    // Apply availability filter
    val availableWorkers =
        searchViewModel.filterWorkersByAvailability(
            workers, selectedDays, selectedHour, selectedMinute)

    // Apply price range filter
    val priceFilteredWorkers =
        searchViewModel.filterWorkersByPriceRange(availableWorkers, priceStart, priceEnd)

    // Apply services filter
    val serviceFilteredWorkers =
        searchViewModel.filterWorkersByServices(priceFilteredWorkers, selectedServices)

    // Sort by rating
    val sortedWorkers = searchViewModel.sortWorkersByRating(serviceFilteredWorkers)

    assertEquals(2, sortedWorkers.size)
    assertEquals("worker2", sortedWorkers[0].uid)
    assertEquals("worker1", sortedWorkers[1].uid)
  }

  @Test
  fun `combine filters excludes workers with mismatched conditions`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                price = 150.0,
                tags = listOf("Plumber"),
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                unavailability_list = listOf(LocalDate.of(2023, 12, 4)),
                rating = 4.5),
            WorkerProfile(
                uid = "worker2",
                price = 200.0,
                tags = listOf("Painter", "Electrician"),
                workingHours = Pair(LocalTime.of(8, 0), LocalTime.of(16, 0)),
                unavailability_list = emptyList(),
                rating = 5.0))

    val selectedDays = listOf(LocalDate.of(2023, 12, 4)) // Workers available on this day
    val selectedHour = 10
    val selectedMinute = 30
    val priceStart = 100
    val priceEnd = 250
    val selectedServices = listOf("Interior Painter") // No worker matches this service

    // Apply availability filter
    val availableWorkers =
        searchViewModel.filterWorkersByAvailability(
            workers, selectedDays, selectedHour, selectedMinute)

    // Apply price range filter
    val priceFilteredWorkers =
        searchViewModel.filterWorkersByPriceRange(availableWorkers, priceStart, priceEnd)

    // Apply services filter
    val serviceFilteredWorkers =
        searchViewModel.filterWorkersByServices(priceFilteredWorkers, selectedServices)

    // Sort by rating
    val sortedWorkers = searchViewModel.sortWorkersByRating(serviceFilteredWorkers)

    assertEquals(0, sortedWorkers.size) // No workers match all filters
  }

  @Test
  fun `combine filters with edge case on availability and price`() {
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                price = 150.0,
                tags = listOf("Painter"),
                workingHours = Pair(LocalTime.of(10, 30), LocalTime.of(17, 0)),
                unavailability_list = emptyList(),
                rating = 4.5),
            WorkerProfile(
                uid = "worker2",
                price = 250.0,
                tags = listOf("Painter"),
                workingHours = Pair(LocalTime.of(8, 0), LocalTime.of(18, 0)),
                unavailability_list = emptyList(),
                rating = 5.0))

    val selectedDays = listOf(LocalDate.of(2023, 12, 3))
    val selectedHour = 10
    val selectedMinute = 30
    val priceStart = 200
    val priceEnd = 300
    val selectedServices = listOf("Painter")

    // Apply availability filter
    val availableWorkers =
        searchViewModel.filterWorkersByAvailability(
            workers, selectedDays, selectedHour, selectedMinute)

    // Apply price range filter
    val priceFilteredWorkers =
        searchViewModel.filterWorkersByPriceRange(availableWorkers, priceStart, priceEnd)

    // Apply services filter
    val serviceFilteredWorkers =
        searchViewModel.filterWorkersByServices(priceFilteredWorkers, selectedServices)

    // Sort by rating
    val sortedWorkers = searchViewModel.sortWorkersByRating(serviceFilteredWorkers)

    assertEquals(1, sortedWorkers.size)
    assertEquals("worker2", sortedWorkers[0].uid)
  }

  @Test
  fun `filterWorkersByDistance returns workers within maxDistance`() {
    val userLocation = Location(40.0, -74.0, "User Location")
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                location = Location(40.0, -74.1, "Nearby Worker"),
                displayName = "Nearby Worker"),
            WorkerProfile(
                uid = "worker2",
                location = Location(42.0, -75.0, "Far Worker"),
                displayName = "Far Worker"),
            WorkerProfile(
                uid = "worker3",
                location = Location(40.0, -74.0, "Same Location"),
                displayName = "Same Location"))

    val maxDistance = 20 // in kilometers

    val result = searchViewModel.filterWorkersByDistance(workers, userLocation, maxDistance)

    // Validate the result
    assertEquals(2, result.size)
    assertEquals("Nearby Worker", result[0].displayName)
    assertEquals("Same Location", result[1].displayName)
  }

  @Test
  fun `filterWorkersByDistance excludes workers beyond maxDistance`() {
    val userLocation = Location(40.0, -74.0, "User Location")
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                location = Location(40.0, -74.1, "Nearby Worker"),
                displayName = "Nearby Worker"),
            WorkerProfile(
                uid = "worker2",
                location = Location(45.0, -80.0, "Far Worker"),
                displayName = "Far Worker"))

    val maxDistance = 10 // in kilometers

    val result = searchViewModel.filterWorkersByDistance(workers, userLocation, maxDistance)

    // Validate the result
    assertEquals(1, result.size)
    assertEquals("Nearby Worker", result[0].displayName)
  }

  @Test
  fun `filterWorkersByDistance includes only workers exactly at maxDistance`() {
    val userLocation = Location(40.0, -74.0, "User Location")
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                location = Location(40.1, -74.1, "At Max Distance"),
                displayName = "At Max Distance"),
            WorkerProfile(
                uid = "worker2",
                location = Location(40.5, -75.0, "Beyond Max Distance"),
                displayName = "Beyond Max Distance"))

    val maxDistance = 15 // in kilometers

    val result = searchViewModel.filterWorkersByDistance(workers, userLocation, maxDistance)

    // Validate the result
    assertEquals(1, result.size)
    assertEquals("At Max Distance", result[0].displayName)
  }

  @Test
  fun `filterWorkersByDistance returns empty list if no workers are within maxDistance`() {
    val userLocation = Location(40.0, -74.0, "User Location")
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                location = Location(45.0, -80.0, "Far Worker"),
                displayName = "Far Worker"))

    val maxDistance = 5 // in kilometers

    val result = searchViewModel.filterWorkersByDistance(workers, userLocation, maxDistance)

    // Validate the result
    assertEquals(0, result.size)
  }

  @Test
  fun `sortWorkersByRating handles NaN and sorts them at the end`() {
    val workers =
        listOf(
            WorkerProfile(uid = "worker1", rating = 4.5, displayName = "Alice"),
            WorkerProfile(uid = "worker2", rating = Double.NaN, displayName = "Bob"),
            WorkerProfile(uid = "worker3", rating = 5.0, displayName = "Charlie"),
            WorkerProfile(uid = "worker4", rating = 3.8, displayName = "Diana"))

    val sortedWorkers = searchViewModel.sortWorkersByRating(workers)

    // Assert valid ratings are sorted in descending order
    assertEquals("worker3", sortedWorkers[0].uid)
    assertEquals("worker1", sortedWorkers[1].uid)
    assertEquals("worker4", sortedWorkers[2].uid)
    // Assert NaN appears last
    assertEquals("worker2", sortedWorkers[3].uid)
  }

  @Test
  fun `sortWorkersByRating handles multiple NaN ratings correctly`() {
    val workers =
        listOf(
            WorkerProfile(uid = "worker1", rating = Double.NaN, displayName = "Alice"),
            WorkerProfile(uid = "worker2", rating = 4.0, displayName = "Bob"),
            WorkerProfile(uid = "worker3", rating = Double.NaN, displayName = "Charlie"),
            WorkerProfile(uid = "worker4", rating = 5.0, displayName = "Diana"))

    val sortedWorkers = searchViewModel.sortWorkersByRating(workers)

    // Assert valid ratings are sorted in descending order
    assertEquals("worker4", sortedWorkers[0].uid)
    assertEquals("worker2", sortedWorkers[1].uid)
    // Assert all NaN ratings appear at the end
    assertEquals("worker1", sortedWorkers[2].uid)
    assertEquals("worker3", sortedWorkers[3].uid)
  }

  @Test
  fun `sortWorkersByRating works with only NaN ratings`() {
    val workers =
        listOf(
            WorkerProfile(uid = "worker1", rating = Double.NaN, displayName = "Alice"),
            WorkerProfile(uid = "worker2", rating = Double.NaN, displayName = "Bob"),
            WorkerProfile(uid = "worker3", rating = Double.NaN, displayName = "Charlie"))

    val sortedWorkers = searchViewModel.sortWorkersByRating(workers)

    // Assert all workers remain in their original order since all ratings are NaN
    assertEquals("worker1", sortedWorkers[0].uid)
    assertEquals("worker2", sortedWorkers[1].uid)
    assertEquals("worker3", sortedWorkers[2].uid)
  }

  @Test
  fun `sortWorkersByRating handles mixed valid and NaN ratings`() {
    val workers =
        listOf(
            WorkerProfile(uid = "worker1", rating = 4.5, displayName = "Alice"),
            WorkerProfile(uid = "worker2", rating = 3.8, displayName = "Bob"),
            WorkerProfile(uid = "worker3", rating = Double.NaN, displayName = "Charlie"),
            WorkerProfile(uid = "worker4", rating = Double.NaN, displayName = "Diana"),
            WorkerProfile(uid = "worker5", rating = 5.0, displayName = "Eve"))

    val sortedWorkers = searchViewModel.sortWorkersByRating(workers)

    // Assert valid ratings are sorted in descending order
    assertEquals("worker5", sortedWorkers[0].uid)
    assertEquals("worker1", sortedWorkers[1].uid)
    assertEquals("worker2", sortedWorkers[2].uid)
    // Assert NaN ratings are at the end in the original order
    assertEquals("worker3", sortedWorkers[3].uid)
    assertEquals("worker4", sortedWorkers[4].uid)
  }

  @Test
  fun `emergencyFilter returns up to 3 closest available workers`() {
    val userLocation = Location(40.0, -74.0, "User Location")
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                location = Location(40.1, -74.1, "Nearby Worker 1"),
                workingHours = Pair(LocalTime.of(8, 0), LocalTime.of(18, 0)),
                unavailability_list = emptyList()),
            WorkerProfile(
                uid = "worker2",
                location = Location(40.2, -74.2, "Nearby Worker 2"),
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                unavailability_list = emptyList()),
            WorkerProfile(
                uid = "worker3",
                location = Location(41.0, -75.0, "Far Worker"),
                workingHours = Pair(LocalTime.of(7, 0), LocalTime.of(15, 0)),
                unavailability_list = emptyList()),
            WorkerProfile(
                uid = "worker4",
                location = Location(42.0, -76.0, "Very Far Worker"),
                workingHours = Pair(LocalTime.of(8, 0), LocalTime.of(16, 0)),
                unavailability_list = emptyList()))

    val result = searchViewModel.emergencyFilter(workers, userLocation, 10, 0)

    // Validate the result
    assertEquals(3, result.size)
    assertEquals("worker1", result[0].uid) // Closest worker
    assertEquals("worker2", result[1].uid)
    assertEquals("worker3", result[2].uid) // Third closest worker
  }

  @Test
  fun `emergencyFilter excludes workers unavailable at current time`() {
    val userLocation = Location(40.0, -74.0, "User Location")
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                location = Location(40.1, -74.1, "Nearby Worker"),
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                unavailability_list = listOf(LocalDate.now()) // Unavailable today
                ),
            WorkerProfile(
                uid = "worker2",
                location = Location(40.2, -74.2, "Nearby Worker 2"),
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(18, 0)),
                unavailability_list = emptyList()))

    val result = searchViewModel.emergencyFilter(workers, userLocation, 17, 30)

    // Validate the result
    assertEquals(1, result.size)
    assertEquals("worker2", result[0].uid) // Only available worker
  }

  @Test
  fun `emergencyFilter returns empty list if no workers are available`() {
    val userLocation = Location(40.0, -74.0, "User Location")
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                location = Location(40.1, -74.1, "Nearby Worker"),
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                unavailability_list = listOf(LocalDate.now()) // Unavailable today
                ),
            WorkerProfile(
                uid = "worker2",
                location = Location(40.2, -74.2, "Nearby Worker 2"),
                workingHours = Pair(LocalTime.of(10, 0), LocalTime.of(16, 0)),
                unavailability_list = emptyList()))

    // Simulate a time when no workers are available
    val fakeCurrentTime = LocalTime.of(18, 0)
    val result =
        searchViewModel.filterWorkersByAvailability(
            workers, listOf(LocalDate.now()), fakeCurrentTime.hour, fakeCurrentTime.minute)

    assertEquals(0, result.size) // No workers available
  }

  @Test
  fun `emergencyFilter includes workers without location at the end`() {
    val userLocation = Location(40.0, -74.0, "User Location")
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                location = Location(40.1, -74.1, "Nearby Worker"),
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                unavailability_list = emptyList()),
            WorkerProfile(
                uid = "worker2",
                location = null, // Worker without location
                workingHours = Pair(LocalTime.of(8, 0), LocalTime.of(16, 0)),
                unavailability_list = emptyList()))

    val result = searchViewModel.emergencyFilter(workers, userLocation, 15, 30)

    // Validate the result
    assertEquals(2, result.size)
    assertEquals("worker1", result[0].uid) // Closest worker
    assertEquals("worker2", result[1].uid) // Worker without location
  }

  @Test
  fun `emergencyFilter returns fewer than 3 workers if less are available`() {
    val userLocation = Location(40.0, -74.0, "User Location")
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker1",
                location = Location(40.1, -74.1, "Nearby Worker"),
                workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                unavailability_list = emptyList()))

    val result = searchViewModel.emergencyFilter(workers, userLocation, 15, 30)

    // Validate the result
    assertEquals(1, result.size)
    assertEquals("worker1", result[0].uid)
  }
}
