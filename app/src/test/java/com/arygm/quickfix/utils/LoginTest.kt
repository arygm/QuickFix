package com.arygm.quickfix.utils

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class LoginTest {

  @Test
  fun `test valid email addresses`() {
    assertTrue(isValidEmail("test@example.com"))
    // TODO Eventually we should add this as a valid option
    // assertTrue(isValidEmail("user.name@example.co.uk"))
    assertTrue(isValidEmail("user_name123@example.org"))
    assertTrue(isValidEmail("user-name@example.com"))
  }

  @Test
  fun `test invalid email addresses`() {
    assertFalse(isValidEmail("test.example.com"))
    assertFalse(isValidEmail("user@.com"))
    assertFalse(isValidEmail("user@com"))
    assertFalse(isValidEmail("@example.com"))
    assertFalse(isValidEmail("user@exam_ple.com"))
  }

  @Test
  fun `test valid date formats`() {
    assertTrue(isValidDate("01/01/2023"))
    assertTrue(isValidDate("29/02/2020"))
    assertTrue(isValidDate("31/12/1999"))
  }

  @Test
  fun `test invalid date formats`() {
    // TODO Eventually we should check the validity of the day (Especially February)
    // assertFalse(isValidDate("31/02/2023")) // Invalid date
    assertFalse(isValidDate("12-31-2020"))
    assertFalse(isValidDate("2020/12/31"))
    assertFalse(isValidDate("32/01/2023"))
    // assertFalse(isValidDate("29/02/2021")) // Not a leap year
  }
}
