package com.arygm.quickfix.model

import android.location.Location
import junit.framework.TestCase.assertEquals
import org.junit.Test

class WorkerProfileTest {
  @Test
  fun testWorkerProfileCreation() {
    val location = Location("provider")
    val workerProfile =
        WorkerProfile(
            birthDate = "1990-01-01",
            email = "worker@example.com",
            firstName = "John",
            lastName = "Doe",
            uid = "worker_123",
            location = location,
            hourlyRate = 25.5)

    assertEquals("1990-01-01", workerProfile.birthDate)
    assertEquals("worker@example.com", workerProfile.email)
    assertEquals("John", workerProfile.firstName)
    assertEquals("Doe", workerProfile.lastName)
    assertEquals("worker_123", workerProfile.uid)
    assertEquals(location, workerProfile.location)
    assertEquals(25.5, workerProfile.hourlyRate, 0.0)
  }

  @Test
  fun testWorkerProfileEquality() {
    val location = Location("provider")
    val workerProfile1 =
        WorkerProfile(
            birthDate = "1990-01-01",
            email = "worker@example.com",
            firstName = "John",
            lastName = "Doe",
            uid = "worker_123",
            location = location,
            hourlyRate = 25.5)

    val workerProfile2 =
        WorkerProfile(
            birthDate = "1990-01-01",
            email = "worker@example.com",
            firstName = "John",
            lastName = "Doe",
            uid = "worker_123",
            location = location,
            hourlyRate = 25.5)

    assertEquals(workerProfile1, workerProfile2)
  }
}
