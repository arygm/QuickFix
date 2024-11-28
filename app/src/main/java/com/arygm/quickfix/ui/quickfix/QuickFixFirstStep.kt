package com.arygm.quickfix.ui.quickfix

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.theme.poppinsTypography

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

  var quickNote = remember { mutableStateOf("") }

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
            ListServices(listServices, checkedStatesServices, index)
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
            ListServices(listAddOnServices, checkedStatesAddOnServices, index)
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          item {
            QuickFixTextFieldCustom(
                heightField = 150.dp,
                widthField = 400.dp * widthRatio.value,
                value = quickNote.value,
                onValueChange = { quickNote.value = it },
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

          item {}
        }
      }
}

@Composable
private fun ListServices(
    listServices: List<String>,
    checkedStatesServices: SnapshotStateList<Boolean>,
    index: Int,
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.toggleable(
                  value = checkedStatesServices[index],
                  onValueChange = { checkedStatesServices[index] = it },
                  role = Role.RadioButton // Role as a RadioButton
                  )
              .padding(vertical = 3.dp)) {
        Box(
            modifier =
                Modifier.size(24.dp) // Set the size of the RadioButton explicitly
                    .align(Alignment.CenterVertically) // Align it vertically in the Row
            ) {
              RadioButton(
                  selected = checkedStatesServices[index],
                  onClick = {
                    checkedStatesServices[index] = !checkedStatesServices[index]
                  }, // Handle toggle
                  modifier =
                      Modifier.size(
                          24.dp), // Set the size directly to remove extra padding of RadioButton
                  colors =
                      RadioButtonDefaults.colors(
                          selectedColor = colorScheme.primary,
                          unselectedColor = colorScheme.tertiaryContainer))
            }
        Spacer(modifier = Modifier.width(8.dp)) // Add space between RadioButton and Text
        Text(
            text = listServices[index],
            style = poppinsTypography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface)
      }
  if (index < listServices.size - 1) {
    HorizontalDivider(
        color = colorScheme.background,
        thickness = 1.5.dp,
        modifier = Modifier.padding(start = 32.dp))
  }
}
