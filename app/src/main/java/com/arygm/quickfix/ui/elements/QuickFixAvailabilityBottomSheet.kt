package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.utils.MyAppTheme
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarView
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixAvailabilityBottomSheet(
    showModalBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    onOkClick: (List<LocalDate>, Int, Int) -> Unit
) {
  if (showModalBottomSheet) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.testTag("availabilityBottomSheet")) {
          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = 16.dp, vertical = 8.dp)
                      .testTag("bottomSheetColumn"),
              horizontalAlignment = Alignment.CenterHorizontally) {
                val currentTime = Calendar.getInstance()
                // State for the TimePicker
                val timePickerState =
                    rememberTimePickerState(
                        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                        initialMinute = currentTime.get(Calendar.MINUTE),
                        is24Hour = true // Set to false for 12-hour clock
                        )
                QuickFixTimePicker(timePickerState)
                MyAppTheme {
                  CalendarView(
                      useCaseState =
                          rememberUseCaseState(
                              visible = true, onCloseRequest = { onDismissRequest() }),
                      config =
                          CalendarConfig(
                              yearSelection = true,
                              monthSelection = true,
                              style = CalendarStyle.WEEK,
                          ),
                      selection =
                          CalendarSelection.Dates { newDates ->
                            onOkClick(newDates, timePickerState.hour, timePickerState.minute)
                            onDismissRequest()
                          })
                }
              }
        }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixTimePicker(timePickerState: TimePickerState) {
  Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("timePickerColumn"),
      horizontalAlignment = Alignment.CenterHorizontally) {
        // Title
        Text("Enter time", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // TimeInput for selecting time
        TimeInput(state = timePickerState, modifier = Modifier.testTag("timeInput"))
      }
}
