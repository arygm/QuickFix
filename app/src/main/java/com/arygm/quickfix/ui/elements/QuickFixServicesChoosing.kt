package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
@Composable
fun ChooseServiceTypeSheet(
    showModalBottomSheet: Boolean,
    serviceTypes: List<String>,
    selectedService: String,
    onServiceSelect: (String) -> Unit,
    onApplyClick: () -> Unit,
    onResetClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
  if (showModalBottomSheet) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
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
                  Row(
                      modifier = Modifier.fillMaxWidth().padding(verticalSpacing),
                      horizontalArrangement =
                          Arrangement.spacedBy(paddingHorizontal, Alignment.CenterHorizontally)) {
                        serviceTypes.forEach { service ->
                          val isSelected = service == selectedService
                          Text(
                              text = service,
                              style = MaterialTheme.typography.bodyMedium,
                              modifier =
                                  Modifier.clickable { onServiceSelect(service) }
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
                      onClick = onApplyClick,
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
                      text = "Reset",
                      color = colorScheme.onSecondaryContainer,
                      modifier =
                          Modifier.clickable { onResetClick() }
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
  var selectedService by remember { mutableStateOf("Exterior Painter") }
  var showModal by remember { mutableStateOf(true) }

  val serviceTypes = listOf("Exterior Painter", "Interior Painter")

  QuickFixTheme {
    ChooseServiceTypeSheet(
        showModalBottomSheet = showModal,
        serviceTypes = serviceTypes,
        selectedService = selectedService,
        onServiceSelect = { selectedService = it },
        onApplyClick = { /* Handle Apply */},
        onResetClick = { selectedService = "" },
        onDismissRequest = { showModal = false })
  }
}
