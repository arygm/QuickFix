package com.arygm.quickfix.ui.elements

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun QuickFixCheckedListElement(
    listServices: List<String>,
    checkedStatesServices: SnapshotStateList<Boolean>,
    index: Int,
    modifier: Modifier = Modifier,
    radioButtonAlignment: Alignment.Vertical = Alignment.CenterVertically,
    displayHorizontalDivider: Boolean = true,
    canSelect: Boolean = true,
    maxAchieved: Boolean = false,
    heightRatio: Dp = 1.dp,
    widthRatio: Dp = 1.dp
) {
  Column {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .toggleable(
                    enabled = canSelect && !(maxAchieved && !checkedStatesServices[index]),
                    value = checkedStatesServices[index],
                    onValueChange = { checkedStatesServices[index] = it },
                    role = Role.RadioButton // Role as a RadioButton
                    )
                .padding(vertical = 3.dp * heightRatio.value)) {
          Box(
              modifier =
                  Modifier.size(
                          width = 24.dp * widthRatio.value,
                          height =
                              24.dp *
                                  heightRatio.value) // Set the size of the RadioButton explicitly
                      .align(radioButtonAlignment) // Align it vertically in the Row
              ) {
                RadioButton(
                    enabled = canSelect && !(maxAchieved && !checkedStatesServices[index]),
                    selected = checkedStatesServices[index],
                    onClick = {
                      Log.d(
                          "QuickFixCheckedListElement",
                          "onClick: $index ${checkedStatesServices[index]}")
                      checkedStatesServices[index] = !checkedStatesServices[index]
                    }, // Handle toggle
                    modifier =
                        Modifier.size(
                            width = 24.dp * widthRatio.value,
                            height =
                                24.dp *
                                    heightRatio
                                        .value), // Set the size directly to remove extra padding
                    // of RadioButton
                    colors =
                        RadioButtonDefaults.colors(
                            selectedColor = colorScheme.primary,
                            unselectedColor = colorScheme.tertiaryContainer))
              }
          Spacer(
              modifier =
                  Modifier.width(8.dp * widthRatio.value)) // Add space between RadioButton and Text
          Text(
              text = listServices[index],
              style = poppinsTypography.labelSmall,
              fontWeight = FontWeight.Medium,
              color = colorScheme.onSurface)
        }
    if (index < listServices.size - 1 && displayHorizontalDivider) {
      HorizontalDivider(
          color = colorScheme.background,
          thickness = 1.5.dp,
          modifier = Modifier.padding(start = 32.dp * widthRatio.value))
    }
  }
}
