package com.arygm.quickfix.model.profile

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RegistrationViewModelTest {

  private lateinit var registrationViewModel: RegistrationViewModel

  @Before
  fun setUp() {
    registrationViewModel = RegistrationViewModel()
  }

  @Test
  fun `updateFirstName updates firstName StateFlow`() = runTest {
    val name = "John"
    registrationViewModel.updateFirstName(name)
    val result = registrationViewModel.firstName.first()
    assertEquals(name, result)
  }

  @Test
  fun `updateLastName updates lastName StateFlow`() = runTest {
    val name = "Doe"
    registrationViewModel.updateLastName(name)
    val result = registrationViewModel.lastName.first()
    assertEquals(name, result)
  }

  @Test
  fun `updateEmail updates email StateFlow`() = runTest {
    val email = "john.doe@example.com"
    registrationViewModel.updateEmail(email)
    val result = registrationViewModel.email.first()
    assertEquals(email, result)
  }

  @Test
  fun `updateBirthDate updates birthDate StateFlow`() = runTest {
    val date = "1990-01-01"
    registrationViewModel.updateBirthDate(date)
    val result = registrationViewModel.birthDate.first()
    assertEquals(date, result)
  }
}
