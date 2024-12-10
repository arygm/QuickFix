package com.arygm.quickfix.ui.quickfix

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.arygm.quickfix.R
import com.arygm.quickfix.model.bill.BillField
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import com.arygm.quickfix.model.bill.Units

@Composable
fun QuickFixThirdStep(
    quickFix: QuickFix,
    workerProfile: WorkerProfile,
) {
    val focusManager = LocalFocusManager.current
  val dateFormatter = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
  val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    var listDates by remember { mutableStateOf(emptyList<Timestamp>()) }
    var listBillFields by remember { mutableStateOf(emptyList<BillField>()) }
    var showSuggestedDates by remember { mutableStateOf(false) }

    if (showSuggestedDates) {
        SuggestedDatesDialog(
            quickFix = quickFix,
            onDismissRequest = { showSuggestedDates = false },
            onDatesSelected = { listDates = it },
            dateFormatter = dateFormatter,
            timeFormatter = timeFormatter,
            listDates = listDates
        )
    }
  BoxWithConstraints(
      modifier = Modifier.background(colorScheme.surface).pointerInput(Unit) {
          detectTapGestures(onTap = { focusManager.clearFocus() })
      },
  ) {
    val widthRatio = maxWidth / 411
    val heightRatio = maxHeight / 860

    LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp * widthRatio.value)) {
      item {
        Text(
            text = quickFix.title,
            style =
                poppinsTypography.bodyMedium.copy(
                    fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
            color = colorScheme.onBackground,
            modifier =
                Modifier.padding(
                    top = 16.dp * heightRatio.value, bottom = 4.dp * heightRatio.value))
      }

      item {
          Row(
              modifier =
              Modifier.fillMaxWidth()
                  .padding(bottom = 8.dp * heightRatio.value)
                  .offset(
                      x = (-4).dp * widthRatio.value, y = 0.dp * heightRatio.value
                  ),
              verticalAlignment = Alignment.CenterVertically
          ) {
              Icon(
                  painter = painterResource(id = R.drawable.calendar_today),
                  contentDescription = null,
                  tint = colorScheme.onSurface,
                  modifier = Modifier.padding(end = 5.dp * widthRatio.value)
              )

              Text(
                  text = "Date and time",
                  color = colorScheme.onSurface,
                  style =
                  poppinsTypography.bodyMedium.copy(
                      fontSize = 12.sp, fontWeight = FontWeight.Medium
                  ),
              )
          }
              if (listDates.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp * widthRatio.value, end = 8.dp * widthRatio.value)) {
                        Text(
                            text = "Day",
                            style = poppinsTypography.labelSmall,
                            color = colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(0.5f)
                        )
                        Text(
                            text = "Time",
                            style = poppinsTypography.labelSmall,
                            color = colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(0.5f)
                        )
                    }
              }
      }
      items(listDates.size) { index ->

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp * widthRatio.value, vertical = 4.dp * heightRatio.value)) {
              Text(
                  text = dateFormatter.format(listDates[index].toDate()),
                  style = poppinsTypography.labelSmall,
                  color = colorScheme.onBackground,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.weight(0.5f))
              Text(
                  text = timeFormatter.format(listDates[index].toDate()),
                  style = poppinsTypography.labelSmall,
                  color = colorScheme.onBackground,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.weight(0.5f))
            }
      }
        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { showSuggestedDates = true },
                    modifier = Modifier.padding(vertical = 8.dp * heightRatio.value),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.wrapContentWidth()) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = "Event",
                            tint = colorScheme.onPrimary,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (listDates.isEmpty()) "Select from Suggested Date(s)" else "Change Suggested Date(s)",
                            style = poppinsTypography.labelSmall,
                            color = colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
      item {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp * heightRatio.value, top = 16.dp * heightRatio.value),
            verticalAlignment = Alignment.CenterVertically) {
            Column {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = colorScheme.onSurface,
                    modifier = Modifier.padding(end = 8.dp * widthRatio.value).size(32.dp * widthRatio.value))
            }
            Column {
                Text(
                    text = "Location",
                    color = colorScheme.onSurface,
                    style =
                    poppinsTypography.bodyMedium.copy(
                        fontSize = 12.sp, fontWeight = FontWeight.Medium
                    ),
                )

                Text(
                    text = quickFix.location.name,
                    color = colorScheme.onBackground,
                    style =
                    poppinsTypography.bodyMedium.copy(
                        fontSize = 12.sp, fontWeight = FontWeight.Medium
                    )
                )
            }
        }
      }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp * heightRatio.value, top = 16.dp * heightRatio.value),
                verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = colorScheme.onSurface,
                        modifier = Modifier.padding(end = 8.dp * widthRatio.value).size(32.dp * widthRatio.value))
                }
                Column {
                    Text(
                        text = workerProfile.displayName,
                        color = colorScheme.onSurface,
                        style =
                        poppinsTypography.bodyMedium.copy(
                            fontSize = 12.sp, fontWeight = FontWeight.Medium
                        ),
                    )

                    Text(
                        text = workerProfile.fieldOfWork,
                        color = colorScheme.onBackground,
                        style =
                        poppinsTypography.bodyMedium.copy(
                            fontSize = 12.sp, fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
        item {
            Text(
                text = "Payment details",
                style =
                poppinsTypography.bodyMedium.copy(
                    fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
                color = colorScheme.onBackground,
                modifier =
                Modifier.padding(
                    top = 16.dp * heightRatio.value, bottom = 4.dp * heightRatio.value))
        }

        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp * widthRatio.value),
                    horizontalArrangement = Arrangement.SpaceBetween // Ensures even spacing
                ) {
                    Text(
                        text = "Description",
                        modifier = Modifier.weight(1.3f), // Give it the same weight as other columns
                        style = poppinsTypography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onBackground,
                        textAlign = TextAlign.Start // Align text to the right
                    )
                    Text(
                        text = "Unit",
                        modifier = Modifier.weight(1f), // Give it the same weight as other columns
                        style = poppinsTypography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onBackground,
                        textAlign = TextAlign.End // Align text to the right
                    )
                    Text(
                        text = "Amount",
                        modifier = Modifier.weight(1f),
                        style = poppinsTypography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onBackground,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "Unit Price",
                        modifier = Modifier.weight(1f),
                        style = poppinsTypography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onBackground,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "Total Price",
                        modifier = Modifier.weight(1f),
                        style = poppinsTypography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onBackground,
                        textAlign = TextAlign.End
                    )
                }
            }
        }

        items(listBillFields.size) { index ->
            val billField = listBillFields[index]
            var dropDownUnitExpanded by remember { mutableStateOf(false) }
            val total = billField.amount * billField.unitPrice
            var billDescription by remember { mutableStateOf(billField.description) }
            var billAmount by remember { mutableDoubleStateOf(0.0) }
            var billUnitPrice by remember { mutableDoubleStateOf(0.0) }
            var billTotal by remember { mutableDoubleStateOf(0.0) }
            var billUnit by remember { mutableStateOf<Units?>(null) }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp * widthRatio.value)) {
                QuickFixTextFieldCustom(
                    value = billDescription,
                    onValueChange = {
                        billDescription = it
                        listBillFields = listBillFields.toMutableList().also {
                            it[index] = billField.copy(description = it[index].description)
                        }
                    },
                    widthField = 140.dp * widthRatio.value,
                    alwaysShowTrailingIcon = false,
                    singleLine = false,
                    placeHolderText = "Add a description",
                    shape = RoundedCornerShape(10.dp),
                    showTrailingIcon = {false},
                    hasShadow = false,
                    borderColor = colorScheme.onSecondaryContainer,
                    borderThickness = 1.dp,
                    heightInEnabled = true
                )
                Box {
                QuickFixTextFieldCustom(
                    value = billUnit?.name ?: "",
                    onValueChange = {
                    },
                    widthField = 50.dp * widthRatio.value,
                    shape = RoundedCornerShape(10.dp),
                    showTrailingIcon = {false},
                    showLeadingIcon = {false},
                    hasShadow = false,
                    enabled = false,
                    borderColor = colorScheme.onSecondaryContainer,
                    borderThickness = 1.dp,
                    modifier = Modifier.clickable { dropDownUnitExpanded = true },
                    moveContentHorizontal = 10.dp * widthRatio.value,
                    scrollable = false
                )
                DropdownMenu(
                    expanded = dropDownUnitExpanded,
                    onDismissRequest = { dropDownUnitExpanded = false },
                    containerColor = colorScheme.background,
                    modifier = Modifier.border(1.dp, colorScheme.onSecondaryContainer, RoundedCornerShape(10.dp)).background(colorScheme.surface).width(70.dp * widthRatio.value)
                ) {
                    Units.entries.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(text = unit.name,
                                style = poppinsTypography.bodyMedium,
                                color = colorScheme.onBackground,) },
                            onClick = {
                                billUnit = unit
                                listBillFields = listBillFields.toMutableList()
                                    .also { it[index] = billField.copy(unit = unit) }
                                dropDownUnitExpanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = colorScheme.onBackground,
                            )
                        )
                    }
                }
                }
                QuickFixTextFieldCustom(
                    value = billAmount.takeIf { it != 0.0 }?.toString() ?: "",
                    onValueChange = {
                        Log.d("QuickFixThirdStep", "onValueChange: $it")
                        Log.d("QuickFixThirdStep", "onValueChange: ${it.isDigitsOnly()}")
                        if (it.isDigitsOnly()) {
                            billAmount = if (it.isEmpty()) 0.0 else it.toDouble()
                            listBillFields = listBillFields.toMutableList().also {
                                it[index] = billField.copy(amount = it[index].amount)
                            }
                        }
                    },
                    widthField = 50.dp * widthRatio.value,
                    showTrailingIcon = {false},
                    hasShadow = false,
                    borderColor = colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(10.dp),
                    borderThickness = 1.dp,
                    scrollable = false,
                    placeHolderText = "Amount"
                )
                QuickFixTextFieldCustom(
                    value = if (billUnitPrice == 0.0 || billUnitPrice.isNaN()) "" else billUnitPrice.toString(),
                    onValueChange = {
                        if (it.isDigitsOnly() && it.isNotEmpty()) {
                            billUnitPrice = it.toDouble()
                            listBillFields = listBillFields.toMutableList().also {
                                it[index] = billField.copy(unitPrice = it[index].unitPrice)
                            }
                        }
                    },
                    widthField = 50.dp * widthRatio.value,
                    showTrailingIcon = {false},
                    hasShadow = false,
                    placeHolderText = "Unit Price",
                    borderColor = colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(10.dp),
                    borderThickness = 1.dp,
                    scrollable = false

                )
                QuickFixTextFieldCustom(
                    value = if (billTotal == 0.0 || billTotal.isNaN()) "" else billTotal.toString(),
                    onValueChange = {
                        if (it.isDigitsOnly() && it.isNotEmpty()) {
                            billTotal = it.toDouble()
                            listBillFields = listBillFields.toMutableList().also {
                                it[index] = billField.copy(total = it[index].total)
                            }
                        }
                    },
                    widthField = 50.dp * widthRatio.value,
                    enabled = false,
                    showTrailingIcon = {false},
                    hasShadow = false,
                    placeHolderText = "Total",
                    borderColor = colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(10.dp),
                    borderThickness = 1.dp,
                    scrollable = false
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { listBillFields = listBillFields + BillField("",
                        Units.U, 1.0, 0.0, 0.0) },
                    modifier = Modifier.padding(vertical = 8.dp * heightRatio.value),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.wrapContentWidth()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = "ReceiptLong",
                            tint = colorScheme.onPrimary,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add A Bill Field",
                            style = poppinsTypography.labelSmall,
                            color = colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
  }
}

@Composable
fun SuggestedDatesDialog(
    quickFix: QuickFix,
    onDismissRequest: () -> Unit,
    onDatesSelected: (List<Timestamp>) -> Unit,
    dateFormatter: SimpleDateFormat,
    timeFormatter: SimpleDateFormat,
    listDates: List<Timestamp>
) {
    var selectedDates by remember { mutableStateOf(listDates) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Select Suggested Dates",
                color = colorScheme.onBackground,
                style = poppinsTypography.bodyMedium,
                fontSize = 12.sp)
        },
        text = {
            LazyColumn {
                items(quickFix.date.size) { index ->
                    val date = quickFix.date[index]
                    val isSelected = selectedDates.contains(date)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                selectedDates = if (isSelected) {
                                    selectedDates - date
                                } else {
                                    selectedDates + date
                                }
                            }
                        )
                        Text(
                            text = dateFormatter.format(date.toDate()),
                            style = poppinsTypography.bodyMedium,
                            color = colorScheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = timeFormatter.format(date.toDate()),
                            style = poppinsTypography.bodyMedium,
                            color = colorScheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDatesSelected(selectedDates)
                onDismissRequest()
            }) {
                Text(text = "OK", fontSize = 16.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel", fontSize = 16.sp)
            }
        },
        containerColor = colorScheme.surface
    )
}
