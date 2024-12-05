package com.arygm.quickfix.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixesWidget
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun DashboardScreen(
    isUser: Boolean = true,
    navigationActions: NavigationActions,
    quickFixViewModel: QuickFixViewModel,
    chatViewModel: ChatViewModel,
    accountViewModel: AccountViewModel,
    profileViewModel: ProfileViewModel,
) {

  data class QuickFixFilterButtons(
      val status: Status,
      val isSelected: Boolean,
      val onClick: () -> Unit
  )

  var quickFixFilterButtons by remember {
    mutableStateOf(
        listOf(
            QuickFixFilterButtons(Status.ALL, false, {}),
            QuickFixFilterButtons(Status.UPCOMING, true, {}),
            QuickFixFilterButtons(Status.PAID, false, {}),
            QuickFixFilterButtons(Status.UNPAID, false, {}),
            QuickFixFilterButtons(Status.PENDING, false, {}),
            QuickFixFilterButtons(Status.COMPLETED, false, {}),
            QuickFixFilterButtons(Status.CANCELED, false, {}),
        ))
  }

  val quickfixes = quickFixViewModel.quickFixes.collectAsState().value

  val messageList = chatViewModel.chats.collectAsState().value
  LaunchedEffect(Unit) {
    quickFixViewModel.getQuickFixes()
    chatViewModel.getChats()
  }

  /*val billList =
  quickfixes.map {
    BillSneakPeak(it.name, it.taskDescription, it.date, Random.nextDouble(10.00, 10000.00))
  }*/

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
                      modifier = Modifier.testTag("LazyRowTag")) {
                        items(quickFixFilterButtons.size) { index ->
                          QuickFixButton(
                              buttonText =
                                  quickFixFilterButtons[index]
                                      .status
                                      .toString()
                                      .lowercase()
                                      .replaceFirstChar { it.uppercase() },
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
                  val buttonTitle = quickFixFilterButtons.firstOrNull { it.isSelected }?.status
                  when (buttonTitle) {
                    Status.UPCOMING ->
                        QuickFixesWidget(
                            status = Status.UPCOMING,
                            quickFixList = quickfixes.filter { it.status == Status.UPCOMING },
                            onShowAllClick = { /* Handle Show All Click */},
                            onItemClick = { /* Handle QuickFix Item Click */},
                            modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                            itemsToShowDefault = 3,
                        )
                    Status.CANCELED -> {
                      QuickFixesWidget(
                          status = Status.CANCELED,
                          quickFixList = quickfixes.filter { it.status == Status.CANCELED },
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                      )
                    }
                    Status.UNPAID -> {
                      QuickFixesWidget(
                          status = Status.UNPAID,
                          quickFixList = quickfixes.filter { it.status == Status.UNPAID },
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                      )
                    }
                    Status.COMPLETED -> {
                      QuickFixesWidget(
                          status = Status.COMPLETED,
                          quickFixList = quickfixes.filter { it.status == Status.COMPLETED },
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                      )
                    }
                    Status.ALL -> {
                      QuickFixesWidget(
                          status = Status.ALL,
                          quickFixList = quickfixes,
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                      )
                    }
                    Status.PENDING -> {
                      QuickFixesWidget(
                          status = Status.PENDING,
                          quickFixList = quickfixes.filter { it.status == Status.PENDING },
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                      )
                    }
                    else -> {
                      QuickFixesWidget(
                          status = Status.PAID,
                          quickFixList = quickfixes.filter { it.status == Status.PAID },
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
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
                    isUser = isUser,
                    accountViewModel = accountViewModel,
                    profileViewModel = profileViewModel,
                )
              }

              item {
                /*BillsWidget(
                billList = billList,
                onItemClick = { */
                /*Handle Bill Item Click*/
                /*},
                onShowAllClick = { */
                /*Handle Show All Click*/
                /*},
                    itemsToShowDefault = 2,
                )*/
              }
            }
      })
}
