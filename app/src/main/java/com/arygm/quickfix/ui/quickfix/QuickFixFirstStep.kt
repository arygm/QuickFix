package com.arygm.quickfix.ui.quickfix

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.arygm.quickfix.ui.elements.QuickFixCheckedListElement
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.utils.MyAppTheme
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarView
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun QuickFixFirstStep() {

  val focusManager = LocalFocusManager.current

  var quickFixTile by remember { mutableStateOf("") }
  val listServices = listOf("Service 1", "Service 2", "Service 3", "Service 4", "Service 5")
  val checkedStatesServices = remember { mutableStateListOf(*Array(listServices.size) { false }) }

  val listAddOnServices =
      listOf(
          "Add-on Service 1",
          "Add-on Service 2",
          "Add-on Service 3",
          "Add-on Service 4",
          "Add-on Service 5")
  val checkedStatesAddOnServices = remember {
    mutableStateListOf(*Array(listAddOnServices.size) { false })
  }

  var quickNote by remember { mutableStateOf("") }
  val selectedDates = remember { mutableStateOf<List<LocalDate>>(emptyList()) }

  BoxWithConstraints(
      modifier =
          Modifier.background(colorScheme.surface).pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
          }) {
        val widthRatio = maxWidth / 411
        val heightRatio = maxHeight / 860
        val sizeRatio = minOf(widthRatio, heightRatio)
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
          item {
            QuickFixTextFieldCustom(
                value = quickFixTile,
                onValueChange = {
                  quickFixTile = it
                  // update the quickfix tile
                },
                placeHolderText = "Enter a title ...",
                placeHolderColor = colorScheme.onSecondaryContainer,
                label =
                    @Composable {
                      Text(
                          text = "Title",
                          style = poppinsTypography.labelSmall,
                          fontWeight = FontWeight.Medium,
                          color = colorScheme.onBackground,
                          modifier = Modifier.padding(horizontal = 4.dp))
                    },
                showLabel = true,
                shape = RoundedCornerShape(5.dp),
                widthField = 400 * widthRatio,
                moveContentHorizontal = 10.dp,
                borderColor = colorScheme.tertiaryContainer,
                borderThickness = 1.625.dp,
                hasShadow = false,
                textStyle =
                    poppinsTypography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                    ),
            )
          }

          item {
            Text(
                text = "Features services",
                style = poppinsTypography.labelSmall,
                color = colorScheme.onBackground,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 10.dp))
          }

          items(listServices.size) { index ->
            QuickFixCheckedListElement(listServices, checkedStatesServices, index)
          }

          item {
            Text(
                text = "Add-on services",
                style = poppinsTypography.labelSmall,
                color = colorScheme.onBackground,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 4.dp))
          }

          items(listAddOnServices.size) { index ->
            QuickFixCheckedListElement(listAddOnServices, checkedStatesAddOnServices, index)
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          item {
            QuickFixTextFieldCustom(
                heightField = 150.dp,
                widthField = 400.dp * widthRatio.value,
                value = quickNote,
                onValueChange = { quickNote = it },
                shape = RoundedCornerShape(8.dp),
                showLabel = true,
                label =
                    @Composable {
                      Text(
                          text =
                              buildAnnotatedString {
                                append("Quick note")
                                withStyle(
                                    style =
                                        SpanStyle(
                                            color = colorScheme.tertiaryContainer,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium)) {
                                      append(" (optional)")
                                    }
                              },
                          style =
                              poppinsTypography.headlineMedium.copy(
                                  fontSize = 12.sp, fontWeight = FontWeight.Medium),
                          color = colorScheme.onBackground,
                          modifier = Modifier.padding(bottom = 8.dp, start = 4.dp, top = 4.dp))
                    },
                hasShadow = false,
                borderColor = colorScheme.tertiaryContainer,
                placeHolderText = "Type a description...",
                maxChar = 1500,
                showCharCounter = true,
                moveCounter = 17.dp,
                charCounterTextStyle =
                    poppinsTypography.headlineMedium.copy(
                        fontSize = 12.sp, fontWeight = FontWeight.Medium),
                charCounterColor = colorScheme.onSecondaryContainer,
                singleLine = false,
                showLeadingIcon = { false },
                showTrailingIcon = { false },
            )
          }

          item {
            MyAppTheme {
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(280.dp) // Set a specific height to avoid infinite constraints
                  ) {
                    CalendarView(
                        useCaseState =
                            rememberUseCaseState(
                                visible = true, onCloseRequest = { /* Handle close request */}),
                        config =
                            CalendarConfig(
                                yearSelection = true,
                                monthSelection = true,
                                style = CalendarStyle.WEEK),
                        selection =
                            CalendarSelection.Dates { newDates -> selectedDates.value = newDates },
                        header =
                            Header.Default(
                                title = "Date and time",
                                icon = IconSource(Icons.Default.CalendarMonth),
                            ),
                    )
                  }
            }
          }

          item {
            MyAppTheme {
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(300.dp) // Set a specific height to avoid infinite constraints
                  ) {
                    CalendarView(
                        useCaseState =
                            rememberUseCaseState(
                                visible = true, onCloseRequest = { /* Handle close request */}),
                        config =
                            CalendarConfig(
                                yearSelection = true,
                                monthSelection = true,
                                style = CalendarStyle.WEEK),
                        selection =
                            CalendarSelection.Dates { newDates -> selectedDates.value = newDates },
                        header =
                            Header.Default(
                                title = "Date and time",
                                icon = IconSource(Icons.Default.CalendarMonth),
                            ),
                    )
                  }
            }
          }
        }
      }
}
