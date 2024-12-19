package com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.announcements

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.category.getCategoryIcon
import com.arygm.quickfix.model.search.Announcement
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun AnnouncementCard(
    modifier: Modifier = Modifier,
    announcement: Announcement,
    announcementImage: Bitmap?,
    accountViewModel: AccountViewModel,
    categoryViewModel: CategoryViewModel,
    onClick: () -> Unit
) {
  var category by remember { mutableStateOf(Category()) }
  LaunchedEffect(Unit) {
    categoryViewModel.getCategoryBySubcategoryId(
        announcement.category,
        onSuccess = {
          if (it != null) {
            category = it
          }
        })
  }

  var account by remember { mutableStateOf<Account?>(null) }
  LaunchedEffect(announcement.userId) {
    accountViewModel.fetchUserAccount(announcement.userId) { fetchedAccount: Account? ->
      account = fetchedAccount
    }
  }

  Card(
      shape = RoundedCornerShape(8.dp),
      colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
      elevation = CardDefaults.cardElevation(10.dp),
      modifier =
          modifier
              .fillMaxWidth()
              .padding(vertical = 10.dp, horizontal = 10.dp)
              .clickable { onClick() }
              .testTag("AnnouncementCard_${announcement.announcementId}")) {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(8.dp)
                    .testTag("AnnouncementCardRow_${announcement.announcementId}"),
            verticalAlignment = Alignment.CenterVertically) {
              // Image Placeholder / Actual Image
              if (announcementImage != null) {
                Image(
                    bitmap = announcementImage.asImageBitmap(),
                    contentDescription = "Announcement Image",
                    contentScale = ContentScale.FillBounds,
                    modifier =
                        Modifier.clip(RoundedCornerShape(8.dp))
                            .size(100.dp)
                            .aspectRatio(1f)
                            .background(colorScheme.onSurface.copy(alpha = 0.1f))
                            .testTag("AnnouncementImage_${announcement.announcementId}"))
              } else {
                Box(
                    modifier =
                        Modifier.clip(RoundedCornerShape(8.dp))
                            .size(100.dp)
                            .aspectRatio(1f)
                            .background(colorScheme.onSurface.copy(alpha = 0.1f))
                            .testTag("AnnouncementImagePlaceholder_${announcement.announcementId}"),
                    contentAlignment = Alignment.Center) {
                      CircularProgressIndicator(
                          color = colorScheme.primary,
                          modifier = Modifier.testTag("Loader_${announcement.announcementId}"))
                    }
              }

              Spacer(
                  modifier =
                      Modifier.width(8.dp)
                          .testTag("SpacerBetweenImageAndContent_${announcement.announcementId}"))

              Column(
                  modifier =
                      Modifier.weight(1f)
                          .testTag("AnnouncementContentColumn_${announcement.announcementId}")) {
                    // Title Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            Modifier.testTag(
                                "AnnouncementTitleRow_${announcement.announcementId}")) {
                          Text(
                              text = announcement.title,
                              style =
                                  poppinsTypography.bodyMedium.copy(
                                      fontWeight = FontWeight.Bold, fontSize = 16.sp),
                              color = colorScheme.onBackground,
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis,
                              modifier =
                                  Modifier.testTag(
                                      "AnnouncementTitle_${announcement.announcementId}"))

                          Spacer(
                              modifier =
                                  Modifier.weight(1f)
                                      .testTag("TitleSpacer_${announcement.announcementId}"))

                          // Category Icon
                          Icon(
                              imageVector = getCategoryIcon(category),
                              contentDescription = "Category Icon",
                              tint = colorScheme.primary,
                              modifier =
                                  Modifier.size(20.dp)
                                      .testTag(
                                          "AnnouncementCategoryIcon_${announcement.announcementId}"))
                        }

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                                .testTag("SpacerAfterTitle_${announcement.announcementId}"))

                    // Description
                    Text(
                        text = announcement.description,
                        style = poppinsTypography.bodySmall.copy(fontSize = 12.sp),
                        fontWeight = FontWeight.Normal,
                        color = colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(end = 16.dp)
                                .testTag("AnnouncementDescription_${announcement.announcementId}"))

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                                .testTag("SpacerAfterDescription_${announcement.announcementId}"))

                    // Location Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            Modifier.testTag(
                                "AnnouncementLocationRow_${announcement.announcementId}")) {
                          Icon(
                              imageVector = Icons.Default.LocationOn,
                              contentDescription = "Location Icon",
                              tint = colorScheme.onSurface,
                              modifier =
                                  Modifier.size(14.dp)
                                      .testTag(
                                          "AnnouncementLocationIcon_${announcement.announcementId}"))
                          Spacer(
                              modifier =
                                  Modifier.width(4.dp)
                                      .testTag(
                                          "SpacerInLocationRow_${announcement.announcementId}"))
                          Text(
                              text = announcement.location?.name ?: "Unknown",
                              style = poppinsTypography.bodySmall.copy(fontSize = 9.sp),
                              color = colorScheme.onSurface,
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis,
                              modifier =
                                  Modifier.width(60.dp)
                                      .testTag(
                                          "AnnouncementLocation_${announcement.announcementId}"))
                          Spacer(
                              modifier =
                                  Modifier.weight(1f)
                                      .testTag(
                                          "SpacerAfterLocation_${announcement.announcementId}"))

                          // Display User Name
                          Text(
                              text =
                                  account?.let {
                                    "By ${it.firstName} ${it.lastName.firstOrNull()?.uppercase() ?: ""}."
                                  } ?: "By Unknown",
                              style = poppinsTypography.bodySmall.copy(fontSize = 9.sp),
                              color = colorScheme.onSurface,
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis,
                              modifier =
                                  Modifier.testTag(
                                      "AnnouncementUserName_${announcement.announcementId}"))
                        }
                  }
            }
      }
}
