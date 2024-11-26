package com.arygm.quickfix.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag // Import for testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.ui.theme.poppinsFontFamily
import com.arygm.quickfix.ui.theme.poppinsTypography

// Data class for QuickFix item
data class BillSneakPeak(
    val name: String,
    val taskDescription: String,
    val date: String,
    val price: Int
)

@Composable
fun BillsWidget(
    billList: List<BillSneakPeak>,
    onShowAllClick: () -> Unit,
    onItemClick: (BillSneakPeak) -> Unit,
    modifier: Modifier = Modifier,
    itemsToShowDefault: Int = 3
) {
  var showAll by remember { mutableStateOf(false) } // Toggle for showing all items
  BoxWithConstraints {
    val cardWidth = maxWidth * 0.4f // 40% of the available width for each card
    val horizontalSpacing = maxWidth * 0.025f // 2.5% of the available width for spacing

    // Card-like styling with shadow for the entire Column
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontalSpacing)
                .shadow(5.dp, RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .testTag("BillsWidget"), // Added testTag
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {
          // Header with divider
          Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Bills",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = poppinsTypography.headlineMedium,
                    fontSize = 19.sp,
                    modifier =
                        Modifier.testTag("UpcomingQuickFixesTitle")
                            .padding(horizontal = 8.dp) // Added testTag
                    )
                TextButton(
                    onClick = { showAll = !showAll },
                    modifier = Modifier.testTag("ShowAllButton") // Added testTag
                    ) {
                      Text(
                          text = if (showAll) "Show Less" else "Show All",
                          color = MaterialTheme.colorScheme.onSurface,
                          style = MaterialTheme.typography.bodyMedium,
                          fontWeight = FontWeight.SemiBold)
                    }
              }
          HorizontalDivider(
              modifier = Modifier.padding(horizontal = 0.dp),
              thickness = 1.dp,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))

          val itemsToShow = if (showAll) billList else billList.take(itemsToShowDefault)

          billList.take(itemsToShow.size).forEachIndexed { index, bill ->
            BillItem(billSneakPeak = bill, onClick = { onItemClick(bill) })

            // Divider between items
            if (index < itemsToShow.size - 1) {
              HorizontalDivider(
                  modifier = Modifier.fillMaxWidth(),
                  thickness = 1.dp,
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            }
          }
        }
  }
}

@Composable
fun BillItem(billSneakPeak: BillSneakPeak, onClick: () -> Unit) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp)
              .clickable { onClick() }
              .testTag("BillItem_${billSneakPeak.name}"), // Added testTag
      verticalAlignment = Alignment.CenterVertically) {
        // Text information
        Column(modifier = Modifier.weight(0.7f).padding(start = 8.dp)) {
          // Row for name and task description on the same line
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = billSneakPeak.name,
                modifier = Modifier.testTag(billSneakPeak.name), // Added testTag
                style = poppinsTypography.bodyMedium,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = billSneakPeak.taskDescription, // Removed leading comma for clarity
                modifier = Modifier.testTag(billSneakPeak.taskDescription), // Added testTag
                style = poppinsTypography.bodyMedium,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
          }
          // Date text on a separate line
          Text(
              text = billSneakPeak.date,
              modifier = Modifier.testTag(billSneakPeak.date), // Added testTag
              style = poppinsTypography.bodyMedium,
              fontSize = 15.sp,
              fontWeight = FontWeight.Normal,
              color = MaterialTheme.colorScheme.onSurface)
        }

        Column(
            modifier = Modifier.weight(0.3f).padding(end = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End) {
              // Price information
              Text(
                  text = "$${billSneakPeak.price}",
                  modifier = Modifier.testTag("BillPrice_${billSneakPeak.price}"),
                  color = MaterialTheme.colorScheme.primary,
                  fontFamily = poppinsFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  textAlign = TextAlign.End,
              )
            }
      }
}
