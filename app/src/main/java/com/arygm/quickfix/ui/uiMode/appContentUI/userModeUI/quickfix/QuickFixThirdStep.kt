package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.quickfix

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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.R
import com.arygm.quickfix.model.bill.BillField
import com.arygm.quickfix.model.bill.Units
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.USER_TOP_LEVEL_DESTINATIONS
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WORKER_TOP_LEVEL_DESTINATIONS
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun QuickFixThirdStep(
    quickFixViewModel: QuickFixViewModel,
    navigationActionsRoot: NavigationActions,
    workerProfile: WorkerProfile,
    onQuickFixChange: (QuickFix) -> Unit,
    onQuickFixPay: (QuickFix) -> Unit,
    mode: AppMode
) {
  val focusManager = LocalFocusManager.current
  val dateFormatter = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
  val quickFix by quickFixViewModel.currentQuickFix.collectAsState()
  val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
  var listDates by remember { mutableStateOf(emptyList<Timestamp>()) }
  var listBillFields by remember { mutableStateOf(emptyList<BillField>()) }
  var showSuggestedDates by remember { mutableStateOf(false) }
  val numberPattern = remember { Regex("^\\d*\\.?\\d*\$") }

  fun updateBillField(index: Int, list: List<BillField>, update: (BillField) -> BillField) {
    listBillFields = list.toMutableList().apply { this[index] = update(this[index]) }
  }

  if (showSuggestedDates) {
    SuggestedDatesDialog(
        quickFix = quickFix,
        onDismissRequest = { showSuggestedDates = false },
        onDatesSelected = { listDates = it },
        dateFormatter = dateFormatter,
        timeFormatter = timeFormatter,
        listDates = listDates,
    )
  }
  BoxWithConstraints(
      modifier =
          Modifier.background(colorScheme.surface).pointerInput(Unit) {
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
                    .offset(x = (-4).dp * widthRatio.value, y = 0.dp * heightRatio.value),
            verticalAlignment = Alignment.CenterVertically) {
              Icon(
                  painter = painterResource(id = R.drawable.calendar_today),
                  contentDescription = null,
                  tint = colorScheme.onSurface,
                  modifier = Modifier.padding(end = 5.dp * widthRatio.value))

              Text(
                  text = "Date and time",
                  color = colorScheme.onSurface,
                  style =
                      poppinsTypography.bodyMedium.copy(
                          fontSize = 12.sp, fontWeight = FontWeight.Medium),
              )
            }
        if (listDates.isNotEmpty()) {
          Row(
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(start = 8.dp * widthRatio.value, end = 8.dp * widthRatio.value)) {
                Text(
                    text = "Day",
                    style = poppinsTypography.labelSmall,
                    color = colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(0.5f))
                Text(
                    text = "Time",
                    style = poppinsTypography.labelSmall,
                    color = colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(0.5f))
              }
        }
      }
      items(if (quickFix.status == Status.PENDING) listDates.size else quickFix.date.size) { index
        ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier =
                Modifier.fillMaxWidth()
                    .padding(
                        horizontal = 8.dp * widthRatio.value,
                        vertical = 4.dp * heightRatio.value)) {
              Text(
                  text =
                      dateFormatter.format(
                          if (quickFix.status == Status.PENDING) listDates[index].toDate()
                          else quickFix.date[index].toDate()),
                  style = poppinsTypography.labelSmall,
                  color = colorScheme.onBackground,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.weight(0.5f).testTag("DateText_$index"))
              Text(
                  text =
                      timeFormatter.format(
                          if (quickFix.status == Status.PENDING) listDates[index].toDate()
                          else quickFix.date[index].toDate()),
                  style = poppinsTypography.labelSmall,
                  color = colorScheme.onBackground,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.weight(0.5f).testTag("TimeText_$index"))
            }
      }
      if (quickFix.status == Status.PENDING) {
        item {
          Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { showSuggestedDates = true },
                modifier =
                    Modifier.padding(vertical = 8.dp * heightRatio.value)
                        .testTag("SelectSuggestedDatesButton"),
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
                        text =
                            if (listDates.isEmpty()) "Select from Suggested Date(s)"
                            else "Change Suggested Date(s)",
                        style = poppinsTypography.labelSmall,
                        color = colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold)
                  }
            }
          }
        }
      }

      item {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(bottom = 4.dp * heightRatio.value, top = 16.dp * heightRatio.value),
            verticalAlignment = Alignment.CenterVertically) {
              Column {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = colorScheme.onSurface,
                    modifier =
                        Modifier.padding(end = 8.dp * widthRatio.value)
                            .size(32.dp * widthRatio.value))
              }
              Column {
                Text(
                    text = "Location",
                    color = colorScheme.onSurface,
                    style =
                        poppinsTypography.bodyMedium.copy(
                            fontSize = 12.sp, fontWeight = FontWeight.Medium),
                )

                Text(
                    text = quickFix.location.name,
                    color = colorScheme.onBackground,
                    style =
                        poppinsTypography.bodyMedium.copy(
                            fontSize = 12.sp, fontWeight = FontWeight.Medium))
              }
            }
      }

      item {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(bottom = 4.dp * heightRatio.value, top = 16.dp * heightRatio.value),
            verticalAlignment = Alignment.CenterVertically) {
              Column {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = colorScheme.onSurface,
                    modifier =
                        Modifier.padding(end = 8.dp * widthRatio.value)
                            .size(32.dp * widthRatio.value))
              }
              Column {
                Text(
                    text = workerProfile.displayName,
                    color = colorScheme.onSurface,
                    style =
                        poppinsTypography.bodyMedium.copy(
                            fontSize = 12.sp, fontWeight = FontWeight.Medium),
                )

                Text(
                    text = workerProfile.fieldOfWork,
                    color = colorScheme.onBackground,
                    style =
                        poppinsTypography.bodyMedium.copy(
                            fontSize = 12.sp, fontWeight = FontWeight.Medium))
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
                    modifier = Modifier.weight(2f), // Give it the same weight as other columns
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
                    textAlign = TextAlign.End)
                Text(
                    text = "Unit Price",
                    modifier = Modifier.weight(1f),
                    style = poppinsTypography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onBackground,
                    textAlign = TextAlign.End)
                Text(
                    text = "Total",
                    modifier = Modifier.weight(1f),
                    style = poppinsTypography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onBackground,
                    textAlign = TextAlign.End)
              }
        }
      }

      items(
          if (quickFix.status == Status.PENDING) listBillFields.size else quickFix.bill.size,
          key = { it.hashCode() }) { index ->
            val billField =
                if (quickFix.status == Status.PENDING) listBillFields[index]
                else quickFix.bill[index]
            var dropDownUnitExpanded by remember { mutableStateOf(false) }
            var billDescription by remember { mutableStateOf(billField.description) }
            var billAmount by remember { mutableDoubleStateOf(billField.amount) }
            var billUnitPrice by remember { mutableDoubleStateOf(billField.unitPrice) }
            var billUnit by remember { mutableStateOf(billField.unit) }
            val total = billUnitPrice.let { billAmount.times(it) }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp * widthRatio.value)) {
                  Box(
                      contentAlignment = Alignment.TopStart,
                      modifier = Modifier.width(150.dp * widthRatio.value)) {
                        if (quickFix.status == Status.PENDING) {
                          IconButton(
                              onClick = { listBillFields = listBillFields - billField },
                              modifier =
                                  Modifier.size(16.dp * widthRatio.value)
                                      .zIndex(1f)
                                      .offset(
                                          y = (-2).dp * widthRatio.value,
                                          x = (-2).dp * widthRatio.value)
                                      .testTag("DeleteBillFieldButton_$index"),
                          ) {
                            Icon(
                                imageVector = Icons.Outlined.Cancel,
                                contentDescription = "Delete Bill Field",
                                tint = colorScheme.primary,
                            )
                          }
                        }
                        QuickFixTextFieldCustom(
                            value =
                                if (quickFix.status == Status.PENDING) billDescription
                                else quickFix.bill[index].description,
                            onValueChange = { it ->
                              if (quickFix.status == Status.PENDING) {
                                billDescription = it
                                updateBillField(index, listBillFields) {
                                  it.copy(description = billDescription)
                                }
                              }
                            },
                            widthField = 150.dp * widthRatio.value,
                            alwaysShowTrailingIcon = false,
                            singleLine = false,
                            placeHolderText = "Add a description",
                            enabled = quickFix.status == Status.PENDING,
                            shape = RoundedCornerShape(10.dp),
                            showTrailingIcon = { false },
                            hasShadow = false,
                            borderColor =
                                if (quickFix.status == Status.PENDING)
                                    colorScheme.onSecondaryContainer
                                else colorScheme.surface,
                            borderThickness = if (quickFix.status == Status.PENDING) 1.dp else 0.dp,
                            heightInEnabled = true,
                            modifier = Modifier.testTag("DescriptionTextField_$index"))
                      }
                  Spacer(modifier = Modifier.width(8.dp * widthRatio.value))
                  Box {
                    QuickFixTextFieldCustom(
                        value =
                            if (quickFix.status == Status.PENDING) billUnit.name
                            else quickFix.bill[index].unit.name,
                        onValueChange = {},
                        widthField = 40.dp * widthRatio.value,
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = {
                          Icon(
                              imageVector = Icons.Default.KeyboardArrowDown,
                              tint = colorScheme.onBackground,
                              contentDescription = "Expand Dropdown",
                              modifier =
                                  Modifier.clickable { dropDownUnitExpanded = true }
                                      .size(16.dp * widthRatio.value))
                        },
                        modifier = Modifier.testTag("UnitDropdown_$index"),
                        showTrailingIcon = { quickFix.status == Status.PENDING },
                        showLeadingIcon = { false },
                        hasShadow = false,
                        enabled = quickFix.status == Status.PENDING,
                        borderColor =
                            if (quickFix.status == Status.PENDING) colorScheme.onSecondaryContainer
                            else colorScheme.surface,
                        borderThickness = if (quickFix.status == Status.PENDING) 1.dp else 0.dp,
                        scrollable = false,
                        alwaysShowTrailingIcon = quickFix.status == Status.PENDING,
                        isTextField = false,
                        onTextFieldClick = { dropDownUnitExpanded = true },
                        moveTrailingIconLeft = 4.dp * widthRatio.value,
                        sizeIconGroup = 16.dp * widthRatio.value,
                    )
                    if (quickFix.status == Status.PENDING) {
                      DropdownMenu(
                          expanded = dropDownUnitExpanded,
                          onDismissRequest = { dropDownUnitExpanded = false },
                          containerColor = colorScheme.background,
                          modifier =
                              Modifier.border(
                                      1.dp,
                                      colorScheme.onSecondaryContainer,
                                      RoundedCornerShape(10.dp))
                                  .background(colorScheme.surface)
                                  .width(70.dp * widthRatio.value)
                                  .testTag("UnitDropdownMenu_$index")) {
                            Units.entries.forEach { unit ->
                              DropdownMenuItem(
                                  text = {
                                    Text(
                                        text = unit.name,
                                        style = poppinsTypography.bodyMedium,
                                        color = colorScheme.onBackground,
                                    )
                                  },
                                  onClick = {
                                    billUnit = unit
                                    updateBillField(index, listBillFields) { it ->
                                      it.copy(
                                          unit = billUnit,
                                          total = billUnitPrice.let { billAmount.times(it) })
                                    }
                                    dropDownUnitExpanded = false
                                  },
                                  colors =
                                      MenuDefaults.itemColors(
                                          textColor = colorScheme.onBackground,
                                      ))
                            }
                          }
                    }
                  }
                  Spacer(modifier = Modifier.width(6.dp * widthRatio.value))
                  QuickFixTextFieldCustom(
                      value =
                          if (quickFix.status == Status.PENDING)
                              billAmount.let { if (it == 0.0) "" else it.toString() }
                          else quickFix.bill[index].amount.toString(), // Use the raw input
                      onValueChange = { input ->
                        if (quickFix.status == Status.PENDING) {
                          val trimmedInput = input.trimStart('0').ifEmpty { "0" }
                          if (trimmedInput.matches(numberPattern)) {
                            billAmount = trimmedInput.toDoubleOrNull() ?: 0.0
                            updateBillField(index, listBillFields) {
                              it.copy(amount = billAmount, total = billAmount.times(it.unitPrice))
                            }
                          }
                        }
                      },
                      modifier = Modifier.testTag("AmountTextField_$index"),
                      widthField = 50.dp * widthRatio.value,
                      showTrailingIcon = { false },
                      hasShadow = false,
                      enabled = quickFix.status == Status.PENDING,
                      borderColor =
                          if (quickFix.status == Status.PENDING) colorScheme.onSecondaryContainer
                          else colorScheme.surface,
                      shape = RoundedCornerShape(10.dp),
                      borderThickness = if (quickFix.status == Status.PENDING) 1.dp else 0.dp,
                      scrollable = false,
                  )

                  Spacer(modifier = Modifier.width(6.dp * widthRatio.value))
                  QuickFixTextFieldCustom(
                      value =
                          if (quickFix.status == Status.PENDING)
                              billUnitPrice.let { if (it == 0.0) "" else it.toString() }
                          else quickFix.bill[index].unitPrice.toString(), // Use the raw input
                      onValueChange = { input ->
                        if (quickFix.status == Status.PENDING) {
                          val trimmedInput = input.trimStart('0').ifEmpty { "0" }
                          if (trimmedInput.matches(numberPattern)) {
                            billUnitPrice = trimmedInput.toDoubleOrNull() ?: 0.0
                            updateBillField(index, listBillFields) {
                              it.copy(
                                  unitPrice = billUnitPrice, total = it.amount.times(billUnitPrice))
                            }
                          }
                        }
                      },
                      modifier = Modifier.testTag("UnitPriceTextField_$index"),
                      widthField = 50.dp * widthRatio.value,
                      showTrailingIcon = { false },
                      hasShadow = false,
                      borderColor =
                          if (quickFix.status == Status.PENDING) colorScheme.onSecondaryContainer
                          else colorScheme.surface,
                      shape = RoundedCornerShape(10.dp),
                      borderThickness = if (quickFix.status == Status.PENDING) 1.dp else 0.dp,
                      scrollable = false,
                      enabled = quickFix.status == Status.PENDING,
                  )
                  Spacer(modifier = Modifier.width(2.dp * widthRatio.value))
                  Text(
                      text =
                          if (quickFix.status == Status.PENDING) {
                            if (total == 0.0 || total.isNaN()) "Total CHF"
                            else "%.2f".format(total).plus(" CHF")
                          } else {
                            "%.2f".format(quickFix.bill[index].total).plus(" CHF")
                          },
                      style = poppinsTypography.labelSmall,
                      color = colorScheme.onBackground,
                      fontWeight = FontWeight.Medium,
                      textAlign = TextAlign.End,
                      modifier = Modifier.weight(1f).testTag("TotalText_$index"))
                }
          }

      if (quickFix.status == Status.PENDING) {
        item {
          Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                  listBillFields = listBillFields + BillField("", Units.U, 0.0, 0.0, 0.0)
                },
                modifier =
                    Modifier.padding(vertical = 8.dp * heightRatio.value)
                        .testTag("AddBillFieldButton"),
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
                        modifier = Modifier.testTag("AddBillFieldIcon"))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add A Bill Field",
                        style = poppinsTypography.labelSmall,
                        color = colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("AddBillFieldText"))
                  }
            }
          }
        }
      }

      item {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp * heightRatio.value)) {
              Text(
                  text = "Total",
                  style = poppinsTypography.labelSmall,
                  color = colorScheme.onBackground,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.weight(1f).testTag("OverallTotalLabel"))
              Text(
                  text =
                      if (quickFix.status == Status.PENDING) {
                        listBillFields
                            .map { it.amount.times(it.unitPrice) }
                            .sum()
                            .let {
                              if (it == 0.0 || it.isNaN()) "Total CHF"
                              else "%.2f".format(it).plus(" CHF")
                            }
                      } else {
                        "%.2f".format(quickFix.bill.sumOf { it.total }).plus(" CHF")
                      },
                  style = poppinsTypography.labelSmall,
                  color = colorScheme.onBackground,
                  fontWeight = FontWeight.Medium,
                  textAlign = TextAlign.End,
                  modifier = Modifier.weight(1f).testTag("OverallTotalValue"))
            }
      }

      item {
        QuickFixButton(
            buttonText = if (quickFix.status == Status.PENDING) "Submit the QuickFix" else "Pay",
            buttonColor = colorScheme.primary,
            textColor = colorScheme.onPrimary,
            onClickAction = {
              if (quickFix.status == Status.PENDING) {
                val updatedQuickFix =
                    quickFix.copy(date = listDates, bill = listBillFields, status = Status.UNPAID)
                quickFixViewModel.updateQuickFix(
                    updatedQuickFix,
                    onSuccess = {
                      onQuickFixChange(updatedQuickFix)
                      // TODO /* Make so that the worker cannot edit the quickfix anymore */
                    },
                    onFailure = {
                      Log.e("QuickFixThirdStep", "Failed to update QuickFix: ${it.message}")
                    })
              } else {
                val quickfix = quickFix.copy(status = Status.UPCOMING)
                quickFixViewModel.updateQuickFix(
                    quickfix,
                    onSuccess = { onQuickFixPay(quickfix) },
                    onFailure = {
                      Log.e("QuickFixThirdStep", "Failed to update QuickFix: ${it.message}")
                    })
              }
            },
            modifier =
                Modifier.fillMaxWidth()
                    .padding(top = 16.dp * heightRatio.value)
                    .testTag("SubmitQuickFixButton"),
            enabled =
                if (quickFix.status == Status.PENDING) {
                  listDates.isNotEmpty() &&
                      listBillFields.isNotEmpty() &&
                      listBillFields.all {
                        it.amount != 0.0 && it.unitPrice != 0.0 && it.description.isNotBlank()
                      }
                } else if (mode == AppMode.WORKER) {
                  false
                } else true)
      }

      item {
        QuickFixButton(
            buttonText = "Go back home",
            buttonColor = colorScheme.surface,
            textStyle = poppinsTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            textColor = colorScheme.onSurface,
            height = 75.dp * heightRatio.value,
            onClickAction = {
              navigationActionsRoot.navigateTo(
                  if (mode == AppMode.USER) USER_TOP_LEVEL_DESTINATIONS[0].route
                  else WORKER_TOP_LEVEL_DESTINATIONS[0].route)
            },
            leadingIcon = Icons.Outlined.Home,
            leadingIconTint = colorScheme.onSurface,
            modifier =
                Modifier.padding(top = 16.dp * heightRatio.value)
                    .fillMaxWidth()
                    .testTag("GoBackHomeButton"))
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
    listDates: List<Timestamp>,
) {
  var selectedDates by remember { mutableStateOf(listDates) }

  AlertDialog(
      onDismissRequest = onDismissRequest,
      title = {
        Text(
            text = "Select Suggested Dates",
            color = colorScheme.onBackground,
            style = poppinsTypography.bodyMedium,
            fontSize = 12.sp,
            modifier = Modifier.testTag("SuggestedDatesDialogTitle"))
      },
      text = {
        LazyColumn {
          items(quickFix.date.size) { index ->
            val date = quickFix.date[index]
            val isSelected = selectedDates.contains(date)
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .testTag("SuggestedDateItem_$index"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              RadioButton(
                  selected = isSelected,
                  onClick = {
                    selectedDates =
                        if (isSelected) {
                          selectedDates - date
                        } else {
                          selectedDates + date
                        }
                  },
                  modifier = Modifier.testTag("RadioButton_$index"))
              Text(
                  text = dateFormatter.format(date.toDate()),
                  style = poppinsTypography.bodyMedium,
                  color = colorScheme.onBackground,
                  modifier = Modifier.weight(1f).testTag("SuggestedDateText_$index"))
              Text(
                  text = timeFormatter.format(date.toDate()),
                  style = poppinsTypography.bodyMedium,
                  color = colorScheme.onBackground,
                  modifier = Modifier.weight(1f).testTag("SuggestedTimeText_$index"))
            }
          }
        }
      },
      confirmButton = {
        TextButton(
            onClick = {
              onDatesSelected(selectedDates)
              onDismissRequest()
            },
            modifier = Modifier.testTag("ConfirmSuggestedDatesButton")) {
              Text(text = "OK", fontSize = 16.sp)
            }
      },
      dismissButton = {
        TextButton(
            onClick = onDismissRequest, modifier = Modifier.testTag("CancelSuggestedDatesButton")) {
              Text("Cancel", fontSize = 16.sp)
            }
      },
      containerColor = colorScheme.surface)
}
