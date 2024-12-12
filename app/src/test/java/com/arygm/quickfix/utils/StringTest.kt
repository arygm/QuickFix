package com.arygm.quickfix.utils

import com.arygm.quickfix.ui.noModeUI.navigation.NoModeRoute
import com.arygm.quickfix.ui.noModeUI.navigation.NoModeScreen
import com.arygm.quickfix.ui.userModeUI.navigation.UserRoute
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.GregorianCalendar
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StringTest {
  @Test
  fun `test timestampToString with default pattern`() {
    val calendar = GregorianCalendar(2023, Calendar.JANUARY, 1, 0, 0, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val timestamp = Timestamp(calendar.time)

    val result = timestampToString(timestamp)
    assertEquals("01/01/2023", result)
  }

  @Test
  fun `test timestampToString with custom pattern`() {
    val calendar = GregorianCalendar(2023, Calendar.JANUARY, 1, 0, 0, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val timestamp = Timestamp(calendar.time)

    val result = timestampToString(timestamp, "yyyy/MM/dd")
    assertEquals("2023/01/01", result)
  }

  @Test
  fun `test timestampToString with leap year date`() {
    val calendar = GregorianCalendar(2020, Calendar.FEBRUARY, 29, 0, 0, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val timestamp = Timestamp(calendar.time)

    val result = timestampToString(timestamp)
    assertEquals("29/02/2020", result)
  }

  @Test
  fun `test timestampToString with old date`() {
    val calendar = GregorianCalendar(1999, Calendar.DECEMBER, 31, 0, 0, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val timestamp = Timestamp(calendar.time)

    val result = timestampToString(timestamp)
    assertEquals("31/12/1999", result)
  }

  @Test
  fun `test timestampToString with different time zones`() {
    val calendar = GregorianCalendar(2023, Calendar.JANUARY, 1, 12, 0, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val timestamp = Timestamp(calendar.time)

    val result = timestampToString(timestamp, "yyyy-MM-dd HH:mm:ss")
    assertEquals("2023-01-01 12:00:00", result)
  }

  @Test
  fun `test valid email addresses`() {
    assertTrue(isValidEmail("test@example.com"))
    assertTrue(isValidEmail("user.name@example.co.uk"))
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
  fun `test valid date formats with correct days and leap year`() {
    // Valid dates
    assertTrue(isValidDate("01/01/2023")) // Regular date
    assertTrue(isValidDate("29/02/2020")) // Leap year
    assertTrue(isValidDate("31/12/1999")) // End of year
    assertTrue(isValidDate("30/04/2021")) // Valid day in April (30 days)
    assertTrue(isValidDate("28/02/2021")) // February in a non-leap year
  }

  @Test
  fun `test invalid date formats with incorrect days and leap year`() {
    // Invalid dates
    assertFalse(isValidDate("31/04/2023")) // April only has 30 days
    assertFalse(isValidDate("29/02/2021")) // 2021 is not a leap year
    assertFalse(isValidDate("32/01/2023")) // No month has 32 days
    assertFalse(isValidDate("00/01/2023")) // Day can't be zero
    assertFalse(isValidDate("15/13/2023")) // Invalid month
    assertFalse(isValidDate("31/02/2023")) // February never has 31 days
  }

  @Test
  fun `test invalid date formats`() {
    // Invalid formats
    assertFalse(isValidDate("2020/12/31")) // Wrong format (YYYY/MM/DD)
    assertFalse(isValidDate("31-12-2020")) // Wrong separator (- instead of /)
    assertFalse(isValidDate("12/31/2023")) // Wrong format (MM/DD/YYYY)
    assertFalse(isValidDate("31/02/2023")) // February can't have 31 days
  }

  @Test
  fun `test valid date strings for stringToTimestamp`() {
    val timestamp = stringToTimestamp("01/01/2023")
    val expectedCalendar = GregorianCalendar(2023, Calendar.JANUARY, 1, 0, 0, 0)
    expectedCalendar.set(Calendar.MILLISECOND, 0)
    val expectedTimestamp = Timestamp(expectedCalendar.time)
    assertEquals(expectedTimestamp, timestamp)

    val leapYearTimestamp = stringToTimestamp("29/02/2020")
    val expectedLeapYearCalendar = GregorianCalendar(2020, Calendar.FEBRUARY, 29, 0, 0, 0)
    expectedLeapYearCalendar.set(Calendar.MILLISECOND, 0)
    val expectedLeapYearTimestamp = Timestamp(expectedLeapYearCalendar.time)
    assertEquals(expectedLeapYearTimestamp, leapYearTimestamp)

    val oldDateTimestamp = stringToTimestamp("31/12/1999")
    val expectedOldDateCalendar = GregorianCalendar(1999, Calendar.DECEMBER, 31, 0, 0, 0)
    expectedOldDateCalendar.set(Calendar.MILLISECOND, 0)
    val expectedOldDateTimestamp = Timestamp(expectedOldDateCalendar.time)
    assertEquals(expectedOldDateTimestamp, oldDateTimestamp)
  }

  @Test
  fun `test invalid date strings for stringToTimestamp`() {
    assertNull(stringToTimestamp("32/01/2023")) // Invalid day
    assertNull(stringToTimestamp("31-12-1999")) // Incorrect format
    assertNull(stringToTimestamp("12/31/2023")) // MM/DD/YYYY format instead of DD/MM/YYYY
    assertNull(stringToTimestamp("29/02/2021")) // Not a leap year
  }

  @Test
  fun `test routeToScreen with HOME route`() {
    val result = routeToScreen(UserRoute.HOME)
    assertEquals(UserScreen.HOME, result)
  }

  @Test
  fun `test routeToScreen with CALENDAR route`() {
    val result = routeToScreen(UserRoute.CALENDAR)
    assertEquals(UserScreen.CALENDAR, result)
  }

  @Test
  fun `test routeToScreen with WELCOME route`() {
    val result = routeToScreen(NoModeRoute.WELCOME)
    assertEquals(NoModeScreen.WELCOME, result)
  }

  @Test
  fun `test routeToScreen with LOGIN route`() {
    val result = routeToScreen(NoModeRoute.LOGIN)
    assertEquals(NoModeScreen.LOGIN, result)
  }

  @Test
  fun `test routeToScreen with PASSWORD route`() {
    val result = routeToScreen(NoModeRoute.PASSWORD)
    assertEquals(NoModeScreen.PASSWORD, result)
  }

  @Test
  fun `test routeToScreen with SEARCH route`() {
    val result = routeToScreen(UserRoute.SEARCH)
    assertEquals(UserScreen.SEARCH, result)
  }

  @Test
  fun `test routeToScreen with DASHBOARD route`() {
    val result = routeToScreen(UserRoute.DASHBOARD)
    assertEquals(UserScreen.DASHBOARD, result)
  }

  @Test
  fun `test routeToScreen with PROFILE route`() {
    val result = routeToScreen(UserRoute.PROFILE)
    assertEquals(UserScreen.PROFILE, result)
  }

  @Test
  fun `test routeToScreen with invalid route defaults to WELCOME`() {
    val result = routeToScreen("INVALID_ROUTE")
    assertEquals(NoModeScreen.WELCOME, result)
  }

  @Test
  fun `test month 1 returns Jan`() {
    assertEquals("Jan", inToMonth(1))
  }

  @Test
  fun `test month 2 returns Feb`() {
    assertEquals("Feb", inToMonth(2))
  }

  @Test
  fun `test month 3 returns Mar`() {
    assertEquals("Mar", inToMonth(3))
  }

  @Test
  fun `test month 4 returns Apr`() {
    assertEquals("Apr", inToMonth(4))
  }

  @Test
  fun `test month 5 returns May`() {
    assertEquals("May", inToMonth(5))
  }

  @Test
  fun `test month 6 returns Jun`() {
    assertEquals("Jun", inToMonth(6))
  }

  @Test
  fun `test month 7 returns Jul`() {
    assertEquals("Jul", inToMonth(7))
  }

  @Test
  fun `test month 8 returns Aug`() {
    assertEquals("Aug", inToMonth(8))
  }

  @Test
  fun `test month 9 returns Sep`() {
    assertEquals("Sep", inToMonth(9))
  }

  @Test
  fun `test month 10 returns Oct`() {
    assertEquals("Oct", inToMonth(10))
  }

  @Test
  fun `test month 11 returns Nov`() {
    assertEquals("Nov", inToMonth(11))
  }

  @Test
  fun `test month 12 returns Dec`() {
    assertEquals("Dec", inToMonth(12))
  }

  @Test
  fun `test month 13 returns Default`() {
    assertEquals("Default", inToMonth(13))
  }

  @Test
  fun `test month 0 returns Default`() {
    assertEquals("Default", inToMonth(0))
  }

  @Test
  fun `test negative month returns Default`() {
    assertEquals("Default", inToMonth(-5))
  }
}
