package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.quickfix

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.category.getCategoryIcon
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.dataFields.Review
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.elements.QuickFixWorkerOverview
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.USER_TOP_LEVEL_DESTINATIONS
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WORKER_TOP_LEVEL_DESTINATIONS
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun QuickFixLastStep(
    quickFix: QuickFix,
    workerProfile: WorkerProfile,
    categoryViewModel: CategoryViewModel,
    quickFixViewModel: QuickFixViewModel,
    workerViewModel: ProfileViewModel,
    navigationActionsRoot: NavigationActions,
    onQuickFixChange: (QuickFix) -> Unit,
    mode: AppMode
) {

  val focusManager = LocalFocusManager.current

  var rating by remember { mutableDoubleStateOf(0.0) }
  var feedback by remember { mutableStateOf("") }

  var category by remember { mutableStateOf(Category()) }
  LaunchedEffect(Unit) {
    categoryViewModel.getCategoryBySubcategoryId(
        workerProfile.fieldOfWork,
        onSuccess = {
          if (it != null) {
            category = it
          }
        })
  }

  val dateFormatter = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
  val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
  BoxWithConstraints(
      modifier =
          Modifier.background(colorScheme.surface)
              .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
              .testTag("QuickFixLastStep_Box")) {
        val widthRatio = maxWidth / 411
        val heightRatio = maxHeight / 860

        LazyColumn(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(horizontal = 8.dp * widthRatio.value)
                    .testTag("QuickFixLastStep_LazyColumn")) {
              item {
                workerProfile.location?.let {
                  QuickFixWorkerOverview(
                      workerProfile = workerProfile,
                      modifier =
                          Modifier.padding(
                                  vertical = 16.dp * heightRatio.value,
                                  horizontal = 12.dp * widthRatio.value)
                              .testTag("WorkerOverview"),
                      sizePP = 36.dp * heightRatio.value)
                }
              }

              item {
                Row(
                    modifier = Modifier.fillMaxWidth().testTag("QuickFixDetails_Row"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top) {
                      Column(modifier = Modifier.weight(0.3f).testTag("ServiceSection")) {
                        Row {
                          Icon(
                              imageVector = getCategoryIcon(category),
                              contentDescription = "Category icon",
                              tint = colorScheme.onSurface,
                              modifier =
                                  Modifier.size(32.dp * widthRatio.value)
                                      .padding(end = 4.dp * widthRatio.value))
                          Column(
                              verticalArrangement = Arrangement.SpaceBetween,
                          ) {
                            Text(
                                text = "Service",
                                style = poppinsTypography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = colorScheme.onSecondaryContainer)
                            Text(
                                text = workerProfile.fieldOfWork,
                                style = poppinsTypography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = colorScheme.onBackground)
                          }
                        }
                      }

                      Column(modifier = Modifier.weight(0.4f).testTag("DateTimeSection")) {
                        Row {
                          Icon(
                              imageVector = Icons.Default.AccessTime,
                              contentDescription = "Date and time icon",
                              tint = colorScheme.onSurface,
                              modifier =
                                  Modifier.size(32.dp * widthRatio.value)
                                      .padding(end = 4.dp * widthRatio.value))
                          Column(
                              verticalArrangement = Arrangement.SpaceBetween,
                          ) {
                            Text(
                                text = "Date and time",
                                style = poppinsTypography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = colorScheme.onSecondaryContainer)
                            quickFix.date.forEach {
                              Text(
                                  text =
                                      dateFormatter
                                          .format(it.toDate())
                                          .plus(" - ")
                                          .plus(timeFormatter.format(it.toDate())),
                                  style = poppinsTypography.labelSmall,
                                  fontWeight = FontWeight.Medium,
                                  fontSize = 10.sp,
                                  color = colorScheme.onBackground)
                            }
                          }
                        }
                      }

                      Column(modifier = Modifier.weight(0.3f).testTag("LocationSection")) {
                        Row {
                          Icon(
                              imageVector = Icons.Outlined.LocationOn,
                              contentDescription = "Location icon",
                              tint = colorScheme.onSurface,
                              modifier =
                                  Modifier.size(32.dp * widthRatio.value)
                                      .padding(end = 4.dp * widthRatio.value))
                          Column {
                            Text(
                                text = "Location",
                                style = poppinsTypography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = colorScheme.onSecondaryContainer)
                            Text(
                                text = quickFix.location.name,
                                style = poppinsTypography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = colorScheme.onBackground)
                          }
                        }
                      }
                    }
                HorizontalDivider(
                    modifier =
                        Modifier.padding(
                                top = 16.dp * heightRatio.value,
                                end = 8.dp * widthRatio.value,
                                start = 8.dp * widthRatio.value)
                            .testTag("QuickFixDetails_Divider"))
              }

              item {
                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = 12.dp * widthRatio.value)
                            .testTag("GeneralInformation_Column")) {
                      Text(
                          text = "General Information",
                          style = poppinsTypography.bodyMedium,
                          fontSize = 20.sp,
                          fontWeight = FontWeight.SemiBold,
                          modifier =
                              Modifier.padding(
                                      bottom = 8.dp * heightRatio.value,
                                      top = 4.dp * heightRatio.value)
                                  .offset(x = (-4).dp * widthRatio.value),
                      )

                      Text(
                          text = "TITLE",
                          style = poppinsTypography.labelSmall,
                          fontWeight = FontWeight.Medium,
                          fontSize = 10.sp,
                          color = colorScheme.onSecondaryContainer)
                      Text(
                          text = quickFix.title,
                          style = poppinsTypography.labelSmall,
                          fontWeight = FontWeight.Medium,
                          fontSize = 10.sp,
                          color = colorScheme.onBackground)

                      Spacer(modifier = Modifier.height(4.dp * heightRatio.value))

                      Text(
                          text = "Total Price",
                          style = poppinsTypography.labelSmall,
                          fontWeight = FontWeight.Medium,
                          fontSize = 10.sp,
                          color = colorScheme.onSecondaryContainer)
                      Text(
                          text = quickFix.bill.sumOf { it.total }.toString(),
                          style = poppinsTypography.labelSmall,
                          fontWeight = FontWeight.Medium,
                          fontSize = 10.sp,
                          color = colorScheme.onBackground)

                      Spacer(modifier = Modifier.height(4.dp * heightRatio.value))

                      Text(
                          text = "Services Selected",
                          style = poppinsTypography.labelSmall,
                          fontWeight = FontWeight.Medium,
                          fontSize = 10.sp,
                          color = colorScheme.onSecondaryContainer)
                      quickFix.includedServices.forEach {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                              Text(
                                  text = it.name,
                                  style = poppinsTypography.labelSmall,
                                  fontWeight = FontWeight.Medium,
                                  fontSize = 10.sp,
                                  color = colorScheme.onBackground)
                              Icon(
                                  imageVector = Icons.Default.Check,
                                  contentDescription = null,
                                  tint = colorScheme.onBackground,
                                  modifier = Modifier.size(12.dp * widthRatio.value))
                            }
                      }
                      quickFix.addOnServices.forEach {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                              Text(
                                  text = it.name,
                                  style = poppinsTypography.labelSmall,
                                  fontWeight = FontWeight.Medium,
                                  fontSize = 10.sp,
                                  color = colorScheme.primary)

                              Icon(
                                  imageVector = Icons.Default.Check,
                                  contentDescription = null,
                                  tint = colorScheme.primary,
                                  modifier = Modifier.size(12.dp * widthRatio.value))
                            }
                      }
                    }
              }

              item {
                if (quickFix.status == Status.UPCOMING) {
                  QuickFixButton(
                      buttonText = "Consult the discussion",
                      buttonColor = colorScheme.surface,
                      textStyle =
                          poppinsTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                      textColor = colorScheme.primary,
                      onClickAction = { /* navigateTo(screenChatUid)*/},
                      leadingIcon = Icons.AutoMirrored.Filled.Send,
                      leadingIconTint = colorScheme.primary,
                      modifier =
                          Modifier.padding(top = 16.dp * heightRatio.value)
                              .fillMaxWidth()
                              .testTag("ConsultDiscussionButton"))

                  QuickFixButton(
                      buttonText = "Cancel",
                      buttonColor = colorScheme.surface,
                      textColor = colorScheme.onSurface,
                      textStyle =
                          poppinsTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                      onClickAction = {
                        quickFixViewModel.updateQuickFix(
                            quickFix.copy(status = Status.CANCELED),
                            onSuccess = { Log.d("QuickFixLastStep", "QuickFix cancelled") },
                            onFailure = {
                              Log.e("QuickFixLastStep", "Error cancelling QuickFix", it)
                            })
                        /* cancelQuickFix()*/
                      },
                      modifier = Modifier.fillMaxWidth().testTag("CancelButton"))
                  var showButtonWorker by remember { mutableStateOf(mode == AppMode.WORKER) }
                  if (showButtonWorker) {
                    QuickFixButton(
                        buttonText = "Mark as completed",
                        buttonColor = colorScheme.primary,
                        textColor = colorScheme.onPrimary,
                        onClickAction = {
                          quickFixViewModel.updateQuickFix(
                              quickFix.copy(status = Status.COMPLETED),
                              onSuccess = {
                                showButtonWorker = false
                                onQuickFixChange(quickFix.copy(status = Status.COMPLETED))
                                Log.d("QuickFixLastStep", "QuickFix completed")
                              },
                              onFailure = {
                                Log.e("QuickFixLastStep", "Error completing QuickFix", it)
                              })
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp * heightRatio.value))
                  }
                }
                var showReview by remember {
                  mutableStateOf(
                      mode == AppMode.USER &&
                          (quickFix.status == Status.COMPLETED ||
                              quickFix.status == Status.CANCELED))
                }
                if (showReview) {
                  Column(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(horizontal = 12.dp * widthRatio.value)
                              .testTag("ReviewSection")) {
                        Text(
                            text = "Review",
                            style = poppinsTypography.bodyMedium,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier =
                                Modifier.padding(
                                        bottom = 8.dp * heightRatio.value,
                                        top = 16.dp * heightRatio.value)
                                    .offset(x = (-4).dp * widthRatio.value),
                        )
                        Row(
                            modifier =
                                Modifier.fillMaxWidth().padding(bottom = 8.dp * heightRatio.value),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                          RatingBar(
                              value = rating.toFloat(),
                              style =
                                  RatingBarStyle.Stroke(
                                      activeColor = colorScheme.primary,
                                      strokeColor = colorScheme.onBackground,
                                      width = 2f),
                              onValueChange = { rating = it.toDouble() },
                              onRatingChanged = { Log.d("TAG", "onRatingChanged: $it") },
                              stepSize = StepSize.HALF,
                              modifier = Modifier.testTag("RatingBar"))
                        }

                        QuickFixTextFieldCustom(
                            heightField = 150.dp * heightRatio.value,
                            widthField = 400.dp * widthRatio.value,
                            value = feedback,
                            onValueChange = { feedback = it },
                            shape = RoundedCornerShape(8.dp),
                            showLabel = true,
                            label =
                                @Composable {
                                  Text(
                                      text =
                                          buildAnnotatedString {
                                            append("Feedback")
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
                                      modifier =
                                          Modifier.padding(bottom = 8.dp, start = 4.dp, top = 4.dp))
                                },
                            hasShadow = false,
                            borderColor = colorScheme.tertiaryContainer,
                            placeHolderText = "Feedback note...",
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
                            modifier = Modifier.testTag("FeedbackTextField"))

                        QuickFixButton(
                            buttonText = "Add a review",
                            enabled = rating > 0f,
                            buttonColor = colorScheme.primary,
                            textColor = colorScheme.onPrimary,
                            onClickAction = {
                              workerViewModel.updateProfile(
                                  workerProfile.apply {
                                    reviews.add(
                                        Review(
                                            rating = rating,
                                            review = feedback,
                                            username = "Placeholder, get real username"))
                                  },
                                  onSuccess = {
                                    quickFixViewModel.updateQuickFix(
                                        quickFix.copy(status = Status.FINISHED),
                                        onSuccess = {
                                          showReview = false
                                          Log.d("QuickFixLastStep", "Worker profile updated")
                                          onQuickFixChange(quickFix.copy(status = Status.FINISHED))
                                        },
                                        onFailure = {
                                          Log.e(
                                              "QuickFixLastStep",
                                              "Error updating worker profile",
                                              it)
                                        })
                                    Log.d("QuickFixLastStep", "Worker profile updated")
                                  },
                                  onFailure = {
                                    Log.e("QuickFixLastStep", "Error updating worker profile", it)
                                  })
                            },
                            modifier =
                                Modifier.fillMaxWidth()
                                    .padding(top = 8.dp * heightRatio.value)
                                    .testTag("FinishButton"))
                      }
                }

                if (!showReview && quickFix.status == Status.FINISHED) {
                  QuickFixButton(
                      buttonText = "Go back home",
                      buttonColor = colorScheme.surface,
                      textStyle =
                          poppinsTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                      textColor = colorScheme.primary,
                      onClickAction = {
                        navigationActionsRoot.navigateTo(
                            if (mode == AppMode.USER) USER_TOP_LEVEL_DESTINATIONS[0].route
                            else WORKER_TOP_LEVEL_DESTINATIONS[0].route)
                      },
                      leadingIcon = Icons.Outlined.Home,
                      leadingIconTint = colorScheme.primary,
                      modifier =
                          Modifier.padding(top = 16.dp * heightRatio.value)
                              .fillMaxWidth()
                              .testTag("ConsultDiscussionButton"))
                }
              }
            }
      }
}
