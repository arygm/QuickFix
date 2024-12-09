package com.arygm.quickfix.ui.dashboard

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arygm.quickfix.R
import com.arygm.quickfix.model.search.Announcement
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun AnnouncementsWidget(
    announcementViewModel: AnnouncementViewModel =
        viewModel(factory = AnnouncementViewModel.Factory),
    modifier: Modifier = Modifier,
    itemsToShowDefault: Int = 2
) {
  var showAll by remember { mutableStateOf(false) }
  val announcements by announcementViewModel.announcementsForUser.collectAsState()

  BoxWithConstraints {
    val cardWidth = maxWidth * 0.4f // Customize card width as needed
    val horizontalSpacing = maxWidth * 0.025f // Horizontal spacing between items

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
                    fontSize = 19.sp)
                TextButton(onClick = { showAll = !showAll }) {
                  Text(
                      text = if (showAll) "Show Less" else "Show All",
                      color = MaterialTheme.colorScheme.onSurface,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.SemiBold)
                }
              }

          Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), thickness = 1.dp)

          val itemsToShow = if (showAll) announcements else announcements.take(itemsToShowDefault)
          itemsToShow.forEachIndexed { index, announcement ->
            announcementViewModel.fetchAnnouncementImagesAsBitmaps(announcement.announcementId)
            val bitmap = announcementViewModel.selectedAnnouncementImages.value.first()
            AnnouncementItem(
                announcement = announcement,
                announcementImage = bitmap,
                onClick = { announcementViewModel.selectAnnouncement(announcement) })

            if (index < itemsToShow.size - 1) {
              Divider(
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), thickness = 1.dp)
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
      verticalAlignment = Alignment.CenterVertically) {
        // Image
        if (announcementImage != null) {
          Image(
              bitmap = announcementImage.asImageBitmap(),
              contentDescription = "Announcement Image",
              contentScale = ContentScale.Crop,
              modifier =
                  Modifier.size(40.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)))
        } else {
          Image(
              painter = painterResource(id = R.drawable.placeholder_worker),
              contentDescription = "Placeholder Image",
              contentScale = ContentScale.Crop,
              modifier =
                  Modifier.size(40.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)))
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Text content
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = announcement.title,
                style = poppinsTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Outlined.ElectricalServices, // Replace this icon
                contentDescription = "Category Icon",
                tint = MaterialTheme.colorScheme.primary)
          }

          Text(
              text = announcement.description,
              style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis)
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Location
        Text(
            text = announcement.location?.name ?: "Unknown",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface)
      }
}
