package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.R
import com.arygm.quickfix.ui.theme.QuickFixTheme

// Data class for QuickFix item
data class QuickFix(
    val name: String,
    val taskDescription: String,
    val date: String
)

@Composable
fun UpcomingQuickFixes(
    quickFixList: List<QuickFix>,
    onShowAllClick: () -> Unit,
    onItemClick: (QuickFix) -> Unit,
    modifier:Modifier=Modifier
) {
    var showAll by remember { mutableStateOf(false) } // Toggle for showing all items
    BoxWithConstraints {
        val cardWidth = maxWidth * 0.4f // 40% of the available width for each card
        val horizontalSpacing = maxWidth * 0.025f // 2.5% of the available width for spacing


        // Card-like styling with shadow for the entire Column
        Column(
            modifier = modifier
                .fillMaxWidth() // Restrict width to 90% of the screen
                .padding(horizontalSpacing)
                .shadow(5.dp, RoundedCornerShape(12.dp)) // Apply shadow to the entire Column
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(12.dp)
                ), // Background with rounded corners
            verticalArrangement = Arrangement.Top, // Align items to the top
            horizontalAlignment = Alignment.Start // Align to the start to avoid centering issues
        ) {
            // Header with divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming QuickFixes",
                    color = MaterialTheme.colorScheme.onBackground, // Theme color for text
                    style = MaterialTheme.typography.titleMedium, // Adjust typography for title
                )
                TextButton(onClick = { showAll = !showAll }) {
                    Text(
                        text = if (showAll) "Show Less" else "Show All",
                        color = MaterialTheme.colorScheme.onSurface, // Primary color for "Show All"
                        style = MaterialTheme.typography.bodyMedium, // Adjust typography
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), // Theme color with opacity
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 0.dp)
            )

            // List of QuickFix items
            LazyColumn(
                modifier = Modifier.wrapContentHeight(), // Ensures it only grows downwards
                verticalArrangement = Arrangement.Top // Keeps items anchored to the top
            ) {
                // Determine how many items to show based on showAll state
                val itemsToShow = if (showAll) quickFixList else quickFixList.take(3)

                items(itemsToShow.size) { index ->
                    val quickFix = itemsToShow[index]
                    QuickFixItem(quickFix = quickFix, onClick = { onItemClick(quickFix) })

                    // Divider between items
                    if (index < itemsToShow.size - 1) {
                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUupcomingQuickFixes() {
    QuickFixTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background), // Set a consistent background
            color = MaterialTheme.colorScheme.background
        ) {
            val sampleData = listOf(
                QuickFix("Ramy", "Bathroom painting", "Sat, 12 Oct 2024"),
                QuickFix("Mehdi", "Laying kitchen tiles", "Sun, 13 Oct 2024"),
                QuickFix("Moha", "Toilet plumbing", "Mon, 14 Oct 2024"),
                QuickFix("Sara", "Fixing lighting", "Tue, 15 Oct 2024"),
                QuickFix("John", "Repairing fence", "Wed, 16 Oct 2024")
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top, // Keeps the content anchored at the top
                horizontalAlignment = Alignment.CenterHorizontally // Centers content horizontally only
            ) {
                UpcomingQuickFixes(
                    quickFixList = sampleData,
                    onShowAllClick = { /* Handle Show All action */ },
                    onItemClick = { /* Handle item click action */ }
                )
            }
        }
    }
}


@Composable
fun QuickFixItem(
    quickFix: QuickFix,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable { onClick() }, // Added clickable modifier

        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile image placeholder
        Image(
            painter = painterResource(id = R.drawable.profile), // Replace with an actual drawable
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)) // Use theme color with reduced opacity
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Text information
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Row for name and task description on the same line
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = quickFix.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground, // Use theme color for text
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(4.dp)) // Adjusted space between name and description
                Text(
                    text = ", ${quickFix.taskDescription}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), // Reduced opacity for description
                    maxLines = 1,
                   // modifier=Modifier.padding(1.5.dp),
                    overflow = TextOverflow.Ellipsis
                )
            }
            // Date text on a separate line
            Text(
                text = quickFix.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface // Theme color with reduced opacity
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Arrow icon with a circular background
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary), // Circular background with slight opacity
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go to details",
                tint = MaterialTheme.colorScheme.onBackground // Primary color for the arrow
            )
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PreviewUpcomingQuickFixes() {
    QuickFixTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val sampleData = listOf(
                QuickFix("Ramy", "Bathroom painting", "Sat, 12 Oct 2024"),
                QuickFix("Mehdi", "Laying kitchen tiles", "Sun, 13 Oct 2024"),
                QuickFix("Moha", "Toilet plumbing", "Mon, 14 Oct 2024"),
                QuickFix("Sara", "Fixing lighting", "Tue, 15 Oct 2024"),
                QuickFix("John", "Repairing fence", "Wed, 16 Oct 2024")
            )
            UpcomingQuickFixes(
                quickFixList = sampleData,
                onShowAllClick = { /* Handle Show All action */ },
                onItemClick = { /* Handle item click action */ }
            )
        }
    }
}
