package com.arygm.quickfix.ui.elements

import android.util.Log
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.ui.theme.QuickFixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixPriceRangeBottomSheet(
    showModalBottomSheet: Boolean,
    onApplyClick: (Int, Int) -> Unit,
    onDismissRequest: () -> Unit,
    onClearClick: () -> Unit,
    clearEnabled: Boolean
) {
  var range0 by remember { mutableIntStateOf(0) }
  var range1 by remember { mutableIntStateOf(0) }
  if (showModalBottomSheet) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.testTag("priceRangeModalSheet")) {
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
                        .testTag("priceRangeColumn"),
                horizontalAlignment = Alignment.CenterHorizontally) {
                  // Title of the sheet
                  Text(
                      text = "Price Range",
                      style = MaterialTheme.typography.headlineLarge,
                      color = colorScheme.outline,
                      modifier = Modifier.testTag("priceRangeTitle"))

                  Spacer(modifier = Modifier.height(verticalSpacing))

                  // Full-width divider under the title
                  Divider(
                      color = colorScheme.onSecondaryContainer,
                      thickness = 1.dp,
                      modifier = Modifier.fillMaxWidth())

                  Spacer(modifier = Modifier.height(verticalSpacing))

                  Row(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(vertical = verticalSpacing)
                              .testTag("priceRangeRow"),
                      horizontalArrangement =
                          Arrangement.spacedBy(paddingHorizontal, Alignment.CenterHorizontally),
                      verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            modifier = Modifier.weight(0.15f).testTag("leftPriceText"),
                            horizontalArrangement = Arrangement.End) {
                              Text(
                                  text = "0$",
                                  color = colorScheme.onBackground,
                                  style = TextStyle(fontWeight = FontWeight.SemiBold))
                            }

                        Row(modifier = Modifier.weight(0.70f).testTag("priceRangeSlider")) {
                          QuickFixPriceRange(
                              modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                              rangeColor = colorScheme.primary,
                              backColor = colorScheme.secondaryContainer,
                              barHeight = 8.dp,
                              circleRadius = 8.dp,
                              cornerRadius = CornerRadius(10f, 10f),
                              minValue = 0,
                              maxValue = 3000,
                              progress1InitialValue = 500,
                              progress2InitialValue = 2500,
                              tooltipSpacing = 5.dp,
                              tooltipWidth = 50.dp * widthRatio.value,
                              tooltipHeight = 30.dp * heightRatio.value,
                              tooltipTriangleSize = 5.dp,
                              onProgressChanged = { value1, value2 ->
                                range0 = value1
                                range1 = value2
                              })
                        }

                        Row(
                            modifier = Modifier.weight(0.15f).testTag("rightPriceText"),
                            horizontalArrangement = Arrangement.Start) {
                              Text(
                                  text = "3000$",
                                  color = colorScheme.onBackground,
                                  style = TextStyle(fontWeight = FontWeight.SemiBold))
                            }
                      }

                  Spacer(modifier = Modifier.height(verticalSpacing * 1.5f))

                  Button(
                      onClick = {
                        onApplyClick(range0, range1)
                        Log.d("hey", range0.toString())
                        Log.d("hey", range1.toString())
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

@Preview(showBackground = true)
@Composable
fun haha() {
  var showModal by remember { mutableStateOf(true) }

  QuickFixTheme {
    QuickFixPriceRangeBottomSheet(
        showModalBottomSheet = showModal,
        onApplyClick = { a, b ->
          Log.d("Chill Guy", a.toString())
          Log.d("Chill Guy", b.toString())
        },
        onDismissRequest = { showModal = false },
        onClearClick = {},
        clearEnabled = false)
  }
}
