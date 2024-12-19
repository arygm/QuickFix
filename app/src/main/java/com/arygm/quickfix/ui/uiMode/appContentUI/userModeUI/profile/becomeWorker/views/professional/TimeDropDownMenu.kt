package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.becomeWorker.views.professional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDropdownMenu(
    modifier: Modifier = Modifier,
    selectedTime: MutableState<LocalTime?>, // LocalTime instead of String
    dependentTime: MutableState<LocalTime?> = mutableStateOf(null), // Dependent time for comparison
    isStartTime: Boolean = true, // If true, this is the Start Time dropdown
    showLabel: Boolean = true,
    widthRatio: Dp,
    heightRatio: Dp,
    startTime: LocalTime = LocalTime.of(0, 0),
    tag: String
) {
  var expanded by remember { mutableStateOf(false) }

  // Generate time slots with condition based on Start or End Time
  val timeSlots =
      if (isStartTime) {
        generateTimeSlotsAsLocalTime(startTime) // For start time, use all slots
      } else {
        generateTimeSlotsAsLocalTime(dependentTime.value ?: startTime).filter { it > startTime }
      }

  val listState = rememberLazyListState()
  val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

  // Text Field Trigger
  QuickFixTextFieldCustom(
      scrollable = false,
      showLeadingIcon = { false },
      spaceBetweenLeadIconText = 8.dp,
      modifier = modifier.semantics { testTag = "TimeDropdownMenuField$tag" },
      widthField = 84.dp * widthRatio.value,
      heightField = 30.dp * heightRatio.value,
      value = selectedTime.value?.format(timeFormatter) ?: "", // Show selected time formatted
      onValueChange = {}, // Not editable
      shape = RoundedCornerShape(8.dp),
      showLabel = showLabel,
      label =
          @Composable {
            Text(
                text =
                    buildAnnotatedString {
                      append("Working Hours")
                      withStyle(
                          style =
                              SpanStyle(
                                  color = MaterialTheme.colorScheme.primary,
                                  fontSize = 12.sp,
                                  fontWeight = FontWeight.Medium)) {
                            append("*")
                          }
                    },
                style =
                    MaterialTheme.typography.labelMedium.copy(
                        fontSize = 12.sp, fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onBackground)
          },
      hasShadow = false,
      borderColor = MaterialTheme.colorScheme.tertiaryContainer,
      placeHolderText = "00:00",
      isTextField = false,
      onTextFieldClick = { expanded = !expanded } // Toggle dropdown menu
      )

  // Dropdown Menu
  DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      modifier =
          Modifier.heightIn(max = 240.dp) // Max height to fit ~6 rows
              .semantics { testTag = "TimeDropdownMenu$tag" },
      containerColor = colorScheme.surface) {
        Box(modifier = Modifier.size(width = 80.dp * widthRatio.value, height = 300.dp)) {
          LazyColumn(state = listState, modifier = Modifier.fillMaxWidth()) {
            items(timeSlots) { time ->
              DropdownMenuItem(
                  text = {
                    Text(
                        text = time.format(timeFormatter), // Format LocalTime
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center))
                  },
                  onClick = {
                    selectedTime.value = time // Update the selected LocalTime
                    if (isStartTime && dependentTime.value != null && time >= dependentTime.value) {
                      dependentTime.value = null // Reset the end time if invalid
                    }
                    expanded = false // Close the dropdown
                  },
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(40.dp) // Fixed height per row
                          .padding(horizontal = 8.dp)
                          .semantics {
                            testTag = "TimeDropdownMenuItem_${time.format(timeFormatter)}"
                          })
            }
          }
        }
      }
}
// Function to generate time slots as LocalTime
fun generateTimeSlotsAsLocalTime(startTime: LocalTime): List<LocalTime> {
  val endTime = LocalTime.of(23, 45) // End at 23:45

  val timeSlots = mutableListOf<LocalTime>()
  var currentTime = startTime
  while (currentTime <= endTime) {
    timeSlots.add(currentTime)
    currentTime = currentTime.plusMinutes(15)

    // Break if the currentTime exceeds 24 hours to avoid infinite loop
    if (currentTime.hour == 0 && currentTime.minute == 0) break
  }
  return timeSlots
}
