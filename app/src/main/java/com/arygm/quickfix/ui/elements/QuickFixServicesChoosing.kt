package com.arygm.quickfix.ui.elements

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.ui.theme.QuickFixTheme

/**
 * A composable function that displays a modal sheet for selecting a service type. It includes a
 * list of service options, an "Apply" button, and a "Reset" option. The sheet uses relative
 * dimensions for responsiveness across various screen sizes.
 *
 * @param showModalBottomSheet Controls the visibility of the modal sheet.
 * @param serviceTypes List of service types to display as selectable options.
 * @param selectedService The currently selected service.
 * @param onServiceSelect Callback triggered when a service option is selected.
 * @param onApplyClick Callback triggered when the "Apply" button is clicked.
 * @param onResetClick Callback triggered when the "Reset" text is clicked.
 * @param onDismissRequest Callback triggered when the modal sheet is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseServiceTypeSheet(
    showModalBottomSheet: Boolean,
    serviceTypes: List<String>,
    onApplyClick: (List<String>) -> Unit,
    onDismissRequest: () -> Unit,
    onClearClick: () -> Unit,
    clearEnabled: Boolean
) {
  var selectedServices by remember { mutableStateOf(emptyList<String>()) }
  if (showModalBottomSheet) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.testTag("chooseServiceTypeModalSheet")) {
          BoxWithConstraints {
            val paddingHorizontal = maxWidth * 0.04f // Relative horizontal padding (4% of width)
            val verticalSpacing = maxHeight * 0.015f // Relative vertical spacing (1.5% of height)
            val cornerRadius = maxWidth * 0.05f // Rounded corner radius (5% of width)
            val buttonPaddingHorizontal = maxWidth * 0.04f // Button padding (4% of width)

            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .background(
                            colorScheme.surface,
                            RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
                        .padding(horizontal = 0.dp)
                        .testTag("chooseServiceTypeDialog"),
                horizontalAlignment = Alignment.CenterHorizontally) {

                  // Title of the sheet
                  Text(
                      text = "Service Type",
                      style = MaterialTheme.typography.headlineLarge,
                      color = colorScheme.outline,
                      modifier = Modifier.testTag("serviceTypeText"))

                  Spacer(modifier = Modifier.height(verticalSpacing)) // Space below title

                  // Full-width divider under the title
                  Divider(
                      color = colorScheme.onSecondaryContainer,
                      thickness = 1.dp,
                      modifier = Modifier.fillMaxWidth())

                  Spacer(modifier = Modifier.height(verticalSpacing)) // Space below divider

                  // Display each service option in a row
                  LazyRow(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(vertical = verticalSpacing)
                              .testTag("lazyServiceRow"),
                      horizontalArrangement =
                          Arrangement.spacedBy(paddingHorizontal, Alignment.CenterHorizontally)) {
                        items(serviceTypes) { service ->
                          val isSelected = service in selectedServices
                          Text(
                              text = service,
                              style = MaterialTheme.typography.bodyMedium,
                              modifier =
                                  Modifier.clickable {
                                        selectedServices =
                                            if (isSelected) {
                                              selectedServices -
                                                  service // Remove if already selected
                                            } else {
                                              selectedServices + service // Add if not selected
                                            }
                                      }
                                      .background(
                                          color =
                                              if (isSelected) colorScheme.primary
                                              else colorScheme.secondary,
                                          shape = RoundedCornerShape(cornerRadius))
                                      .padding(
                                          horizontal =
                                              paddingHorizontal *
                                                  1.5f, // Extra padding inside each option
                                          vertical = verticalSpacing)
                                      .testTag("serviceText_$service"),
                              color =
                                  if (isSelected) colorScheme.onPrimary
                                  else colorScheme.onSecondary)
                        }
                      }

                  Spacer(
                      modifier =
                          Modifier.height(verticalSpacing * 1.5f)) // Space before Apply button

                  // Apply button to confirm the selection
                  Button(
                      onClick = {
                        onApplyClick(selectedServices)
                        onDismissRequest()
                      },
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(horizontal = buttonPaddingHorizontal)
                              .testTag("applyButton"),
                      colors =
                          androidx.compose.material3.ButtonDefaults.buttonColors(
                              containerColor = colorScheme.primary,
                              contentColor = colorScheme.onPrimary)) {
                        Text("Apply")
                      }

                  Spacer(
                      modifier = Modifier.height(verticalSpacing)) // Space between Apply and Reset

                  // Reset option to clear the selection
                  Text(
                      text = "Clear",
                      color =
                          if (clearEnabled) colorScheme.primary
                          else colorScheme.onSecondaryContainer,
                      modifier =
                          Modifier.clickable(enabled = clearEnabled) {
                                selectedServices = emptyList<String>()
                                onClearClick()
                                onDismissRequest()
                              }
                              .padding(vertical = verticalSpacing / 2)
                              .testTag("resetButton"))
                }
          }
        }
  }
}

/**
 * Preview function to display the ChooseServiceTypeSheet in the UI editor. Sets up initial values
 * for testing and previewing the component.
 */
@Preview(showBackground = true)
@Composable
fun ChooseServiceTypePreview() {
  var showModal by remember { mutableStateOf(true) }

  val serviceTypes = listOf("Exterior Painter", "Interior Painter")

  QuickFixTheme {
    ChooseServiceTypeSheet(
        showModalBottomSheet = showModal,
        serviceTypes = serviceTypes,
        onApplyClick = { l -> l.forEach { it -> Log.d("Chill Guy", it) } },
        onDismissRequest = { showModal = false },
        onClearClick = {},
        clearEnabled = false)
  }
}
