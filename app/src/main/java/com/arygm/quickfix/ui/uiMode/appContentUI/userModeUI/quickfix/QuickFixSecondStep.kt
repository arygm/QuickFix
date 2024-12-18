package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.quickfix

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.USER_TOP_LEVEL_DESTINATIONS
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WORKER_TOP_LEVEL_DESTINATIONS
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerScreen
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

@Composable
fun QuickFixSecondStep(
    quickFixViewModel: QuickFixViewModel,
    navigationActionsRoot: NavigationActions,
    accountViewModel: AccountViewModel,
    chatViewModel: ChatViewModel,
    navigationActions: NavigationActions,
    onQuickFixMakeBill: () -> Unit,
    quickFix: QuickFix,
    mode: AppMode
) {
  val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM")
  val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
  var userAccount by remember { mutableStateOf<Account?>(null) }
  var workerAccount by remember { mutableStateOf<Account?>(null) }
  LaunchedEffect(Unit) {
    accountViewModel.fetchUserAccount(quickFix.userId) { userAccount = it }
    accountViewModel.fetchUserAccount(quickFix.workerId) { workerAccount = it }
  }
  var chat by remember { mutableStateOf<Chat?>(null) }

  BoxWithConstraints(
      modifier = Modifier.background(colorScheme.surface),
  ) {
    val widthRatio = maxWidth / 411
    val heightRatio = maxHeight / 860

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp).testTag("MainColumn"),
        horizontalAlignment = Alignment.Start,
    ) {
      item {
        Text(
            text =
                if (mode == AppMode.USER) "The worker has been contacted..."
                else
                    "${userAccount?.firstName.plus(" ").plus(userAccount?.lastName)} has contacted you...",
            style =
                poppinsTypography.bodyMedium.copy(fontSize = 30.sp, fontWeight = FontWeight.Bold),
            color = colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 4.dp).testTag("HeaderText"))

        Image(
            painter = painterResource(id = R.drawable.on_boarding_worker),
            contentDescription = "workers",
            contentScale = ContentScale.Fit,
            modifier =
                Modifier.width(320.dp * widthRatio.value)
                    .padding(top = 16.dp * heightRatio.value)
                    .testTag("HeaderImage"),
        )
      }

      item {
        Text(
            text = "Summary",
            style =
                poppinsTypography.bodyMedium.copy(
                    fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
            color = colorScheme.onBackground,
        )

        Text(
            text = "Location",
            style =
                poppinsTypography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium),
            color = colorScheme.onBackground,
            modifier =
                Modifier.padding(top = 6.dp * heightRatio.value, start = 4.dp * widthRatio.value))

        Text(
            text = quickFix.location.name.split(",").take(2).joinToString(","),
            style =
                poppinsTypography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium),
            color = colorScheme.onSurface,
            modifier = Modifier.padding(start = 7.dp * widthRatio.value))

        Text(
            text = "Selected Services",
            style =
                poppinsTypography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium),
            color = colorScheme.onBackground,
            modifier = Modifier.padding(start = 4.dp * widthRatio.value))

        Column(
            modifier =
                Modifier.padding(
                    top = 4.dp * heightRatio.value,
                    start = 7.dp * widthRatio.value,
                    bottom = 4.dp * heightRatio.value)) {
              quickFix.includedServices.forEachIndexed { it, _ ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                  Icon(
                      imageVector = Icons.Outlined.Check,
                      contentDescription = null,
                      tint = colorScheme.onSurface,
                      modifier =
                          Modifier.padding(end = 4.dp * widthRatio.value)
                              .size(16.dp * widthRatio.value))
                  Text(
                      text = quickFix.includedServices[it].name,
                      style =
                          poppinsTypography.bodyMedium.copy(
                              fontSize = 12.sp, fontWeight = FontWeight.Medium),
                      color = colorScheme.onSurface,
                  )
                }
              }

              quickFix.addOnServices.forEachIndexed { it, _ ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                  Icon(
                      imageVector = Icons.Outlined.Check,
                      contentDescription = null,
                      tint = colorScheme.primary,
                      modifier =
                          Modifier.padding(end = 4.dp * widthRatio.value)
                              .size(16.dp * widthRatio.value))
                  Text(
                      text = quickFix.addOnServices[it].name,
                      style =
                          poppinsTypography.bodyMedium.copy(
                              fontSize = 12.sp, fontWeight = FontWeight.Medium),
                      color = colorScheme.primary,
                  )
                }
              }
            }
      }

      item {
        Text(
            text = "Suggested date(s)",
            style =
                poppinsTypography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium),
            color = colorScheme.onBackground,
            modifier =
                Modifier.padding(top = 6.dp * heightRatio.value, start = 4.dp * widthRatio.value))
        Row(
            modifier =
                Modifier.padding(
                        start = 7.dp * widthRatio.value,
                        bottom = 4.dp * heightRatio.value,
                        top = 4.dp * heightRatio.value)
                    .testTag("DatesRow")
                    .fillMaxWidth(0.7f),
            horizontalArrangement = Arrangement.SpaceBetween) {
              Text(
                  text = "Day",
                  style =
                      poppinsTypography.bodyMedium.copy(
                          fontSize = 12.sp, fontWeight = FontWeight.Medium),
                  color = colorScheme.onSecondaryContainer,
                  modifier = Modifier.weight(0.6f).testTag("DayHeader"))

              Text(
                  text = "Starting Time",
                  style =
                      poppinsTypography.bodyMedium.copy(
                          fontSize = 12.sp, fontWeight = FontWeight.Medium),
                  color = colorScheme.onSecondaryContainer,
                  modifier = Modifier.weight(0.4f).testTag("TimeHeader"))
            }

        Column(
            modifier =
                Modifier.padding(start = 7.dp * widthRatio.value, bottom = 4.dp * heightRatio.value)
                    .testTag("DatesLazyColumn")) {
              quickFix.date.forEachIndexed() { index, _ ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.testTag("DateRow_$index")) {
                      Text(
                          text =
                              dateFormatter.format(
                                  LocalDateTime.ofInstant(
                                      quickFix.date[index].toDate().toInstant(),
                                      ZoneId.systemDefault())),
                          style =
                              poppinsTypography.bodyMedium.copy(
                                  fontSize = 12.sp, fontWeight = FontWeight.Medium),
                          color = colorScheme.onBackground,
                          modifier = Modifier.weight(0.6f).testTag("DateText_$index"))
                      Text(
                          text =
                              timeFormatter.format(
                                  LocalDateTime.ofInstant(
                                      quickFix.time.toDate().toInstant(), ZoneId.systemDefault())),
                          style =
                              poppinsTypography.bodyMedium.copy(
                                  fontSize = 12.sp, fontWeight = FontWeight.Medium),
                          color = colorScheme.onBackground,
                          modifier = Modifier.weight(0.4f).testTag("TimeText_$index"))
                    }
              }
            }
      }

      item {
        Column(
            modifier =
                Modifier.clip(RoundedCornerShape(5.dp))
                    .background(colorScheme.background)
                    .testTag("ImagesLazyColumn")) {
              quickFix.imageUrl.forEachIndexed { index, _ ->
                Box(
                    modifier =
                        Modifier.clip(RoundedCornerShape(5.dp))
                            .fillMaxWidth()
                            .height(this@BoxWithConstraints.maxHeight * 0.20f)
                            .testTag("ImageBox_$index")) {
                      Image(
                          painter =
                              painterResource(
                                  id =
                                      R.drawable.electrician), // to change when we can fetch iamges
                          contentDescription = quickFix.imageUrl[index],
                          contentScale = ContentScale.FillBounds,
                          modifier =
                              Modifier.fillMaxSize()
                                  .padding(8.dp * widthRatio.value, 16.dp * heightRatio.value)
                                  .clip(RoundedCornerShape(5.dp))
                                  .testTag("Image_$index") // Apply clipping here
                          )
                    }
              }
            }
      }

      item {
        Row(
            modifier = Modifier.padding(top = 16.dp * heightRatio.value),
        ) {
          QuickFixButton(
              buttonText = "Consult the discussion",
              textStyle =
                  poppinsTypography.bodyMedium.copy(
                      fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
              contentPadding = PaddingValues(0.dp),
              height = 75.dp * heightRatio.value,
              modifier = Modifier.testTag("ConsultDiscussionButton").fillMaxWidth(),
              onClickAction = {
                chat =
                    Chat(
                        chatId = quickFix.userId + quickFix.workerId,
                        quickFixUid = quickFix.uid,
                        workeruid = quickFix.workerId,
                        useruid = quickFix.userId,
                    )
                chatViewModel.viewModelScope.launch {
                  chatViewModel.getChatByChatUid(
                      chat!!.chatId,
                      onSuccess = {
                        chatViewModel.selectChat(it!!)
                        navigationActions.navigateTo(UserScreen.MESSAGES)
                      },
                      onFailure = {
                        chatViewModel.viewModelScope.launch {
                          chatViewModel.addChat(
                              chat!!,
                              onSuccess = {
                                chatViewModel.selectChat(chat!!)
                                accountViewModel.updateAccount(
                                    userAccount!!.copy(
                                        activeChats = userAccount!!.activeChats + chat!!.chatId),
                                    onSuccess = {
                                      accountViewModel.updateAccount(
                                          workerAccount!!.copy(
                                              activeChats =
                                                  workerAccount!!.activeChats + chat!!.chatId),
                                          onSuccess = {
                                            navigationActions.navigateTo(UserScreen.MESSAGES)
                                            Log.d("QuickFixSecondStep", "Chat added to user")
                                          },
                                          onFailure = {
                                            Log.d("QuickFixSecondStep", "Chat not added to worker")
                                          })
                                    },
                                    onFailure = {
                                      Log.d("QuickFixSecondStep", "Chat not added to user")
                                    })
                              },
                              onFailure = { Log.d("QuickFixSecondStep", "Chat not added") })
                        }
                      })
                }
              },
              buttonColor = colorScheme.surface,
              textColor = colorScheme.primary,
              leadingIcon = Icons.AutoMirrored.Outlined.Send,
              leadingIconTint = colorScheme.primary,
              border = BorderStroke(1.dp, colorScheme.primary),
          )
        }
      }

      item {
        if (mode == AppMode.WORKER)
            Row(
                modifier = Modifier.padding(top = 16.dp * heightRatio.value),
            ) {
              QuickFixButton(
                  buttonText = "Make the bill",
                  textStyle =
                      poppinsTypography.bodyMedium.copy(
                          fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
                  contentPadding = PaddingValues(0.dp),
                  height = 75.dp * heightRatio.value,
                  modifier = Modifier.testTag("MakethebillButton").fillMaxWidth(),
                  onClickAction = {
                    onQuickFixMakeBill()
                    navigationActions.navigateTo(WorkerScreen.QUICKFIX_BILLING)
                  },
                  buttonColor = colorScheme.primary,
                  textColor = colorScheme.onPrimary,
                  leadingIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
                  leadingIconTint = colorScheme.onPrimary,
              )
            }
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
