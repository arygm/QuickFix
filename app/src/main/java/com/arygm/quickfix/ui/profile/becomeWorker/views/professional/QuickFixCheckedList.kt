package com.arygm.quickfix.ui.profile.becomeWorker.views.professional

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.elements.QuickFixCheckedListElement
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun QuickFixCheckedList(
    modifier: Modifier = Modifier,
    listServices: List<String>,
    checkedStatesServices: SnapshotStateList<Boolean>,
    heightRatio: Dp,
    indices: IntProgression,
    onClickActionOk: () -> Unit = {},
    minToSelect: Int = 5,
    maxToSelect: Int = 10,
    formValidated: MutableState<Boolean>,
    boldText: String,
    label: String,
    secondPartLabel: String = "",
    widthRatio: Dp,
    isTextFieldList: Boolean = false,
    textFieldList: MutableList<MutableState<String>> = mutableListOf(),
    canAddTextField: MutableState<Boolean> = mutableStateOf(true),
) {
  Column(modifier = modifier) {
    Text(
        text =
            buildAnnotatedString {
              append(label)
              withStyle(
                  style =
                      SpanStyle(
                          color = colorScheme.onBackground,
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Medium)) {
                    append(boldText)
                  }
              append(secondPartLabel)
            },
        style =
            poppinsTypography.headlineMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
        color = colorScheme.onSurface)
    Column(modifier = Modifier.fillMaxWidth()) {
      indices.forEach { index ->
        Column(
            modifier = Modifier.semantics { testTag = C.Tag.quickFixCheckedListElement + index }) {
              Row(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.weight(1f).align(Alignment.Top)) {
                  QuickFixCheckedListElement(
                      listServices = listServices,
                      checkedStatesServices = checkedStatesServices,
                      index = index,
                      radioButtonAlignment = Alignment.Top,
                      displayHorizontalDivider = false,
                      canSelect = !formValidated.value,
                      maxAchieved = checkedStatesServices.filter { it }.size >= maxToSelect,
                      modifier =
                          Modifier.semantics {
                            testTag = C.Tag.quickFixCheckedListElementLeft + (index)
                          })
                }
                // Second item in the row
                if (index + 1 < listServices.size) {
                  Row(modifier = Modifier.weight(1f).align(Alignment.Top)) {
                    QuickFixCheckedListElement(
                        listServices = listServices,
                        checkedStatesServices = checkedStatesServices,
                        index = index + 1,
                        radioButtonAlignment = Alignment.Top,
                        displayHorizontalDivider = false,
                        canSelect = !formValidated.value,
                        maxAchieved = checkedStatesServices.filter { it }.size >= maxToSelect,
                        modifier =
                            Modifier.semantics {
                              testTag = C.Tag.quickFixCheckedListElementRight + (index)
                            })
                  }
                } else {
                  // Spacer to fill the remaining space if the list size is odd
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }
        if (index < listServices.size - 2) {
          Spacer(modifier = Modifier.height(8.dp * heightRatio.value))
          HorizontalDivider(
              color = colorScheme.background,
              thickness = 1.5.dp,
              modifier =
                  Modifier.padding(start = 32.dp * widthRatio.value).semantics {
                    testTag = C.Tag.quickFixCheckedListElementDivider + (index)
                  })
          Spacer(modifier = Modifier.height(8.dp * heightRatio.value))
        }
      }
    }
    Spacer(modifier = Modifier.height(12.dp * heightRatio.value))
    if (isTextFieldList) {
      textFieldList.forEachIndexed { index, service ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {
              Row(modifier = Modifier.weight(0.5f)) {
                QuickFixTextFieldCustom(
                    modifier =
                        Modifier.semantics {
                          testTag = C.Tag.quickFixCheckedListTextFieldElement + index
                        },
                    widthField = 380.dp * widthRatio.value,
                    value = service.value,
                    onValueChange = { service.value = it },
                    shape = RoundedCornerShape(8.dp),
                    hasShadow = false,
                    borderColor = colorScheme.tertiaryContainer,
                    placeHolderText = "Select Occupation",
                    alwaysShowTrailingIcon = true,
                    moveTrailingIconLeft = 2.dp * widthRatio.value,
                    singleLine = false,
                    heightInEnabled = true,
                    minHeight = 27.dp * heightRatio.value, // Set default height
                    maxHeight =
                        54.dp *
                            heightRatio.value, // Allow expansion up to double the default height
                    maxLines = 2,
                    maxChar = 52,
                )
              }
              if (canAddTextField.value) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "textfield delete $index",
                    tint = colorScheme.onSurface,
                    modifier =
                        Modifier.size(width = 30.dp * widthRatio.value, height = 30.dp * heightRatio.value)
                            .clickable {
                              service.value = ""
                              textFieldList.remove(service)
                            }
                            .weight(0.1f)
                            .semantics {
                              testTag = C.Tag.quickFixCheckedListTextFieldElementDelete + index
                            })
              } else {
                Spacer(modifier = Modifier.weight(0.1f))
              }
              Spacer(modifier = Modifier.weight(0.4f))
            }
        Spacer(modifier = Modifier.height(8.dp * heightRatio.value))
      }
      if (canAddTextField.value) {
        Text(
            text = "+ Add New",
            color = colorScheme.primary,
            style =
                poppinsTypography.headlineSmall.copy(
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            modifier =
                Modifier.clickable(onClick = { textFieldList.add(mutableStateOf("")) }).semantics {
                  testTag = C.Tag.quickFixCheckedListTextFieldElementAdd
                })
        Spacer(modifier = Modifier.height(8.dp * heightRatio.value))
      }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically) {
          Spacer(modifier = Modifier.width(3.dp * widthRatio.value))
          if (!formValidated.value) {
            val isEnabled = {
              if (isTextFieldList) {
                (checkedStatesServices.filter { it }.size + textFieldList.size) >= minToSelect
              } else {
                checkedStatesServices.filter { it }.size >= minToSelect
              }
            }
            Text(
                text = "OK",
                color = if (isEnabled()) colorScheme.primary else colorScheme.tertiaryContainer,
                style =
                    poppinsTypography.headlineSmall.copy(
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                modifier =
                    Modifier.clickable(enabled = isEnabled(), onClick = onClickActionOk).semantics {
                      testTag = C.Tag.quickFixCheckedListOk
                    })
            Spacer(modifier = Modifier.width(20.dp * widthRatio.value))
            Text(
                text = "Reset",
                color = colorScheme.tertiaryContainer, // Use the disabled color
                style =
                    poppinsTypography.headlineSmall.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold), // Optional: adjust typography
                modifier =
                    Modifier.clickable {
                          // Reset all checked states to false
                          checkedStatesServices.forEachIndexed { index, _ ->
                            checkedStatesServices[index] = false
                          }
                          textFieldList.clear()
                        }
                        .semantics {
                          testTag = C.Tag.quickFixCheckedListReset
                        } // Adjust padding for appearance
                )
          } else {
            Text(
                text = "Edit",
                color = colorScheme.primary,
                style =
                    poppinsTypography.headlineSmall.copy(
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                modifier =
                    Modifier.clickable(
                            onClick = {
                              formValidated.value = false
                              canAddTextField.value = true
                            })
                        .semantics { testTag = C.Tag.quickFixCheckedListEdit })
          }
        }
  }
}
