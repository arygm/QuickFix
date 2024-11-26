package com.arygm.quickfix.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.ImagesearchRoller
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.R
import com.arygm.quickfix.ui.elements.QuickFix
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixesWidget
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import kotlin.random.Random

@Composable
fun DashboardScreen(navigationActions: NavigationActions, isUser: Boolean = true) {

  data class QuickFixFilterButtons(
      val title: String,
      val isSelected: Boolean,
      val onClick: () -> Unit
  )

  var quickFixFilterButtons by remember {
    mutableStateOf(
        listOf(
            QuickFixFilterButtons("All", false, {}),
            QuickFixFilterButtons("Upcoming", true, {}),
            QuickFixFilterButtons("Canceled", false, {}),
            QuickFixFilterButtons("Unpaid", false, {}),
            QuickFixFilterButtons("Finished", false, {}),
        ))
  }

  // Sample data for fetched filtered quick fixes
  val quickFixes =
      listOf(
          QuickFix("Adam", "Bathroom renovation", "Sat, 12 Oct 2024"),
          QuickFix("Mehdi", "Laying kitchen tiles", "Sun, 13 Oct 2024"),
          QuickFix("Ramy", "Bathroom painting", "Sat, 12 Oct 2024"),
          QuickFix("Ramy", "Bathroom painting", "Sat, 12 Oct 2024"),
          QuickFix("Mehdi", "Laying kitchen tiles", "Sun, 13 Oct 2024"),
          QuickFix("Ramy", "Bathroom painting", "Sat, 12 Oct 2024"))

  val messageList =
      listOf(
          MessageSneakPeak(
              "Ramy Hatimy",
              "Hello, I’m available everyday from 7pm to 8pm bla bla bla bla bla bla bla",
              "8:30",
              R.drawable.placeholder_worker,
              false,
              2,
              Icons.Outlined.ImagesearchRoller),
          MessageSneakPeak(
              "Adam Ait Bousselham",
              "Yes of course I’ll be there by 9pm. Can you tell me ouais c'est comment la vie d'artiste hehehe",
              "11:00",
              R.drawable.placeholder_worker,
              true,
              0,
              Icons.Outlined.ElectricalServices),
      )

  val billList =
      quickFixes.map {
        BillSneakPeak(it.name, it.taskDescription, it.date, Random.nextInt(10, 10000))
      }

  Scaffold(
      containerColor = colorScheme.background,
      content = { padding ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .testTag("DashboardContent")
                    .padding(padding)
                    .padding(6.dp)
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.Start) {
              item {
                Card(
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(6.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors().copy(containerColor = colorScheme.surface),
                ) {
                  Text(
                      text = "QuickFixes",
                      style = poppinsTypography.headlineLarge,
                      textAlign = TextAlign.Start,
                      color = colorScheme.onBackground,
                      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

                  LazyRow(
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                  ) {
                    items(quickFixFilterButtons.size) { index ->
                      QuickFixButton(
                          buttonText = quickFixFilterButtons[index].title,
                          onClickAction = {
                            quickFixFilterButtons =
                                quickFixFilterButtons.mapIndexed { i, button ->
                                  button.copy(isSelected = i == index)
                                }
                            quickFixFilterButtons[index].onClick()
                          },
                          textColor =
                              if (quickFixFilterButtons[index].isSelected) colorScheme.onPrimary
                              else colorScheme.primary,
                          textStyle = poppinsTypography.bodyMedium,
                          buttonColor =
                              if (quickFixFilterButtons[index].isSelected) colorScheme.primary
                              else colorScheme.onPrimary,
                          height = 40.dp,
                          modifier = Modifier.padding(7.dp),
                          contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                          border =
                              if (quickFixFilterButtons[index].isSelected) null
                              else BorderStroke(1.dp, colorScheme.primary),
                      )
                    }
                  }
                  when (quickFixFilterButtons.firstOrNull { it.isSelected }?.title) {
                    "Upcoming" ->
                        QuickFixesWidget(
                            status = "Upcoming",
                            quickFixList = quickFixes,
                            onShowAllClick = { /* Handle Show All Click */},
                            onItemClick = { /* Handle QuickFix Item Click */},
                            modifier = Modifier.testTag("UpcomingQuickFixes"),
                            itemsToShowDefault = 3,
                        )
                    "Canceled" -> {
                      QuickFixesWidget(
                          status = "Canceled",
                          quickFixList = quickFixes,
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("UpcomingQuickFixes"),
                          itemsToShowDefault = 3,
                      )
                    }
                    "Unpaid" -> {
                      QuickFixesWidget(
                          status = "Unpaid",
                          quickFixList = quickFixes,
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("UpcomingQuickFixes"),
                          itemsToShowDefault = 3,
                      )
                    }
                    "Finished" -> {
                      QuickFixesWidget(
                          status = "Finished",
                          quickFixList = quickFixes,
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("UpcomingQuickFixes"),
                          itemsToShowDefault = 3,
                      )
                    }
                    else -> {
                      QuickFixesWidget(
                          quickFixList = quickFixes,
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("UpcomingQuickFixes"),
                          itemsToShowDefault = 3,
                      )
                    }
                  }
                }
              }

              item {
                MessagesWidget(
                    messageList = messageList,
                    onItemClick = { /*Handle Message Item Click*/},
                    onShowAllClick = { /*Handle Show All Click*/},
                    itemsToShowDefault = 3,
                )
              }

              item {
                BillsWidget(
                    billList = billList,
                    onItemClick = { /*Handle Bill Item Click*/},
                    onShowAllClick = { /*Handle Show All Click*/},
                    itemsToShowDefault = 2,
                )
              }
            }
      })
}
