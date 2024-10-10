package com.arygm.quickfix.utils

import java.util.regex.Pattern

fun isValidEmail(email: String): Boolean {
  val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
  return Pattern.matches(emailPattern, email.trim())
}

fun isValidDate(date: String): Boolean {
  // Simple check for DD/MM/YYYY format
  val datePattern = "^([0-2][0-9]|(3)[0-1])/((0)[1-9]|(1)[0-2])/((19|20)\\d\\d)$"
  return Pattern.matches(datePattern, date.trim())
}
