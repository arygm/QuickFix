package com.arygm.quickfix.utils

import android.util.Log
import com.arygm.quickfix.ui.navigation.Route
import com.arygm.quickfix.ui.navigation.Screen
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.regex.Pattern

fun isValidEmail(email: String): Boolean {
  val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
  return Pattern.matches(emailPattern, email.trim())
}

fun isValidDate(date: String): Boolean {
  // Simple check for DD/MM/YYYY format
  val datePattern = "^([0-2][0-9]|(3)[0-1])/((0)[1-9]|(1)[0-2])/((19|20)\\d\\d)$"
  if (!Pattern.matches(datePattern, date.trim())) {
    return false
  }

  val (day, month, year) = splitDate(date)
  val isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

  val daysInMonth =
      when (month) {
        1,
        3,
        5,
        7,
        8,
        10,
        12 -> 31
        4,
        6,
        9,
        11 -> 30
        2 -> if (isLeapYear) 29 else 28
        else -> return false
      }

  return day in 1..daysInMonth
}

fun stringToTimestamp(date: String): Timestamp? {
  // First validate the date format and value using isValidDate
  if (!isValidDate(date)) {
    Log.e("DateConversion", "Invalid date format or value: $date")
    return null
  }
  val (day, month, year) = splitDate(date)
  val calendar =
      GregorianCalendar(year, month - 1, day).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }
  return Timestamp(calendar.time)
}

fun timestampToString(timestamp: Timestamp, pattern: String = "dd-MM-yyyy"): String {
  Log.d("DateConversion", "Converting timestamp: $timestamp")
  val date = timestamp.toDate() // Converts Firebase Timestamp to a Java Date object
  val formatter = SimpleDateFormat(pattern, Locale.getDefault()) // Format the Date
  val ret = formatter.format(date)
  Log.d("DateConversion", "Converted date: $ret")
  return ret
}

fun splitDate(date: String): Triple<Int, Int, Int> {
  val dateParts = date.split("/")
  val day = dateParts[0].toInt()
  val month = dateParts[1].toInt()
  val year = dateParts[2].toInt()
  return Triple(day, month, year)
}

fun routeToScreen(route: String): String {
  return when (route) {
    Route.HOME -> Screen.HOME
    Route.CALENDAR -> Screen.CALENDAR
    Route.WELCOME -> Screen.WELCOME
    Route.INFO -> Screen.INFO
    Route.LOGIN -> Screen.LOGIN
    Route.PASSWORD -> Screen.PASSWORD
    Route.SEARCH -> Screen.SEARCH
    Route.DASHBOARD -> Screen.DASHBOARD
    Route.PROFILE -> Screen.PROFILE
    Route.ACCOUNT_CONFIGURATION -> Screen.ACCOUNT_CONFIGURATION
    else -> {
      Screen.WELCOME
    }
  }
}

fun screenToRoute(route: String): String {
  return when (route) {
    Screen.HOME -> Route.HOME
    Screen.CALENDAR -> Route.CALENDAR
    Screen.WELCOME -> Route.WELCOME
    Screen.INFO -> Route.INFO
    Screen.LOGIN -> Route.LOGIN
    Screen.PASSWORD -> Route.PASSWORD
    Screen.SEARCH -> Route.SEARCH
    Screen.DASHBOARD -> Route.DASHBOARD
    Screen.PROFILE -> Route.PROFILE
    Screen.ACCOUNT_CONFIGURATION -> Route.ACCOUNT_CONFIGURATION
    else -> {
      Route.WELCOME
    }
  }
}
