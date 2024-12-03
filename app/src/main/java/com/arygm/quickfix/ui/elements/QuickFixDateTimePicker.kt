package com.arygm.quickfix.ui.elements

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.utils.MyAppTheme
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixDateTimePicker(
    onDateTimeSelected: (LocalDate, LocalTime) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    disableDates: List<LocalDate> = emptyList(),
    timeViewTest: Boolean = true
) {
  var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
  var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
  var showDatePicker by remember { mutableStateOf(true) }
  var showTimePicker by remember { mutableStateOf(false) }

  if (showDatePicker) {
    DatePickerDialog(
        onDismissRequest = {
          // Cancel the flow if dismissed
          showDatePicker = false
          onDismissRequest()
        },
        onDateSelected = { year, month, day -> selectedDate = LocalDate.of(year, month, day) },
        onFinishedRequest = {
          // Transition only if a date is selected
          showDatePicker = false
          showTimePicker = true
        },
        disableDates = disableDates)
  }

  if (showTimePicker) {
    TimePickerDialog(
        onDismissRequest = {
          showTimePicker = false
          onDismissRequest()
        },
        onTimeSelected = { hour, minute ->
          selectedTime = LocalTime.of(hour, minute)
          onDateTimeSelected(selectedDate!!, selectedTime!!)
          showTimePicker = false
        },
        timeViewTest = timeViewTest)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Int, Int, Int) -> Unit,
    onFinishedRequest: () -> Unit,
    disableDates: List<LocalDate> = emptyList(),
) {
  val useCaseState =
      rememberUseCaseState(
          visible = true,
          onDismissRequest = {
            // Handle cancel action explicitly
            onDismissRequest()
          },
      )
  MyAppTheme {
    CalendarDialog(
        state = useCaseState,
        selection =
            CalendarSelection.Date(
                onSelectDate = { localDate ->
                  // Trigger onDateSelected only when a date is selected
                  onDateSelected(localDate.year, localDate.monthValue, localDate.dayOfMonth)
                  onFinishedRequest()
                },
                selectedDate = LocalDate.now(),
                onNegativeClick = { onDismissRequest() }),
        config =
            CalendarConfig(
                locale = Locale.getDefault(),
                style = CalendarStyle.WEEK,
                cameraDate = LocalDate.now(),
                yearSelection = true,
                monthSelection = true,
                disabledDates = disableDates),
        header = Header.Default("Select Date"))
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    timeViewTest: Boolean
) {
  val timePickerState =
      rememberTimePickerState(
          initialHour = LocalTime.now().hour,
          initialMinute = LocalTime.now().minute,
          is24Hour = true)

  val displayModeState = remember { mutableStateOf(DisplayMode.Input) }

  AlertDialog(
      onDismissRequest = { onDismissRequest() },
      confirmButton = {
        TextButton(onClick = { onTimeSelected(timePickerState.hour, timePickerState.minute) }) {
          Text(
              "OK",
              fontSize = 16.sp,
          )
        }
      },
      dismissButton = {
        TextButton(onClick = { onDismissRequest() }) {
          Text(
              "Cancel",
              fontSize = 16.sp,
          )
        }
      },
      title = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()) {
              Text(
                  text = "Select Time",
              )
              DisplayModeToggleButton(
                  displayModeState = displayModeState, modifier = Modifier.padding(start = 8.dp))
            }
      },
      text = {
        Column {
          if (displayModeState.value == DisplayMode.Picker && timeViewTest) {
            TimeInput(
                state = timePickerState,
                modifier = Modifier.fillMaxWidth(),
                colors =
                    TimePickerDefaults.colors(
                        timeSelectorSelectedContainerColor = colorScheme.primary.copy(alpha = 0.3f),
                        timeSelectorSelectedContentColor = colorScheme.onBackground,
                        clockDialColor = colorScheme.primary.copy(alpha = 0.1f),
                        timeSelectorUnselectedContainerColor = Color(0xffe9e0e0),
                        timeSelectorUnselectedContentColor = colorScheme.onSecondaryContainer,
                        clockDialUnselectedContentColor = colorScheme.onBackground),
            )
          } else {
            TimePicker(
                state = timePickerState,
                modifier = Modifier.fillMaxWidth().testTag("TimePicker"),
                colors =
                    TimePickerDefaults.colors(
                        timeSelectorSelectedContainerColor = colorScheme.primary.copy(alpha = 0.3f),
                        timeSelectorSelectedContentColor = colorScheme.onBackground,
                        clockDialColor = colorScheme.primary.copy(alpha = 0.1f),
                        timeSelectorUnselectedContainerColor = Color(0xffe9e0e0),
                        timeSelectorUnselectedContentColor = colorScheme.onSecondaryContainer,
                        clockDialUnselectedContentColor = colorScheme.onBackground),
            )
          }
        }
      },
      containerColor = colorScheme.surface,
      titleContentColor = colorScheme.onBackground,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisplayModeToggleButton(
    displayModeState: MutableState<DisplayMode>,
    modifier: Modifier = Modifier,
) {
  when (displayModeState.value) {
    DisplayMode.Picker ->
        IconButton(
            modifier = modifier,
            onClick = { displayModeState.value = DisplayMode.Input },
            colors =
                IconButtonDefaults.iconButtonColors(
                    contentColor = colorScheme.primary,
                )) {
              Icon(
                  Icons.Default.AccessTime,
                  contentDescription = "time_picker_button_select_input_mode")
            }
    DisplayMode.Input ->
        IconButton(
            modifier = modifier,
            onClick = { displayModeState.value = DisplayMode.Picker },
            colors =
                IconButtonDefaults.iconButtonColors(
                    contentColor = colorScheme.primary,
                )) {
              Icon(
                  Icons.Default.Keyboard,
                  contentDescription = "time_picker_button_select_picker_mode")
            }
  }
}
