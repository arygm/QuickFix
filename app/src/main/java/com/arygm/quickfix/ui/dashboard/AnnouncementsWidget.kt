package com.arygm.quickfix.ui.dashboard

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.R
import com.arygm.quickfix.model.search.Announcement
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen

@Composable
fun AnnouncementsWidget(
    announcementViewModel: AnnouncementViewModel,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier,
    itemsToShowDefault: Int = 2
) {
  var showAll by remember { mutableStateOf(false) }
  val announcements by announcementViewModel.announcementsForUser.collectAsState()
  val imagesForAnnouncements by announcementViewModel.announcementImagesMap.collectAsState()

  BoxWithConstraints {
    val cardWidth = maxWidth * 0.4f // 40% of the available width for each card
    val horizontalSpacing = maxWidth * 0.025f // 2.5% of the available width for spacing

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontalSpacing)
                .shadow(5.dp, RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .testTag("AnnouncementsWidget")) {
          // Header with Show All button
          Row(
              modifier = Modifier.fillMaxWidth().padding(8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Announcements",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = poppinsTypography.headlineMedium,
                    fontSize = 19.sp,
                    modifier = Modifier.testTag("AnnouncementsTitle"))
                TextButton(
                    onClick = { showAll = !showAll },
                    modifier = Modifier.testTag("ShowAllButton")) {
                      Text(
                          text = if (showAll) "Show Less" else "Show All",
                          color = MaterialTheme.colorScheme.onSurface,
                          style = MaterialTheme.typography.bodyMedium,
                          fontWeight = FontWeight.SemiBold)
                    }
              }

          Divider(
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
              thickness = 1.dp,
              modifier = Modifier.testTag("AnnouncementsDivider"))

          val itemsToShow = if (showAll) announcements else announcements.take(itemsToShowDefault)
          itemsToShow.forEachIndexed { index, announcement ->
            val pairs = imagesForAnnouncements[announcement.announcementId] ?: emptyList()
            val bitmapToDisplay = pairs.firstOrNull()?.second

            AnnouncementItem(
                announcement = announcement,
                announcementImage = bitmapToDisplay,
                onClick = {
                  announcementViewModel.selectAnnouncement(announcement)
                  navigationActions.navigateTo(UserScreen.ANNOUNCEMENT_DETAIL)
                })

            if (index < itemsToShow.size - 1) {
              Divider(
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                  thickness = 1.dp,
                  modifier = Modifier.testTag("AnnouncementDivider_$index"))
            }
          }
        }
  }
}

@Composable
fun AnnouncementItem(announcement: Announcement, announcementImage: Bitmap?, onClick: () -> Unit) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(8.dp)
              .clickable { onClick() }
              .testTag("AnnouncementItem_${announcement.announcementId}"),
      verticalAlignment = Alignment.Top) {
        // Image
        if (announcementImage != null) {
          Image(
              bitmap = announcementImage.asImageBitmap(),
              contentDescription = "Announcement Image",
              contentScale = ContentScale.Crop,
              modifier =
                  Modifier.size(40.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                      .testTag("AnnouncementImage_${announcement.announcementId}"))
        } else {
          Image(
              painter = painterResource(id = R.drawable.placeholder_worker),
              contentDescription = "Placeholder Image",
              contentScale = ContentScale.Crop,
              modifier =
                  Modifier.size(40.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                      .testTag("PlaceholderImage_${announcement.announcementId}"))
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Text content
        Column(modifier = Modifier.weight(1f)) {
          // Title and category icon row
          Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier =
                  Modifier.fillMaxWidth()
                      .testTag("AnnouncementRow_${announcement.announcementId}")) {
                Text(
                    text = announcement.title,
                    style = poppinsTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("AnnouncementTitle_${announcement.announcementId}"))

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Outlined.ElectricalServices,
                    contentDescription = "Category Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("CategoryIcon_${announcement.announcementId}"))

                Spacer(modifier = Modifier.weight(1f))

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("LocationRow_${announcement.announcementId}")) {
                      Icon(
                          imageVector = Icons.Default.LocationOn,
                          contentDescription = "Location",
                          tint = MaterialTheme.colorScheme.onSurface,
                          modifier =
                              Modifier.size(16.dp)
                                  .testTag("LocationIcon_${announcement.announcementId}"))
                      Text(
                          text = announcement.location?.name ?: "Unknown",
                          fontSize = 9.sp,
                          color = MaterialTheme.colorScheme.onSurface,
                          modifier =
                              Modifier.padding(start = 2.dp)
                                  .testTag("LocationText_${announcement.announcementId}"),
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis)
                    }
              }

          Spacer(modifier = Modifier.height(4.dp))

          // Description row
          Text(
              text = announcement.description,
              style = poppinsTypography.bodyMedium.copy(fontSize = 12.sp),
              fontWeight = FontWeight.Normal,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.testTag("AnnouncementDescription_${announcement.announcementId}"))
        }
      }
}
