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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.ui.theme.poppinsTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixLocationFilterBottomSheet(
    showModalBottomSheet: Boolean,
    userProfile: UserProfile,
    phoneLocation: Location,
    selectedLocationIndex: Int? = null,
    onApplyClick: (Location, Int) -> Unit,
    onDismissRequest: () -> Unit,
    onClearClick: () -> Unit,
    clearEnabled: Boolean,
    end: Int = 200
) {
  var range by remember { mutableIntStateOf(0) }
  if (showModalBottomSheet) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.testTag("locationFilterModalSheet")) {
          BoxWithConstraints {
            val paddingHorizontal = maxWidth * 0.04f
            val verticalSpacing = maxHeight * 0.015f
            val cornerRadius = maxWidth * 0.05f
            val buttonPaddingHorizontal = maxWidth * 0.04f

            val widthRatio = maxWidth / 411
            val heightRatio = maxHeight / 860

            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .background(
                            colorScheme.surface,
                            RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
                        .padding(horizontal = 0.dp)
                        .testTag("locationFilterColumn"),
                horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                      text = "Search Radius",
                      style = MaterialTheme.typography.headlineLarge,
                      color = colorScheme.outline,
                      modifier = Modifier.testTag("locationFilterTitle"))

                  Spacer(modifier = Modifier.height(verticalSpacing))

                  HorizontalDivider(
                      modifier = Modifier.fillMaxWidth().testTag("locationFilterDivider"),
                      thickness = 1.dp,
                      color = colorScheme.onSecondaryContainer)

                  Spacer(modifier = Modifier.height(verticalSpacing))

                  val locationOptions = mutableListOf("Use my Current Location")
                  userProfile.locations.forEach { loc -> locationOptions += loc.name }
                  val selectedOption = remember { mutableStateOf<Int?>(selectedLocationIndex) }

                  val maxListHeight = heightRatio * 800 * 0.5f

                  Text(
                      text = "Location Options:",
                      style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp),
                      color = colorScheme.onBackground,
                      fontWeight = FontWeight.SemiBold,
                      modifier =
                          Modifier.align(Alignment.Start)
                              .padding(horizontal = 16.dp)
                              .testTag("locationOptionsTitle"))

                  Spacer(modifier = Modifier.height(verticalSpacing))

                  LazyColumn(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(horizontal = 16.dp)
                              .heightIn(max = maxListHeight)
                              .testTag("locationOptionsList")) {
                        items(locationOptions.size) { index ->
                          val optionText = locationOptions[index]
                          Column(modifier = Modifier.testTag("locationOptionContainer$index")) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .padding(vertical = 3.dp * heightRatio.value)
                                        .toggleable(
                                            value = selectedOption.value == index,
                                            onValueChange = { selectedOption.value = index },
                                            enabled = true,
                                            role = Role.RadioButton)
                                        .testTag("locationOptionRow$index")) {
                                  RadioButton(
                                      selected = (selectedOption.value == index),
                                      onClick = { selectedOption.value = index },
                                      modifier =
                                          Modifier.size(
                                                  width = 24.dp * widthRatio.value,
                                                  height = 24.dp * heightRatio.value)
                                              .testTag("locationOptionRadio$index"),
                                      colors =
                                          RadioButtonDefaults.colors(
                                              selectedColor = colorScheme.primary,
                                              unselectedColor = colorScheme.tertiaryContainer))

                                  Spacer(modifier = Modifier.width(8.dp * widthRatio.value))

                                  Text(
                                      text = optionText,
                                      style = poppinsTypography.labelSmall,
                                      fontWeight = FontWeight.Medium,
                                      color = colorScheme.onBackground,
                                      modifier = Modifier.testTag("locationOptionText$index"))
                                }

                            if (index < locationOptions.lastIndex) {
                              Spacer(modifier = Modifier.height(8.dp * heightRatio.value))
                              HorizontalDivider(
                                  color = colorScheme.background,
                                  thickness = 1.5.dp,
                                  modifier =
                                      Modifier.padding(start = 32.dp * widthRatio.value)
                                          .testTag("locationOptionDivider$index"))
                              Spacer(modifier = Modifier.height(8.dp * heightRatio.value))
                            }
                          }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                      }

                  Row(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(vertical = verticalSpacing)
                              .testTag("priceRangeRow"),
                      horizontalArrangement =
                          Arrangement.spacedBy(paddingHorizontal, Alignment.CenterHorizontally),
                      verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            modifier = Modifier.weight(0.15f).testTag("leftDistText"),
                            horizontalArrangement = Arrangement.End) {
                              Text(
                                  text = "0 km",
                                  color = colorScheme.onBackground,
                                  style = TextStyle(fontWeight = FontWeight.SemiBold),
                                  modifier = Modifier.testTag("leftDistTextValue"))
                            }

                        Row(modifier = Modifier.weight(0.70f).testTag("locationFilterSlider")) {
                          QuickFixPriceRange(
                              modifier =
                                  Modifier.fillMaxWidth()
                                      .padding(horizontal = 8.dp)
                                      .testTag("quickFixPriceRange"),
                              rangeColor = colorScheme.primary,
                              backColor = colorScheme.secondaryContainer,
                              barHeight = 8.dp,
                              circleRadius = 8.dp,
                              cornerRadius = CornerRadius(10f, 10f),
                              minValue = 0,
                              maxValue = 800,
                              progress1InitialValue = end,
                              progress2InitialValue = 900,
                              tooltipSpacing = 5.dp,
                              tooltipWidth = 50.dp * widthRatio.value,
                              tooltipHeight = 30.dp * heightRatio.value,
                              tooltipTriangleSize = 5.dp,
                              isDoubleSlider = false,
                              onProgressChanged = { value1, _ -> range = value1 })
                        }

                        Row(
                            modifier = Modifier.weight(0.15f).testTag("rightPriceDistText"),
                            horizontalArrangement = Arrangement.Start) {
                              Text(
                                  text = "800 km",
                                  color = colorScheme.onBackground,
                                  style = TextStyle(fontWeight = FontWeight.SemiBold),
                                  modifier = Modifier.testTag("rightDistTextValue"))
                            }
                      }

                  Spacer(modifier = Modifier.height(verticalSpacing * 1.5f))

                  Button(
                      enabled = selectedOption.value != null,
                      onClick = {
                        if (selectedOption.value == 0) {
                          onApplyClick(phoneLocation, range)
                        } else {
                          onApplyClick(
                              userProfile.locations[selectedOption.value!!.minus(1)], range)
                        }
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
                        Text("Apply", modifier = Modifier.testTag("applyButtonText"))
                      }
                  Text(
                      text = "Clear",
                      color =
                          if (clearEnabled) colorScheme.primary
                          else colorScheme.onSecondaryContainer,
                      modifier =
                          Modifier.clickable(enabled = clearEnabled) {
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
