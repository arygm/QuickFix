package com.arygm.quickfix.ui.elements

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.arygm.quickfix.utils.MyAppTheme
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker() {
  val selectedDates = remember { mutableStateOf<List<LocalDate>>(listOf()) }
  CalendarDialog(
      state = rememberUseCaseState(visible = true, onCloseRequest = {}),
      config =
          CalendarConfig(
              yearSelection = true,
              monthSelection = true,
              style = CalendarStyle.WEEK,
          ),
      selection = CalendarSelection.Dates { newDates -> selectedDates.value = newDates },
  )
}

@Composable
fun QuicFixDatePicker() {
  MyAppTheme { DatePicker() }
}
