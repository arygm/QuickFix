package com.arygm.quickfix.model.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegistrationViewModel : ViewModel() {

  private val _firstName = MutableStateFlow("")
  val firstName: StateFlow<String> = _firstName.asStateFlow()

  private val _lastName = MutableStateFlow("")
  val lastName: StateFlow<String> = _lastName.asStateFlow()

  private val _email = MutableStateFlow("")
  val email: StateFlow<String> = _email.asStateFlow()

  private val _birthDate = MutableStateFlow("")
  val birthDate: StateFlow<String> = _birthDate.asStateFlow()

  fun updateFirstName(name: String) {
    _firstName.value = name
  }

  fun updateLastName(name: String) {
    _lastName.value = name
  }

  fun updateEmail(email: String) {
    _email.value = email
  }

  fun updateBirthDate(date: String) {
    _birthDate.value = date
  }
}
